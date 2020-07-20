package ru.wawulya.CBTicket.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import ru.wawulya.CBTicket.enums.CompCodeSysnameEnum;
import ru.wawulya.CBTicket.enums.LogLevel;
import ru.wawulya.CBTicket.enums.PropertyNameEnum;
import ru.wawulya.CBTicket.enums.TicketFieldsEnum;
import ru.wawulya.CBTicket.error.ApiError;
import ru.wawulya.CBTicket.error.BadRequestException;
import ru.wawulya.CBTicket.error.ForbiddenException;
import ru.wawulya.CBTicket.error.NotFoundException;
import ru.wawulya.CBTicket.model.*;
import ru.wawulya.CBTicket.modelCache.Properties;
import ru.wawulya.CBTicket.modelDAO.*;
import ru.wawulya.CBTicket.service.DataService;
import ru.wawulya.CBTicket.utility.Utils;

import java.sql.Timestamp;
import java.util.*;

@Slf4j
@RestController
@RequestMapping(value = "/api")
public class ApiTicketController {

    private Utils utils;
    private Properties properties;
    private DataService dataService;

    private ObjectMapper Obj = new ObjectMapper();

    @Autowired
    public ApiTicketController(DataService dataService,
                               Utils utils,
                               Properties properties) {
        this.dataService = dataService;
        this.utils = utils;
        this.properties = properties;
    }

    @PostMapping(value = "/ticket/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResult createCallbackTicket(@RequestBody String jsonString) {
        UUID sessionId = new Session().getUuid();
        String logMethod ="POST";
        String logApiUrl = "/api/ticket/add";
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);
        log.info(sessionId + " | REST Body " + jsonString);

        JsonNode jsonNode = null;

        try {
            jsonNode = Obj.readTree(jsonString);
        } catch (JsonProcessingException except) {
            log.error(sessionId + " | Error :" + except.getMessage());
            throw new BadRequestException(sessionId, logMethod, logApiUrl,except.getMessage());
        }

        String cbNumber = jsonNode.path(TicketFieldsEnum.CB_NUMBER).asText();
        if (cbNumber.isEmpty()) {
            log.error(sessionId + " | NOT get cbNumber from Json ");
            throw new BadRequestException(sessionId, logMethod, logApiUrl, "Not found mandatory attribute for callback number (cb_number)");
        } else {
            log.info(sessionId + " | Get cb_number from Json :" + cbNumber);
        }

        List<Ticket> uncompleteTickets= dataService.findAllByCbNumber(cbNumber);

        if (uncompleteTickets.size() != 0)
            throw new ForbiddenException(sessionId, logMethod, logApiUrl, "Forbidden create ticket for number [" + cbNumber + "], bacause it is exist and not in state 'Completed'");


        String cbDateFromJson = jsonNode.path(TicketFieldsEnum.CB_DATE).asText();
        Timestamp cbDateT = utils.getTimeStamp(sessionId, TicketFieldsEnum.CB_DATE, cbDateFromJson);
        if (cbDateT == null) {
            throw new BadRequestException(sessionId, logMethod, logApiUrl, "Invalid format cb_date ["+cbDateFromJson+"]. Valid format is 'yyyy-MM-dd hh:mm' or 'yyyy-MM-dd hh:mm:ss'");
        }

        String cbOriginatorFromJson = jsonNode.path(TicketFieldsEnum.CB_ORIGINATOR).asText();
        log.info(sessionId + " | Get cbOriginator from Json :" + cbOriginatorFromJson);
        if (cbOriginatorFromJson.isEmpty())
            cbOriginatorFromJson = properties.getPropertyByName(PropertyNameEnum.DEFAULT_ORIGINATOR).getValue();

        String cbUrlFromJson = jsonNode.path(TicketFieldsEnum.CB_URL).asText();
        log.info(sessionId + " | Get cbUrl from Json :" + cbUrlFromJson);

        String ucidOldFromJson = jsonNode.path(TicketFieldsEnum.CB_UCID_OLD).asText();
        log.info(sessionId + " | Get ucidOld from Json :" + ucidOldFromJson);

        String cbTypeFromJson = jsonNode.path(TicketFieldsEnum.CB_TYPE).asText();
        log.info(sessionId + " | Get cbType from Json :" + cbTypeFromJson);

        String cbSourceFromJson = jsonNode.path(TicketFieldsEnum.CB_SOURCE).asText();
        log.info(sessionId + " | Get cbSource from Json :" + cbSourceFromJson);

        int cbMaxAttemtsFromJson = jsonNode.path(TicketFieldsEnum.CB_MAX_ATTEMPTS).asInt();
        log.info(sessionId + " | Get cbMaxAttempts from Json :" + cbMaxAttemtsFromJson);
        if (cbMaxAttemtsFromJson == 0)
            cbMaxAttemtsFromJson = Integer.valueOf(properties.getPropertyByName(PropertyNameEnum.MAX_CALLBACK_ATTEMPTS).getValue());

        int cbAttemtsTimeoutFromJson = jsonNode.path(TicketFieldsEnum.CB_ATTEMPTS_TIMEOUT).asInt();
        log.info(sessionId + " | Get cbAttemptsTimeout from Json :" + cbAttemtsTimeoutFromJson);
        if (cbAttemtsTimeoutFromJson == 0)
            cbAttemtsTimeoutFromJson = Integer.valueOf(properties.getPropertyByName(PropertyNameEnum.CALLBACK_ATTEMPTS_TIMEOUT).getValue());

        CompletionCodeDAO compCodeDefault = dataService.findCompCodeDAOBySysname(CompCodeSysnameEnum.NOT_CALLED);

        TicketDAO ticketDAO = new TicketDAO(cbNumber, cbDateT, compCodeDefault);

        ticketDAO.setTicketParamsDAO(new TicketParamsDAO(ticketDAO, cbUrlFromJson, ucidOldFromJson, cbTypeFromJson, cbSourceFromJson, cbOriginatorFromJson, cbMaxAttemtsFromJson, cbAttemtsTimeoutFromJson));
        //ticketDAO = ticketRepo.save(ticketDAO);
        ticketDAO = dataService.save(ticketDAO);

        RequestResult result = new RequestResult("Success","Created ticket for callback: id ["+ticketDAO.getId()+"], number ["+ticketDAO.getCbNumber()+"], date ["+ticketDAO.getCbDate()+"]");
        dataService.saveLog(sessionId.toString(),LogLevel.INFO,logMethod,logApiUrl, jsonString, utils.createJsonStr(sessionId,result), "200 OK");

         return result;
    }

    @GetMapping(value = "/ticket/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Ticket getTicketById(@PathVariable Long id) {
        UUID sessionId = new Session().getUuid();
        String logMethod ="GET";
        String logApiUrl = "/api/ticket/" + id;
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        Ticket ticket = dataService.getTicketById(id);

        if (ticket == null)
            throw new NotFoundException(sessionId,logMethod,logApiUrl,"Ticket with id [" + id + "] not found");

        dataService.saveLog(sessionId.toString(),LogLevel.INFO, logMethod,logApiUrl, "",utils.createJsonStr(sessionId, ticket), "200 OK");
        return ticket;
    }

    @GetMapping(value = "/ticket/find", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Ticket> getTicketsByNumber(@RequestParam (name = "number") String number) {
        UUID sessionId = new Session().getUuid();
        String logMethod ="GET";
        String logApiUrl = "/api/ticket/find?number=" + number;
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        List<Ticket> tikets = dataService.getTicketsByNumber(number);

        if(tikets.size() == 0)
            throw new NotFoundException(sessionId, logMethod, logApiUrl, "Tickets with number [" + number + "] not found.");

        dataService.saveLog(sessionId.toString(),LogLevel.INFO, logMethod,logApiUrl, "",utils.createJsonStr(sessionId, tikets), "200 OK");
        return tikets;
    }

    @GetMapping(value = "/ticket/job", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Ticket> getTicketsForCallBack(@RequestParam (name = "count") int count) {
        UUID sessionId = new Session().getUuid();
        String logMethod ="GET";
        String logApiUrl = "/api/ticket/job?count=" + count;
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        Timestamp currentTime =  new Timestamp(new Date().getTime());

        List<Ticket> tikets = dataService.getTicketsForCallBack(currentTime,count);

        if (tikets.size()!= 0) {
            log.info(sessionId + " | Get callback job list");
            tikets.forEach(r -> log.debug(sessionId + " | " + r.toString()));
        }

        dataService.saveLog(sessionId.toString(),LogLevel.INFO, logMethod,logApiUrl, "",utils.createJsonStr(sessionId,tikets), "200 OK");
        return tikets;
    }

    @GetMapping(value = "/ticket/dialing", produces = MediaType.APPLICATION_JSON_VALUE)
    public Ticket getOneTicketInDialingState() {
        UUID sessionId = new Session().getUuid();
        String logMethod ="GET";
        String logApiUrl = "/api/ticket/dialing";
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        Ticket ticket = dataService.getOneTicketInDialingState();

        if (ticket == null)
            throw new NotFoundException(sessionId, logMethod, logApiUrl, "Tickets in dialing state not found.");

        dataService.saveLog(sessionId.toString(),LogLevel.INFO, logMethod,logApiUrl, "",utils.createJsonStr(sessionId,ticket), "200 OK");

        return ticket;
    }

    @PostMapping(value = "/ticket/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResult updateTicket(@RequestBody Ticket ticket) {
        UUID sessionId = new Session().getUuid();
        String logMethod ="PUT";
        String logApiUrl = "/api/ticket/update";
        log.info(sessionId + " | REST " +  logMethod + " " + logApiUrl);

        Timestamp currentTime =  new Timestamp(new Date().getTime());

        Ticket uTicket = dataService.updateTicket(ticket,currentTime);

        dataService.saveLog(sessionId.toString(),LogLevel.INFO, logMethod,logApiUrl, utils.createJsonStr(sessionId, ticket), utils.createJsonStr(sessionId, uTicket),"200 OK");
        return new RequestResult("Success","");
    }

    @PostMapping(value = "/ticket/cancel/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResult cancelTicketById(@PathVariable Long id) {
        UUID sessionId = new Session().getUuid();
        String logMethod ="PUT";
        String logApiUrl = "/api/ticket/cancel/" + id;
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        Ticket ticket = dataService.cancelTicketById(id);

        if (ticket == null)
            throw new NotFoundException(sessionId,logMethod, logApiUrl, "Ticket with id [" + id + "] not found");

        RequestResult result = new RequestResult("Success","Ticket with id ["+id+"] is canceled successfully.");
        dataService.saveLog(sessionId.toString(),LogLevel.INFO, logMethod,logApiUrl, "",utils.createJsonStr(sessionId,result), "200 OK");

        return result;
    }

    @DeleteMapping(value = "/ticket/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResult deleteTicketById(@PathVariable Long id) {
        UUID sessionId = new Session().getUuid();
        String logMethod ="DELETE";
        String logApiUrl = "/api/ticket/delete/" + id;
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        Ticket ticket = null;

        ticket = dataService.getTicketById(id);

        if (ticket == null)
            throw new NotFoundException(sessionId,logMethod,logApiUrl,"Ticket with id [" + id + "] not found");

        ticket = dataService.deleteTicketById(id);

        if (ticket == null)
            throw new ForbiddenException(sessionId, logMethod, logApiUrl, "Forbidden delete ticket with id [" + id + "], bacause it is not in state 'Finished'. Cancel ticket before delete.");

        RequestResult result = new RequestResult("Success","Ticket with id ["+id+"] is deleted successfully.");
        dataService.saveLog(sessionId.toString(),LogLevel.INFO, logMethod,logApiUrl, "",utils.createJsonStr(sessionId,result), "200 OK");

        return result;
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ApiError sendBadRequestResponse(BadRequestException except) {

        log.error(except.getSessionId()+ " | Error :" + except.getMessage());

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, except.getSessionId(), except.getMessage());
        dataService.saveLog(except.getSessionId().toString(),LogLevel.ERROR, except.getMethod(),except.getApiUrl(), "",utils.createJsonStr(except.getSessionId(), apiError), "400 BAD REQUEST");
        log.error(apiError.toString());

        return apiError;
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    private ApiError sendForbiddenResponse(ForbiddenException except) {

        log.error(except.getSessionId()+ " | Error :" + except.getMessage());

        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, except.getSessionId(), except.getMessage());
        dataService.saveLog(except.getSessionId().toString(),LogLevel.WARN, except.getMethod(),except.getApiUrl(), "",utils.createJsonStr(except.getSessionId(), apiError), "403 FORBIDDEN");
        log.error(apiError.toString());

        return apiError;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ApiError sendNotFoundResponse(NotFoundException except) {

        log.error(except.getSessionId()+ " | Error " + except.getMessage());

        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, except.getSessionId(), except.getMessage());
        dataService.saveLog(except.getSessionId().toString(),LogLevel.WARN, except.getMethod(),except.getApiUrl(), "",utils.createJsonStr(except.getSessionId(), apiError), "404 NOT FOUND");

        log.error(apiError.toString());

        return apiError;
    }

}
