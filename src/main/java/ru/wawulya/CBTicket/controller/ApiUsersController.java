package ru.wawulya.CBTicket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.wawulya.CBTicket.enums.LogLevel;
import ru.wawulya.CBTicket.error.ApiError;
import ru.wawulya.CBTicket.error.BadRequestException;
import ru.wawulya.CBTicket.error.NotFoundException;
import ru.wawulya.CBTicket.model.*;
import ru.wawulya.CBTicket.modelCache.Users;
import ru.wawulya.CBTicket.modelDAO.RoleDAO;
import ru.wawulya.CBTicket.service.DataService;
import ru.wawulya.CBTicket.service.LogDataService;
import ru.wawulya.CBTicket.service.UserDataService;
import ru.wawulya.CBTicket.utility.Utils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping(value = "/api")
public class ApiUsersController {

    private DataService dataService;

    private Utils utils;
    private Users users;

    @Autowired
    public ApiUsersController(DataService dataService, Utils utils, Users users) {
        this.dataService = dataService;

        this.utils = utils;
        this.users = users;

    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getAllUsers() {

        UUID sessionId = getSession().getUuid();
        String logMethod ="GET";
        String logApiUrl = "/api/users";
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        List<User> userList = users.getAllUsers().stream().filter(user->!user.getUsername().equals("partner")).collect(Collectors.toList());

        dataService.getLogService().saveLog(sessionId.toString(), LogLevel.INFO,logMethod,logApiUrl,"",utils.createJsonStr(sessionId,userList),"200 OK");
        return userList;
    }

    @PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public User addUser(@RequestBody User user) {
        UUID sessionId = getSession().getUuid();
        String logMethod ="POST";
        String logApiUrl = "/api/users";
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);


        user.addRole(new Role("ROLE_ADMIN"));

        User usr = dataService.getUserService().addUser(user);

        if (usr == null)
            throw new BadRequestException(sessionId, logMethod, logApiUrl, "User with this username ia already exist! Check log file.");
        else
            users.addUser(usr);

        dataService.getLogService().saveLog(sessionId.toString(),LogLevel.INFO,logMethod,logApiUrl, utils.createJsonStr(sessionId,user),utils.createJsonStr(sessionId,user),"200 OK");
        return usr;
    }

    @PutMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public User updateUser(@RequestBody User user) {
        UUID sessionId = getSession().getUuid();
        String logMethod ="PUT";
        String logApiUrl = "/api/users";
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        users.updateUser(user);

        dataService.getUserService().updateUser(user);
        dataService.getLogService().saveLog(sessionId.toString(),LogLevel.INFO,logMethod,logApiUrl, utils.createJsonStr(sessionId,user),utils.createJsonStr(sessionId,user),"200 OK");
        return user;
    }

    @DeleteMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteUser(@PathVariable("id") Long id) {
        UUID sessionId = getSession().getUuid();
        String logMethod ="DELETE";
        String logApiUrl = "/api/users/" + id;
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        users.deleteUser(id);

        dataService.getUserService().deleteUser(id);
        dataService.getLogService().saveLog(sessionId.toString(),LogLevel.INFO,logMethod,logApiUrl, "","","200 OK");
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ApiError sendNotFoundResponse(NotFoundException except) {

        log.error(except.getSessionId()+ " | Error " + except.getMessage());

        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, except.getSessionId(), except.getMessage());
        dataService.getLogService().saveLog(except.getSessionId().toString(),LogLevel.WARN,except.getMethod(),except.getApiUrl(), "",utils.createJsonStr(except.getSessionId(), apiError), "404 NOT FOUND");
        log.error(apiError.toString());

        return apiError;
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ApiError sendBadRequestResponse(BadRequestException except) {

        log.error(except.getSessionId()+ " | Error :" + except.getMessage());

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, except.getSessionId(), except.getMessage());
        dataService.getLogService().saveLog(except.getSessionId().toString(),LogLevel.ERROR, except.getMethod(),except.getApiUrl(), "",utils.createJsonStr(except.getSessionId(), apiError), "400 BAD REQUEST");
        log.error(apiError.toString());

        return apiError;
    }

    @Lookup
    public Session getSession() {
        return null;
    }

}
