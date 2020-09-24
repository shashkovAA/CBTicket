package ru.wawulya.CBTicket.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    public RequestResult createCallbackTicket(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonString) {

        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());
        log.debug(getSession().getUuid() + " | REST Body " + jsonString);

        JsonNode jsonNode = null;

        try {
            jsonNode = Obj.readTree(jsonString);
        } catch (JsonProcessingException except) {
            log.error(getSession().getUuid() + " | Error :" + except.getMessage());
            throw new BadRequestException(getSession().getUuid(), request.getMethod(), request.getRequestURI(),except.getMessage());
        }

        String cbNumber = jsonNode.path(TicketFieldsEnum.CB_NUMBER).asText();
        if (cbNumber.isEmpty()) {
            log.error(getSession().getUuid() + " | NOT get cbNumber from Json ");
            throw new BadRequestException(getSession().getUuid(), request.getMethod(), request.getRequestURI(), "Not found mandatory attribute for callback number (cb_number)");
        } else {
            log.info(getSession().getUuid() + " | Get cb_number from Json :" + cbNumber);
        }

        List<Ticket> uncompleteTickets= dataService.getTicketDataService().findAllByCbNumber(cbNumber);

        if (uncompleteTickets.size() != 0)
            throw new ForbiddenException(getSession().getUuid(), request.getMethod(), request.getRequestURI(), "Forbidden create ticket for number [" + cbNumber + "], bacause it is exist and not in state 'Completed'");


        String cbDateFromJson = jsonNode.path(TicketFieldsEnum.CB_DATE).asText();
        Timestamp cbDateT = utils.getTimeStamp(getSession().getUuid(), TicketFieldsEnum.CB_DATE, cbDateFromJson);
        if (cbDateT == null) {
            throw new BadRequestException(getSession().getUuid(), request.getMethod(), request.getRequestURI(), "Invalid format cb_date ["+cbDateFromJson+"]. Valid format is 'yyyy-MM-dd hh:mm' or 'yyyy-MM-dd hh:mm:ss'");
        }

        String cbOriginatorFromJson = jsonNode.path(TicketFieldsEnum.CB_ORIGINATOR).asText();
        log.info(getSession().getUuid() + " | Get cbOriginator from Json :" + cbOriginatorFromJson);
        if (cbOriginatorFromJson.isEmpty())
            cbOriginatorFromJson = properties.getPropertyByName(PropertyNameEnum.DEFAULT_ORIGINATOR).getValue();

        String cbUrlFromJson = jsonNode.path(TicketFieldsEnum.CB_URL).asText();
        log.info(getSession().getUuid() + " | Get cbUrl from Json :" + cbUrlFromJson);

        String cbSourceFromJson = jsonNode.path(TicketFieldsEnum.CB_SOURCE).asText();
        log.info(getSession().getUuid() + " | Get cbSource from Json :" + cbSourceFromJson);

        SourceDAO sourceDAO = dataService.getSourceDataService().findByNameAndUrlOrDefault(cbSourceFromJson, cbUrlFromJson);

        String ucidOldFromJson = jsonNode.path(TicketFieldsEnum.CB_UCID_OLD).asText();
        log.info(getSession().getUuid() + " | Get ucidOld from Json :" + ucidOldFromJson);

        String cbTypeFromJson = jsonNode.path(TicketFieldsEnum.CB_TYPE).asText();
        log.info(getSession().getUuid() + " | Get cbType from Json :" + cbTypeFromJson);


        int cbMaxAttemtsFromJson = jsonNode.path(TicketFieldsEnum.CB_MAX_ATTEMPTS).asInt();
        log.info(getSession().getUuid() + " | Get cbMaxAttempts from Json :" + cbMaxAttemtsFromJson);
        if (cbMaxAttemtsFromJson == 0)
            cbMaxAttemtsFromJson = Integer.valueOf(properties.getPropertyByName(PropertyNameEnum.MAX_CALLBACK_ATTEMPTS).getValue());

        int cbAttemtsTimeoutFromJson = jsonNode.path(TicketFieldsEnum.CB_ATTEMPTS_TIMEOUT).asInt();
        log.info(getSession().getUuid() + " | Get cbAttemptsTimeout from Json :" + cbAttemtsTimeoutFromJson);
        if (cbAttemtsTimeoutFromJson == 0)
            cbAttemtsTimeoutFromJson = Integer.valueOf(properties.getPropertyByName(PropertyNameEnum.CALLBACK_ATTEMPTS_TIMEOUT).getValue());

        CompletionCodeDAO compCodeDefault = dataService.getCompCodeDataService().findCompCodeDAOBySysname(CompCodeSysnameEnum.NOT_CALLED);

        TicketDAO ticketDAO = new TicketDAO(cbNumber, cbDateT, compCodeDefault);

        ticketDAO.setTicketParamsDAO(new TicketParamsDAO(ticketDAO, cbUrlFromJson, ucidOldFromJson, cbTypeFromJson, cbSourceFromJson, sourceDAO, cbOriginatorFromJson, cbMaxAttemtsFromJson, cbAttemtsTimeoutFromJson));

        ticketDAO = dataService.getTicketDataService().save(ticketDAO);

        RequestResult result = new RequestResult("Success","Created ticket for callback: id ["+ticketDAO.getId()+"], number ["+ticketDAO.getCbNumber()+"], date ["+ticketDAO.getCbDate()+"]");

        dataService.getLogService().saveLog(
                getSession().getUuid().toString(),
                request.getRemoteUser(),
                LogLevel.INFO,
                request.getMethod(),
                request.getRequestURI(),
                jsonString,
                utils.createJsonStr(getSession().getUuid(),result),
                String.valueOf(response.getStatus()),
                request.getRemoteAddr());
         return result;
    }
    //Тестовый метод
    @PostMapping(value = "/ticket/add/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResult createCallbackTicket2(HttpServletRequest request, HttpServletResponse response, @RequestBody Ticket ticket) {

        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        RequestResult result = new RequestResult("Success","Created ticket for callback: ");

        dataService.getLogService().saveLog(
                getSession().getUuid().toString(),
                request.getRemoteUser(),
                LogLevel.INFO,
                request.getMethod(),
                request.getRequestURI(),
                "",
                utils.createJsonStr(getSession().getUuid(),result),
                String.valueOf(response.getStatus()),
                request.getRemoteAddr());
        return result;
    }

    @GetMapping(value = "/ticket/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Ticket getTicketById(HttpServletRequest request, HttpServletResponse response, @PathVariable Long id) {

        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        Ticket ticket = dataService.getTicketDataService().getTicketById(id);

        if (ticket == null)
            throw new NotFoundException(getSession().getUuid(),request.getMethod(),request.getRequestURI(),"Ticket with id [" + id + "] not found");

        dataService.getLogService().saveLog(
                getSession().getUuid().toString(),
                request.getRemoteUser(),
                LogLevel.INFO,
                request.getMethod(),
                request.getRequestURI(),
                "",
                utils.createJsonStr(getSession().getUuid(),ticket),
                String.valueOf(response.getStatus()),
                request.getRemoteAddr());

        return ticket;
    }

    @GetMapping(value = "/ticket/find", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Ticket> getTicketsByNumber(HttpServletRequest request, HttpServletResponse response, @RequestParam (name = "number") String number) {

        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        List<Ticket> tikets = dataService.getTicketDataService().getTicketsByNumber(number);

        if(tikets.size() == 0)
            throw new NotFoundException(getSession().getUuid(), request.getMethod(), request.getRequestURI(), "Tickets with number [" + number + "] not found.");

        dataService.getLogService().saveLog(
                getSession().getUuid().toString(),
                request.getRemoteUser(),
                LogLevel.INFO,
                request.getMethod(),
                request.getRequestURI(),
                "",
                utils.createJsonStr(getSession().getUuid(),tikets),
                String.valueOf(response.getStatus()),
                request.getRemoteAddr());

        return tikets;
    }

    @GetMapping(value = "/ticket/job", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Ticket> getTicketsForCallBack(HttpServletRequest request, HttpServletResponse response, @RequestParam (name = "count") int count) {

        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        Timestamp currentTime =  new Timestamp(new Date().getTime());

        List<Ticket> tikets = dataService.getTicketDataService().getTicketsForCallBack(currentTime,count);

        if (tikets.size()!= 0) {
            log.info(getSession().getUuid() + " | Get callback job list");
            tikets.forEach(r -> log.debug(getSession().getUuid() + " | " + r.toString()));
        }

        dataService.getLogService().saveLog(
                getSession().getUuid().toString(),
                request.getRemoteUser(),
                LogLevel.INFO,
                request.getMethod(),
                request.getRequestURI(),
                "",
                utils.createJsonStr(getSession().getUuid(),tikets),
                String.valueOf(response.getStatus()),
                request.getRemoteAddr());

        return tikets;
    }

    @GetMapping(value = "/ticket/dialing", produces = MediaType.APPLICATION_JSON_VALUE)
    public Ticket getOneTicketInDialingState() {
        UUID sessionId = getSession().getUuid();
        String logMethod ="GET";
        String logApiUrl = "/api/ticket/dialing";
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        Ticket ticket = dataService.getTicketDataService().getOneTicketInDialingState();

        if (ticket == null)
            throw new NotFoundException(sessionId, logMethod, logApiUrl, "Tickets in dialing state not found.");

        dataService.getLogService().saveLog(sessionId.toString(),LogLevel.INFO, logMethod,logApiUrl, "",utils.createJsonStr(sessionId,ticket), "200 OK");

        return ticket;
    }

    @PostMapping(value = "/ticket/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResult updateTicket(HttpServletRequest request, HttpServletResponse response, @RequestBody Ticket ticket) {

        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        Timestamp currentTime =  new Timestamp(new Date().getTime());

        Ticket uTicket = dataService.getTicketDataService().updateTicket(ticket,currentTime);

        dataService.getLogService().saveLog(
                getSession().getUuid().toString(),
                request.getRemoteUser(),
                LogLevel.INFO,
                request.getMethod(),
                request.getRequestURI(),
                utils.createJsonStr(getSession().getUuid(), ticket),
                utils.createJsonStr(getSession().getUuid(), uTicket),
                String.valueOf(response.getStatus()),
                request.getRemoteAddr());

        return new RequestResult("Success","");
    }

    @PostMapping(value = "/ticket/cancel/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResult cancelTicketById(HttpServletRequest request, HttpServletResponse response, @PathVariable Long id) {

        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        Ticket ticket = dataService.getTicketDataService().cancelTicketById(id);

        if (ticket == null)
            throw new NotFoundException(getSession().getUuid(),request.getMethod(), request.getRequestURI(), "Ticket with id [" + id + "] not found");

        RequestResult result = new RequestResult("Success","Ticket with id ["+id+"] is canceled successfully.");

        dataService.getLogService().saveLog(
                getSession().getUuid().toString(),
                request.getRemoteUser(),
                LogLevel.INFO,
                request.getMethod(),
                request.getRequestURI(),
                "",
                utils.createJsonStr(getSession().getUuid(), result),
                String.valueOf(response.getStatus()),
                request.getRemoteAddr());

        return result;
    }

    @DeleteMapping(value = "/ticket/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResult deleteTicketById(HttpServletRequest request, HttpServletResponse response, @PathVariable Long id) {

        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        Ticket ticket = null;

        ticket = dataService.getTicketDataService().getTicketById(id);

        if (ticket == null)
            throw new NotFoundException(getSession().getUuid(),request.getMethod(),request.getRequestURI(),"Ticket with id [" + id + "] not found");

        ticket = dataService.getTicketDataService().deleteTicketById(id);

        if (ticket == null)
            throw new ForbiddenException(getSession().getUuid(), request.getMethod(), request.getRequestURI(), "Forbidden delete ticket with id [" + id + "], bacause it is not in state 'Finished'. Cancel ticket before delete.");

        RequestResult result = new RequestResult("Success","Ticket with id ["+id+"] is deleted successfully.");

        dataService.getLogService().saveLog(
                getSession().getUuid().toString(),
                request.getRemoteUser(),
                LogLevel.INFO,
                request.getMethod(),
                request.getRequestURI(),
                "",
                utils.createJsonStr(getSession().getUuid(), result),
                String.valueOf(response.getStatus()),
                request.getRemoteAddr());
        return result;
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ApiError sendBadRequestResponse(BadRequestException except) {

        log.error(except.getSessionId()+ " | Error :" + except.getMessage());

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, except.getSessionId(), except.getMessage());
        dataService.getLogService().saveLog(except.getSessionId().toString(),LogLevel.ERROR, except.getMethod(),except.getApiUrl(), "",utils.createJsonStr(except.getSessionId(), apiError), "400");
        log.error(apiError.toString());

        return apiError;
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    private ApiError sendForbiddenResponse(ForbiddenException except) {

        log.error(except.getSessionId()+ " | Error :" + except.getMessage());

        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, except.getSessionId(), except.getMessage());
        dataService.getLogService().saveLog(except.getSessionId().toString(),LogLevel.WARN, except.getMethod(),except.getApiUrl(), "",utils.createJsonStr(except.getSessionId(), apiError), "403");
        log.error(apiError.toString());

        return apiError;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ApiError sendNotFoundResponse(NotFoundException except) {

        log.error(except.getSessionId()+ " | Error " + except.getMessage());

        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, except.getSessionId(), except.getMessage());
        dataService.getLogService().saveLog(except.getSessionId().toString(),LogLevel.WARN, except.getMethod(),except.getApiUrl(), "",utils.createJsonStr(except.getSessionId(), apiError), "404");

        log.error(apiError.toString());

        return apiError;
    }

    @Lookup
    public Session getSession() {
        return null;
    }

}
