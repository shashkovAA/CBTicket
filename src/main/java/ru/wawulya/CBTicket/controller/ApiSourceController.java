package ru.wawulya.CBTicket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.wawulya.CBTicket.enums.LogLevel;
import ru.wawulya.CBTicket.error.ApiError;
import ru.wawulya.CBTicket.error.BadRequestException;
import ru.wawulya.CBTicket.error.NotFoundException;
import ru.wawulya.CBTicket.model.Prompt;
import ru.wawulya.CBTicket.model.Session;
import ru.wawulya.CBTicket.model.Source;
import ru.wawulya.CBTicket.service.DataService;
import ru.wawulya.CBTicket.utility.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api")
public class ApiSourceController {

    private DataService dataService;
    private Utils utils;

    public ApiSourceController(DataService dataService, Utils utils) {
        this.dataService = dataService;
        this.utils = utils;
    }

    @GetMapping(value = "/source/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Source> getAllSources(HttpServletRequest request, HttpServletResponse response) {

        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        List<Source> sourceList = dataService.getSourceDataService().findAll();

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

        return sourceList;
    }

    @PostMapping(value = "/source/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Source addUser(HttpServletRequest request, HttpServletResponse response, @RequestBody Source source) {

        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        Source tmpSource = dataService.getSourceDataService().addSource(source);

        if (tmpSource == null)
            throw new BadRequestException(getSession().getUuid(), request.getMethod(), request.getRequestURI(), "Prompt with this name ia already exist! Check log file.");

        dataService.getLogService().saveLog(
                getSession().getUuid().toString(),
                request.getRemoteUser(),
                LogLevel.INFO,
                request.getMethod(),
                request.getRequestURI(),
                utils.createJsonStr(getSession().getUuid(),source),
                utils.createJsonStr(getSession().getUuid(),tmpSource),
                String.valueOf(response.getStatus()),
                request.getRemoteAddr());

        return tmpSource;
    }

    @PutMapping(value = "/source/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public Source updateUser(HttpServletRequest request, HttpServletResponse response,@RequestBody Source source) {

        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        Source tmpSource = dataService.getSourceDataService().updateSource(source);

        dataService.getLogService().saveLog(
                getSession().getUuid().toString(),
                request.getRemoteUser(),
                LogLevel.INFO,
                request.getMethod(),
                request.getRequestURI(),
                utils.createJsonStr(getSession().getUuid(),source),
                utils.createJsonStr(getSession().getUuid(),tmpSource),
                String.valueOf(response.getStatus()),
                request.getRemoteAddr());

        return tmpSource;
    }

    @DeleteMapping(value = "/source/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteUser(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") Long id) {

        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        dataService.getSourceDataService().deleteSource(id);

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

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ApiError sendNotFoundResponse(NotFoundException except) {

        log.error(except.getSessionId()+ " | Error " + except.getMessage());

        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, except.getSessionId(), except.getMessage());
        dataService.getLogService().saveLog(except.getSessionId().toString(),LogLevel.WARN,except.getMethod(),except.getApiUrl(), "",utils.createJsonStr(except.getSessionId(), apiError), "404");
        log.error(apiError.toString());

        return apiError;
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

    @Lookup
    public Session getSession() {
        return null;
    }
}
