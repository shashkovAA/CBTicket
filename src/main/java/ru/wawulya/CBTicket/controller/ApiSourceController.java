package ru.wawulya.CBTicket.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.wawulya.CBTicket.enums.LogLevel;
import ru.wawulya.CBTicket.enums.PropertyNameEnum;
import ru.wawulya.CBTicket.error.BadRequestException;
import ru.wawulya.CBTicket.error.NotFoundExceptionOld;
import ru.wawulya.CBTicket.model.Session;
import ru.wawulya.CBTicket.model.Source;
import ru.wawulya.CBTicket.modelCache.Properties;
import ru.wawulya.CBTicket.service.DataService;
import ru.wawulya.CBTicket.utility.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api")
public class ApiSourceController {

    private DataService dataService;
    private Properties properties;

    public ApiSourceController(DataService dataService, Properties properties) {
        this.dataService = dataService;
        this.properties = properties;
    }

    @GetMapping(value = "/source/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Source> getAllSources(HttpServletRequest request, HttpServletResponse response) throws InterruptedException {

        return dataService.getSourceDataService().findAll();
    }

    @PostMapping(value = "/source/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Source addUser(HttpServletRequest request, HttpServletResponse response, @RequestBody Source source) {

        Source tmpSource = dataService.getSourceDataService().addSource(source);

        if (tmpSource == null)
            throw new BadRequestException("Prompt with this name ia already exist! Check log file.");

        return tmpSource;
    }

    @PutMapping(value = "/source/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public Source updateUser(HttpServletRequest request, HttpServletResponse response,@RequestBody Source source) {

        return dataService.getSourceDataService().updateSource(source);
    }

    @DeleteMapping(value = "/source/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteUser(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") Long id) {

        dataService.getSourceDataService().deleteSource(id);

    }

    @GetMapping("/source/export")
    public void exportCSV(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String filename = "sources.csv";

        response.setContentType("text/csv;charset=UTF8");

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"");

        List<Source> list = dataService.getSourceDataService().findAll();

        String delimiterStr = properties.getPropertyByName(PropertyNameEnum.EXPORT_DATA_DELIMITER).getValue();
        char delimiter = delimiterStr.replace("\"", "").replace("\'", "").charAt(0);

        try (CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.newFormat(delimiter).withRecordSeparator('\n').withHeader(
                "id",
                "name",
                "url",
                "skipid",
                "prompt_name",
                "description"))) {
            for (Source source : list) {
                log.debug(source.toString());
                csvPrinter.printRecord(Arrays.asList(source.getId(), source.getName(), source.getUrl(), source.getSkpid(), source.getPrompt().getName(),source.getDescription()));
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

   /* @ExceptionHandler(NotFoundExceptionOld.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ApiErrorOld sendNotFoundResponse(NotFoundExceptionOld except) {

        log.error(except.getSessionId()+ " | Error " + except.getMessage());

        ApiErrorOld apiError = new ApiErrorOld(HttpStatus.NOT_FOUND, except.getSessionId(), except.getMessage());
        dataService.getLogService().saveLog(except.getSessionId().toString(),LogLevel.WARN,except.getMethod(),except.getApiUrl(), "",utils.createJsonStr(except.getSessionId(), apiError), "404");
        log.error(apiError.toString());

        return apiError;
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ApiErrorOld sendBadRequestResponse(BadRequestException except) {

        log.error(except.getSessionId()+ " | Error :" + except.getMessage());

        ApiErrorOld apiError = new ApiErrorOld(HttpStatus.BAD_REQUEST, except.getSessionId(), except.getMessage());
        dataService.getLogService().saveLog(except.getSessionId().toString(),LogLevel.ERROR, except.getMethod(),except.getApiUrl(), "",utils.createJsonStr(except.getSessionId(), apiError), "400");
        log.error(apiError.toString());

        return apiError;
    }

    @Lookup
    public Session getSession() {
        return null;
    }*/
}
