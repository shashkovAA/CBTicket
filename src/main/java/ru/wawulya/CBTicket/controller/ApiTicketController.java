package ru.wawulya.CBTicket.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import ru.wawulya.CBTicket.enums.CompCodeSysnameEnum;
import ru.wawulya.CBTicket.enums.LogLevel;
import ru.wawulya.CBTicket.enums.PropertyNameEnum;
import ru.wawulya.CBTicket.enums.TicketFieldsEnum;
import ru.wawulya.CBTicket.error.*;
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
        String sessionId = request.getRequestedSessionId();

        JsonNode jsonNode = null;

        try {
            jsonNode = Obj.readTree(jsonString);
        } catch (JsonProcessingException except) {
            log.error(sessionId + " | Error :" + except.getMessage());
            throw new BadRequestException(except.getMessage());
        }

        String cbNumber = jsonNode.path(TicketFieldsEnum.CB_NUMBER).asText();
        if (cbNumber.isEmpty()) {
            log.error(sessionId + " | NOT get cbNumber from Json ");
            throw new BadRequestException("Not found mandatory attribute for callback number (cb_number)");
        } else {
            log.info(sessionId + " | Get cb_number from Json :" + cbNumber);
        }

        List<Ticket> uncompleteTickets= dataService.getTicketDataService().findAllByCbNumber(cbNumber);

        if (uncompleteTickets.size() != 0)
            throw new ForbiddenException("Forbidden create ticket for number [" + cbNumber + "], bacause it is exist and not in state 'Completed'");


        String cbDateFromJson = jsonNode.path(TicketFieldsEnum.CB_DATE).asText();
        Timestamp cbDateT = utils.getTimeStamp(sessionId, TicketFieldsEnum.CB_DATE, cbDateFromJson);
        if (cbDateT == null) {
            throw new BadRequestException("Invalid format cb_date ["+cbDateFromJson+"]. Valid format is 'yyyy-MM-dd hh:mm' or 'yyyy-MM-dd hh:mm:ss'");
        }

        String cbOriginatorFromJson = jsonNode.path(TicketFieldsEnum.CB_ORIGINATOR).asText();
        log.info(sessionId + " | Get cbOriginator from Json :" + cbOriginatorFromJson);
        if (cbOriginatorFromJson.isEmpty())
            cbOriginatorFromJson = properties.getPropertyByName(PropertyNameEnum.DEFAULT_ORIGINATOR).getValue();

        String cbUrlFromJson = jsonNode.path(TicketFieldsEnum.CB_URL).asText();
        log.info(sessionId + " | Get cbUrl from Json :" + cbUrlFromJson);

        String cbSourceFromJson = jsonNode.path(TicketFieldsEnum.CB_SOURCE).asText();
        log.info(sessionId + " | Get cbSource from Json :" + cbSourceFromJson);

        SourceDAO sourceDAO = dataService.getSourceDataService().findByNameAndUrlOrDefault(cbSourceFromJson, cbUrlFromJson);

        String ucidOldFromJson = jsonNode.path(TicketFieldsEnum.CB_UCID_OLD).asText();
        log.info(sessionId + " | Get ucidOld from Json :" + ucidOldFromJson);

        String cbTypeFromJson = jsonNode.path(TicketFieldsEnum.CB_TYPE).asText();
        log.info(sessionId + " | Get cbType from Json :" + cbTypeFromJson);


        int cbMaxAttemtsFromJson = jsonNode.path(TicketFieldsEnum.CB_MAX_ATTEMPTS).asInt();
        log.info(sessionId + " | Get cbMaxAttempts from Json :" + cbMaxAttemtsFromJson);
        if (cbMaxAttemtsFromJson == 0)
            cbMaxAttemtsFromJson = Integer.valueOf(properties.getPropertyByName(PropertyNameEnum.MAX_CALLBACK_ATTEMPTS).getValue());

        int cbAttemtsTimeoutFromJson = jsonNode.path(TicketFieldsEnum.CB_ATTEMPTS_TIMEOUT).asInt();
        log.info(sessionId + " | Get cbAttemptsTimeout from Json :" + cbAttemtsTimeoutFromJson);
        if (cbAttemtsTimeoutFromJson == 0)
            cbAttemtsTimeoutFromJson = Integer.valueOf(properties.getPropertyByName(PropertyNameEnum.CALLBACK_ATTEMPTS_TIMEOUT).getValue());

        CompletionCodeDAO compCodeDefault = dataService.getCompCodeDataService().findCompCodeDAOBySysname(CompCodeSysnameEnum.NOT_CALLED);

        TicketDAO ticketDAO = new TicketDAO(cbNumber, cbDateT, compCodeDefault);

        ticketDAO.setTicketParamsDAO(new TicketParamsDAO(ticketDAO, cbUrlFromJson, ucidOldFromJson, cbTypeFromJson, cbSourceFromJson, sourceDAO, cbOriginatorFromJson, cbMaxAttemtsFromJson, cbAttemtsTimeoutFromJson));

        ticketDAO = dataService.getTicketDataService().save(ticketDAO);

        RequestResult result = new RequestResult("Success","Created ticket for callback: id ["+ticketDAO.getId()+"], number ["+ticketDAO.getCbNumber()+"], date ["+ticketDAO.getCbDate()+"]");

         return result;
    }
    //Тестовый метод
    @PostMapping(value = "/ticket/add/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResult createCallbackTicket2(HttpServletRequest request, HttpServletResponse response, @RequestBody Ticket ticket) {

        RequestResult result = new RequestResult("Success","Created ticket for callback: ");

        return result;
    }

    @GetMapping(value = "/ticket/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Ticket getTicketById(HttpServletRequest request, HttpServletResponse response, @PathVariable Long id) {

        Ticket ticket = dataService.getTicketDataService().getTicketById(id);

        if (ticket == null)
            throw new NotFoundException("Ticket with id [" + id + "] not found");

        return ticket;
    }

    @GetMapping(value = "/ticket/find", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Ticket> getTicketsByNumber(HttpServletRequest request, HttpServletResponse response, @RequestParam (name = "number") String number) {

        List<Ticket> tikets = dataService.getTicketDataService().getTicketsByNumber(number);

        if(tikets.size() == 0)
            throw new NotFoundException("Tickets with number [" + number + "] not found.");

        return tikets;
    }

    @GetMapping(value = "/ticket/job", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Ticket> getTicketsForCallBack(HttpServletRequest request, HttpServletResponse response, @RequestParam (name = "count") int count) {

        Timestamp currentTime =  new Timestamp(new Date().getTime());

        List<Ticket> tikets = dataService.getTicketDataService().getTicketsForCallBack(currentTime,count);

        return tikets;
    }

    @GetMapping(value = "/ticket/dialing", produces = MediaType.APPLICATION_JSON_VALUE)
    public Ticket getOneTicketInDialingState() {

        Ticket ticket = dataService.getTicketDataService().getOneTicketInDialingState();

        if (ticket == null)
            throw new NotFoundException("Tickets in dialing state not found.");

        return ticket;
    }

    @PostMapping(value = "/ticket/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResult updateTicket(HttpServletRequest request, HttpServletResponse response, @RequestBody Ticket ticket) {

        Timestamp currentTime =  new Timestamp(new Date().getTime());

        Ticket uTicket = dataService.getTicketDataService().updateTicket(ticket,currentTime);

        return new RequestResult("Success","");
    }

    @PostMapping(value = "/ticket/cancel/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResult cancelTicketById(HttpServletRequest request, HttpServletResponse response, @PathVariable Long id) {

        Ticket ticket = dataService.getTicketDataService().cancelTicketById(id);

        if (ticket == null)
            throw new NotFoundException("Ticket with id [" + id + "] not found");

        RequestResult result = new RequestResult("Success","Ticket with id ["+id+"] is canceled successfully.");

        return result;
    }

    @DeleteMapping(value = "/ticket/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResult deleteTicketById(HttpServletRequest request, HttpServletResponse response, @PathVariable Long id) {

        Ticket ticket = null;

        ticket = dataService.getTicketDataService().getTicketById(id);

        if (ticket == null)
            throw new NotFoundException("Ticket with id [" + id + "] not found");

        ticket = dataService.getTicketDataService().deleteTicketById(id);

        if (ticket == null)
            throw new ForbiddenException("Forbidden delete ticket with id [" + id + "], because it is not in state 'Finished'. Cancel ticket before delete.");

        RequestResult result = new RequestResult("Success","Ticket with id ["+id+"] is deleted successfully.");

        return result;
    }

}
