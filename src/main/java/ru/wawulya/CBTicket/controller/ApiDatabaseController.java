package ru.wawulya.CBTicket.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.wawulya.CBTicket.enums.LogLevel;
import ru.wawulya.CBTicket.enums.PropertyFieldsEnum;
import ru.wawulya.CBTicket.enums.PropertyNameEnum;
import ru.wawulya.CBTicket.model.*;
import ru.wawulya.CBTicket.modelCache.Properties;
import ru.wawulya.CBTicket.modelDAO.ApiLogDAO;
import ru.wawulya.CBTicket.modelDAO.AttemptDAO;
import ru.wawulya.CBTicket.modelDAO.TicketDAO;
import ru.wawulya.CBTicket.modelDAO.TicketParamsDAO;
import ru.wawulya.CBTicket.service.DataService;
import ru.wawulya.CBTicket.utility.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping(value = "/api/database")
public class ApiDatabaseController {


    private DataService dataService;
    private Properties properties;

    @Autowired
    public ApiDatabaseController(DataService dataService, Properties properties) {
        this.dataService = dataService;
        this.properties = properties;
    }

    @PostMapping(value = "/apilogs", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<ApiLog> getApiLogs(HttpServletRequest request, HttpServletResponse response, @RequestBody ApiLogSearch apiLogSearch) {

        int currentPage = apiLogSearch.getPage();
        int pageSize = apiLogSearch.getSize();

        Specification<ApiLogDAO> spec = new ApiLogSpecification(apiLogSearch);
        Page<ApiLog> page = dataService.getLogService().findAll(spec, PageRequest.of(currentPage, pageSize));

        return page;
    }

    @PostMapping(value = "/apilogs/export", produces = MediaType.APPLICATION_JSON_VALUE)
    public void exportApiLogCSV(HttpServletRequest request, HttpServletResponse response, @RequestBody ApiLogSearch apiLogSearch) throws Exception {

        String filename = "apilog.csv";

        response.setContentType("text/csv;charset=UTF8");

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"");

        Specification spec = new ApiLogSpecification(apiLogSearch);
        List<ApiLog> list = dataService.getLogService().findAll(spec);

        String delimiterStr = properties.getPropertyByName(PropertyNameEnum.EXPORT_DATA_DELIMITER).getValue();
        char delimiter = delimiterStr.replace("\"", "").replace("\'", "").charAt(0);

        try (CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.newFormat(delimiter).withRecordSeparator('\n').withHeader(
                "date",
                "level",
                "username",
                "method",
                "apiUrl",
                "requestBody",
                "responseBody",
                "statusCode",
                "host",
                "sessionId"
                ))) {
            for (ApiLog rec : list) {
                log.debug(rec.toString());
                csvPrinter.printRecord(Arrays.asList(
                        rec.getDate(),
                        rec.getLevel(),
                        rec.getUsername(),
                        rec.getMethod(),
                        rec.getApiUrl(),
                        rec.getRequestBody(),
                        rec.getResponseBody(),
                        rec.getStatusCode(),
                        rec.getHost(),
                        rec.getSessionId()));
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @PostMapping(value = "/attempts", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<Attempt> getAttempts(HttpServletRequest request, HttpServletResponse response, @RequestBody AttemptFilter attemptFilter) {

        int currentPage = attemptFilter.getPage();
        int pageSize = attemptFilter.getSize();

        Specification<AttemptDAO> spec = new AttemptSpecification(attemptFilter);
        Page<Attempt> page = dataService.getAttemptDataService().findAll(spec, PageRequest.of(currentPage, pageSize));

        return page;
    }

    @PostMapping(value = "/attempts/export", produces = MediaType.APPLICATION_JSON_VALUE)
    public void exportAttemptsCSV(HttpServletRequest request, HttpServletResponse response, @RequestBody AttemptFilter attemptFilter) throws Exception {

         String filename = "attempts.csv";

        response.setContentType("text/csv;charset=UTF8");

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"");

        Specification spec = new AttemptSpecification(attemptFilter);
        List<Attempt> list = dataService.getAttemptDataService().findAll(spec);

        String delimiterStr = properties.getPropertyByName(PropertyNameEnum.EXPORT_DATA_DELIMITER).getValue();
        char delimiter = delimiterStr.replace("\"", "").replace("\'", "").charAt(0);

        try (CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.newFormat(delimiter).withRecordSeparator('\n').withHeader(
                "id",
                "ticketId",
                "startDate",
                "endDate",
                "callId",
                "ucid",
                "operatorNumber",
                "phantomNumber",
                "comcodeId"
        ))) {
            for (Attempt rec : list) {
                log.debug(rec.toString());
                csvPrinter.printRecord(Arrays.asList(
                        rec.getId(),
                        rec.getTicketId(),
                        rec.getAttemptStart(),
                        rec.getAttemptStop(),
                        rec.getCallId(),
                        rec.getUcid(),
                        rec.getOperatorNumber(),
                        rec.getPhantomNumber(),
                        rec.getCompletionCodeId()));
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    @PostMapping(value = "/tickets", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<Ticket> getTickets(HttpServletRequest request, HttpServletResponse response, @RequestBody TicketFilter ticketFilter) {

        int currentPage = ticketFilter.getPage();
        int pageSize = ticketFilter.getSize();

        Specification<TicketDAO> spec = new TicketSpecification(ticketFilter);
        Page<Ticket> page = dataService.getTicketDataService().findAll(spec, PageRequest.of(currentPage, pageSize));

        return page;
    }

    @PostMapping(value = "/tickets/export", produces = MediaType.APPLICATION_JSON_VALUE)
    public void exportTicketsCSV(HttpServletRequest request, HttpServletResponse response, @RequestBody TicketFilter ticketFilter) throws Exception {

        String filename = "tickets.csv";

        response.setContentType("text/csv;charset=UTF8");

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"");

        Specification<TicketDAO> spec = new TicketSpecification(ticketFilter);
        List<Ticket> list = dataService.getTicketDataService().findAll(spec);

        String delimiterStr = properties.getPropertyByName(PropertyNameEnum.EXPORT_DATA_DELIMITER).getValue();
        char delimiter = delimiterStr.replace("\"", "").replace("\'", "").charAt(0);

        try (CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.newFormat(delimiter).withRecordSeparator('\n').withHeader(
                "id",
                "cbNumber",
                "createDate",
                "cbDate",
                "attemptCount",
                "completionCodeName",
                "finished"
        ))) {
            for (Ticket rec : list) {
                log.debug(rec.toString());
                csvPrinter.printRecord(Arrays.asList(
                        rec.getId(),
                        rec.getCbNumber(),
                        rec.getCreateDate(),
                        rec.getCbDate(),
                        rec.getAttemptCount(),
                        rec.getLastCompletionCode().getName(),
                        rec.isFinished()));
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    @PostMapping(value = "/ticketparams", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<TicketParams> getTickets(HttpServletRequest request, HttpServletResponse response, @RequestBody TicketParamsFilter ticketParamsFilter) {

        int currentPage = ticketParamsFilter.getPage();
        int pageSize = ticketParamsFilter.getSize();

        Specification<TicketParamsDAO> spec = new TicketParamsSpecification(ticketParamsFilter);
        Page<TicketParams> page = dataService.getTicketParamsDataService().findAll(spec, PageRequest.of(currentPage, pageSize));

        return page;
    }

    @PostMapping(value = "/ticketparams/export", produces = MediaType.APPLICATION_JSON_VALUE)
    public void exportTicketParamsCSV(HttpServletRequest request, HttpServletResponse response, @RequestBody TicketParamsFilter ticketParamsFilter) throws Exception {

         String filename = "ticketparams.csv";

        response.setContentType("text/csv;charset=UTF8");

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"");

        Specification<TicketParamsDAO> spec = new TicketParamsSpecification(ticketParamsFilter);
        List<TicketParams> list = dataService.getTicketParamsDataService().findAll(spec);

        String delimiterStr = properties.getPropertyByName(PropertyNameEnum.EXPORT_DATA_DELIMITER).getValue();
        char delimiter = delimiterStr.replace("\"", "").replace("\'", "").charAt(0);

        try (CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.newFormat(delimiter).withRecordSeparator('\n').withHeader(
                "id",
                "ticketId",
                "cbMaxAttempts",
                "cbAttemptsTimeout",
                "cbSource",
                "cbType",
                "cbOriginator",
                "cbUrl",
                "ucidOld"
        ))) {
            for (TicketParams rec : list) {
                log.debug(rec.toString());
                csvPrinter.printRecord(Arrays.asList(
                        rec.getId(),
                        rec.getTicketId(),
                        rec.getCbMaxAttempts(),
                        rec.getCbAttemptsTimeout(),
                        rec.getCbSource(),
                        rec.getCbType(),
                        rec.getCbOriginator(),
                        rec.getCbUrl(),
                        rec.getUcidOld()));
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<User> getUsers(HttpServletRequest request, HttpServletResponse response) {

        return dataService.getUserService().getAllUsers();

    }

}
