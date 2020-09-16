package ru.wawulya.CBTicket.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.wawulya.CBTicket.enums.CompCodeFieldsEnum;
import ru.wawulya.CBTicket.enums.LogLevel;
import ru.wawulya.CBTicket.enums.PropertyNameEnum;
import ru.wawulya.CBTicket.model.CompletionCode;
import ru.wawulya.CBTicket.model.Session;
import ru.wawulya.CBTicket.modelCache.Properties;
import ru.wawulya.CBTicket.service.DataService;
import ru.wawulya.CBTicket.utility.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/api")
public class ApiCompletionCodeController {

    private DataService dataService;
    private Utils utils;
    private Properties properties;

    @Autowired
    public ApiCompletionCodeController (DataService dataService, Utils utils,  Properties properties) {
        this.dataService = dataService;
        this.utils = utils;
        this.properties = properties;
    }

    @GetMapping(value = "/compcode/fetch", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletionCode getCompCodeBySysName(HttpServletRequest request, HttpServletResponse response, @RequestParam(name = "sysname") String sysname) {

        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        CompletionCode completionCode = dataService.getCompCodeDataService().getOrCreateCompCode(sysname);

        dataService.getLogService().saveLog(
                getSession().getUuid().toString(),
                request.getRemoteUser(),
                LogLevel.INFO,
                request.getMethod(),
                request.getRequestURI(),
                "",
                utils.createJsonStr(getSession().getUuid(), completionCode),
                String.valueOf(response.getStatus()),
                request.getRemoteAddr());

        return completionCode;
    }

    @GetMapping(value = "/compcode/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CompletionCode> getAll(HttpServletRequest request, HttpServletResponse response) {

        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        List<CompletionCode> completionCodeList = dataService.getCompCodeDataService().findAllCompCodes();

        dataService.getLogService().saveLog(
                getSession().getUuid().toString(),
                request.getRemoteUser(),
                LogLevel.INFO,
                request.getMethod(),
                request.getRequestURI(),
                "",
                utils.createJsonStr(getSession().getUuid(), completionCodeList),
                String.valueOf(response.getStatus()),
                request.getRemoteAddr());

        return completionCodeList;
    }

    @GetMapping("/compcode/export")
    public void exportCSV(HttpServletRequest request, HttpServletResponse response, Authentication auth) throws Exception {

        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        String filename = "compcode.csv";

        response.setContentType("text/csv;charset=UTF8");

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"");

        List<CompletionCode> list = dataService.getCompCodeDataService().findAllCompCodes();

        String delimiterStr = properties.getPropertyByName(PropertyNameEnum.EXPORT_DATA_DELIMITER).getValue();
        char delimiter = delimiterStr.replace("\"", "").replace("\'", "").charAt(0);

        try (CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.newFormat(delimiter).withRecordSeparator('\n').withHeader(
                CompCodeFieldsEnum.ID,
                CompCodeFieldsEnum.NAME,
                CompCodeFieldsEnum.SYSNAME,
                CompCodeFieldsEnum.DESCRIPTION,
                CompCodeFieldsEnum.RECALL))) {
            for (CompletionCode compCode : list) {
                log.debug(compCode.toString());
                csvPrinter.printRecord(Arrays.asList(compCode.getId(), compCode.getName(), compCode.getSysname(), compCode.getDescription(), compCode.isRecall()));
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
        dataService.getLogService().saveLog(
                getSession().getUuid().toString(),
                request.getRemoteUser(),
                LogLevel.INFO,
                request.getMethod(),
                request.getRequestURI(),
                "",
                "",
                String.valueOf(response.getStatus()),
                request.getRemoteAddr());
    }

    @Lookup
    public Session getSession() {
        return null;
    }

}
