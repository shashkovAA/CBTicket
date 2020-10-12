package ru.wawulya.CBTicket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import ru.wawulya.CBTicket.error.BadRequestException;

import ru.wawulya.CBTicket.model.*;
import ru.wawulya.CBTicket.modelCache.Users;
import ru.wawulya.CBTicket.service.DataService;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping(value = "/api")
public class ApiUsersController {

    private DataService dataService;
    private Users users;

    @Autowired
    public ApiUsersController(DataService dataService, Users users) {
        this.dataService = dataService;
        this.users = users;

    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getAllUsers(HttpServletRequest request, HttpServletResponse response) {

        List<User> userList = users.getAllUsers().stream().filter(user->!user.getUsername().equals("partner")).collect(Collectors.toList());

       return userList;
    }

    @PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public User addUser(HttpServletRequest request, HttpServletResponse response, @RequestBody User user) {

        //user.addRole(new Role("ROLE_USER", "USER"));
        User usr = dataService.getUserService().addUser(user);

        if (usr == null)
            throw new BadRequestException("User with this username ia already exist! Check log file.");
        else
            users.addUser(usr);

        return usr;
    }

    @PutMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public User updateUser(HttpServletRequest request, HttpServletResponse response, @RequestBody User user) {

        users.updateUser(user);
        dataService.getUserService().updateUser(user);
        return user;
    }

    @DeleteMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteUser(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") Long id) {

        users.deleteUser(id);

        dataService.getUserService().deleteUser(id);

    }

   /* @ExceptionHandler(NotFoundExceptionOld.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ApiErrorOld sendNotFoundResponse(NotFoundExceptionOld except) {

        log.error(except.getSessionId()+ " | Error " + except.getMessage());

        ApiErrorOld apiError = new ApiErrorOld(HttpStatus.NOT_FOUND, except.getSessionId(), except.getMessage());
        dataService.getLogService().saveLog(except.getSessionId().toString(),LogLevel.WARN,except.getMethod(),except.getApiUrl(), "",utils.createJsonStr(except.getSessionId(), apiError), "404 NOT FOUND");
        log.error(apiError.toString());

        return apiError;
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ApiErrorOld sendBadRequestResponse(BadRequestException except) {

        log.error(except.getSessionId()+ " | Error :" + except.getMessage());

        ApiErrorOld apiError = new ApiErrorOld(HttpStatus.BAD_REQUEST, except.getSessionId(), except.getMessage());
        dataService.getLogService().saveLog(except.getSessionId().toString(),LogLevel.ERROR, except.getMethod(),except.getApiUrl(), "",utils.createJsonStr(except.getSessionId(), apiError), "400 BAD REQUEST");
        log.error(apiError.toString());

        return apiError;
    }

    @Lookup
    public Session getSession() {
        return null;
    }*/

}
