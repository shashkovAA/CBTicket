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
import ru.wawulya.CBTicket.error.ForbiddenException;
import ru.wawulya.CBTicket.error.NotFoundExceptionOld;
import ru.wawulya.CBTicket.model.*;
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
public class ApiPromptsController {

    private DataService dataService;
    private Properties properties;

    public ApiPromptsController(DataService dataService, Properties properties) {
        this.dataService = dataService;
        this.properties = properties;
    }

    @GetMapping(value = "/prompt/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Prompt> getAllPrompts(HttpServletRequest request, HttpServletResponse response) {

        return dataService.getPromptDataService().findAll();
    }

    @PostMapping(value = "/prompt/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Prompt addUser(HttpServletRequest request, HttpServletResponse response, @RequestBody Prompt prompt) {

        Prompt tmpPrompt = dataService.getPromptDataService().addPrompt(prompt);

        if (tmpPrompt == null)
            throw new BadRequestException("Prompt with this name ia already exist! Check log file.");

        return tmpPrompt;
    }

    @PutMapping(value = "/prompt/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public Prompt updateUser(HttpServletRequest request, HttpServletResponse response,@RequestBody Prompt prompt) {

        return dataService.getPromptDataService().updatePrompt(prompt);
    }

    @DeleteMapping(value = "/prompt/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteUser(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") Long id) {

        Long test = dataService.getPromptDataService().deletePrompt(request.getRequestedSessionId(), id);
        if (test == 0)
            throw new ForbiddenException("Forbidden delete prompt with id [" + id + "], because it referenced by Source. Delete or change Source first.");
    }

    @GetMapping("/prompt/export")
    public void exportCSV(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String filename = "prompts.csv";

        response.setContentType("text/csv;charset=UTF8");

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"");

        List<Prompt> list = dataService.getPromptDataService().findAll();

        String delimiterStr = properties.getPropertyByName(PropertyNameEnum.EXPORT_DATA_DELIMITER).getValue();
        char delimiter = delimiterStr.replace("\"", "").replace("\'", "").charAt(0);

        try (CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.newFormat(delimiter).withRecordSeparator('\n').withHeader(
                "id",
                "name",
                "filepath",
                "filename",
                "description"))) {
            for (Prompt prompt : list) {
                csvPrinter.printRecord(Arrays.asList(prompt.getId(), prompt.getName(), prompt.getFilepath(), prompt.getFilename(), prompt.getDescription()));
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    /*@ExceptionHandler(NotFoundExceptionOld.class)
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

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    private ApiErrorOld sendForbiddenResponse(ForbiddenException except) {

        log.error(except.getSessionId()+ " | Error :" + except.getMessage());

        ApiErrorOld apiError = new ApiErrorOld(HttpStatus.FORBIDDEN, except.getSessionId(), except.getMessage());
        dataService.getLogService().saveLog(except.getSessionId().toString(),LogLevel.WARN, except.getMethod(),except.getApiUrl(), "",utils.createJsonStr(except.getSessionId(), apiError), "403");
        log.error(apiError.toString());

        return apiError;
    }

    @Lookup
    public Session getSession() {
        return null;
    }*/
}
