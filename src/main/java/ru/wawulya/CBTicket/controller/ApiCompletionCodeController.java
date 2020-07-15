package ru.wawulya.CBTicket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.wawulya.CBTicket.data.JpaApiLogtRepository;
import ru.wawulya.CBTicket.data.JpaCompletionCodeRepository;
import ru.wawulya.CBTicket.enums.LogLevel;
import ru.wawulya.CBTicket.model.CompletionCode;
import ru.wawulya.CBTicket.model.Session;
import ru.wawulya.CBTicket.model.Ticket;
import ru.wawulya.CBTicket.modelDAO.ApiLogDAO;
import ru.wawulya.CBTicket.modelDAO.CompletionCodeDAO;
import ru.wawulya.CBTicket.modelDAO.TicketDAO;
import ru.wawulya.CBTicket.service.CompCodeDataService;
import ru.wawulya.CBTicket.service.DataService;
import ru.wawulya.CBTicket.utility.Utils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/api")
public class ApiCompletionCodeController {

    private DataService dataService;
    private Utils utils;

    public ApiCompletionCodeController (DataService dataService, Utils utils) {
        this.dataService = dataService;
        this.utils = utils;
    }

    @GetMapping(value = "/compcode/fetch", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletionCode getCompCodeBySysName(@RequestParam(name = "sysname") String sysname) {
        UUID sessionId = new Session().getUuid();
        String logMethod ="GET";
        String logApiUrl = "/api/compcode/fetch?sysname=" + sysname;
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl );

        CompletionCode completionCode = dataService.getOrCreateCompCode(sysname);

        dataService.saveLog(sessionId.toString(),LogLevel.INFO,logMethod,logApiUrl, "",utils.createJsonStr(sessionId, completionCode), "200 OK");
        return completionCode;
    }

    @GetMapping(value = "/compcode/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CompletionCode> getAll() {
        UUID sessionId = new Session().getUuid();
        String logMethod ="GET";
        String logApiUrl = "/api/compcode/all";
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        List<CompletionCode> completionCodeList = dataService.getAllCompCodes();

        dataService.saveLog(sessionId.toString(),LogLevel.INFO,logMethod,logApiUrl, "",utils.createJsonStr(sessionId, completionCodeList), "200 OK");
        return completionCodeList;
    }

}
