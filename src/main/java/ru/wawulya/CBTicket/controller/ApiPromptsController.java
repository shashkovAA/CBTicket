package ru.wawulya.CBTicket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.wawulya.CBTicket.enums.LogLevel;
import ru.wawulya.CBTicket.error.ApiError;
import ru.wawulya.CBTicket.error.BadRequestException;
import ru.wawulya.CBTicket.error.ForbiddenException;
import ru.wawulya.CBTicket.error.NotFoundException;
import ru.wawulya.CBTicket.model.Prompt;
import ru.wawulya.CBTicket.model.Property;
import ru.wawulya.CBTicket.model.Session;
import ru.wawulya.CBTicket.model.User;
import ru.wawulya.CBTicket.modelCache.Properties;
import ru.wawulya.CBTicket.service.DataService;
import ru.wawulya.CBTicket.utility.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/api")
public class ApiPromptsController {

    private DataService dataService;
    private Utils utils;

    public ApiPromptsController(DataService dataService, Utils utils) {
        this.dataService = dataService;
        this.utils = utils;
    }

    @GetMapping(value = "/prompt/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Prompt> getAllPrompts(HttpServletRequest request, HttpServletResponse response) {

        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        List<Prompt> promptList = dataService.getPromptDataService().findAll();

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

        return promptList;
    }

    @PostMapping(value = "/prompt/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Prompt addUser(HttpServletRequest request, HttpServletResponse response, @RequestBody Prompt prompt) {

        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        Prompt tmpPrompt = dataService.getPromptDataService().addPrompt(prompt);

        if (tmpPrompt == null)
            throw new BadRequestException(getSession().getUuid(), request.getMethod(), request.getRequestURI(), "Prompt with this name ia already exist! Check log file.");

        dataService.getLogService().saveLog(
                getSession().getUuid().toString(),
                request.getRemoteUser(),
                LogLevel.INFO,
                request.getMethod(),
                request.getRequestURI(),
                utils.createJsonStr(getSession().getUuid(),prompt),
                utils.createJsonStr(getSession().getUuid(),tmpPrompt),
                String.valueOf(response.getStatus()),
                request.getRemoteAddr());

        return tmpPrompt;
    }

    @PutMapping(value = "/prompt/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public Prompt updateUser(HttpServletRequest request, HttpServletResponse response,@RequestBody Prompt prompt) {

        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        Prompt tmpPrompt = dataService.getPromptDataService().updatePrompt(prompt);

        dataService.getLogService().saveLog(
                getSession().getUuid().toString(),
                request.getRemoteUser(),
                LogLevel.INFO,
                request.getMethod(),
                request.getRequestURI(),
                utils.createJsonStr(getSession().getUuid(),prompt),
                utils.createJsonStr(getSession().getUuid(),tmpPrompt),
                String.valueOf(response.getStatus()),
                request.getRemoteAddr());

        return tmpPrompt;
    }

    @DeleteMapping(value = "/prompt/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteUser(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") Long id) {

        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        Long test = dataService.getPromptDataService().deletePrompt(getSession().getUuid(), id);
        if (test == 0)
            throw new ForbiddenException(getSession().getUuid(), request.getMethod(), request.getRequestURI(), "Forbidden delete prompt with id [" + id + "], because it referenced by Source. Delete or change Source first.");

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

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    private ApiError sendForbiddenResponse(ForbiddenException except) {

        log.error(except.getSessionId()+ " | Error :" + except.getMessage());

        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, except.getSessionId(), except.getMessage());
        dataService.getLogService().saveLog(except.getSessionId().toString(),LogLevel.WARN, except.getMethod(),except.getApiUrl(), "",utils.createJsonStr(except.getSessionId(), apiError), "403");
        log.error(apiError.toString());

        return apiError;
    }

    @Lookup
    public Session getSession() {
        return null;
    }
}
