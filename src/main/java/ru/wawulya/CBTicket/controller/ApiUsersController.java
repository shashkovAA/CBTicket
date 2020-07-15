package ru.wawulya.CBTicket.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.wawulya.CBTicket.enums.LogLevel;
import ru.wawulya.CBTicket.enums.PropertyFieldsEnum;
import ru.wawulya.CBTicket.error.ApiError;
import ru.wawulya.CBTicket.error.BadRequestException;
import ru.wawulya.CBTicket.error.NotFoundException;
import ru.wawulya.CBTicket.model.*;
import ru.wawulya.CBTicket.modelDAO.PropertyDAO;
import ru.wawulya.CBTicket.service.DataService;
import ru.wawulya.CBTicket.service.FileStorageService;
import ru.wawulya.CBTicket.utility.Utils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;


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

        UUID sessionId = new Session().getUuid();
        String logMethod ="GET";
        String logApiUrl = "/api/users";
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        List<User> userList = users.getAllUsers();

        dataService.saveLog(sessionId.toString(), LogLevel.INFO,logMethod,logApiUrl,"",utils.createJsonStr(sessionId,userList),"200 OK");
        return userList;
    }

    @PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public User addUser(@RequestBody User user) {
        UUID sessionId = new Session().getUuid();
        String logMethod ="POST";
        String logApiUrl = "/api/users";
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        User usr = dataService.addUser(user);

        if (usr == null)
            throw new BadRequestException(sessionId, logMethod, logApiUrl, "User with this username ia already exist! Check log file.");
        else
            users.addUser(usr);

        dataService.saveLog(sessionId.toString(),LogLevel.INFO,logMethod,logApiUrl, utils.createJsonStr(sessionId,user),utils.createJsonStr(sessionId,user),"200 OK");
        return usr;
    }

    @PutMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public User updateUser(@RequestBody User user) {
        UUID sessionId = new Session().getUuid();
        String logMethod ="PUT";
        String logApiUrl = "/api/users";
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);


        users.updateUser(user);

        User usr = dataService.updateUser(user);

        dataService.saveLog(sessionId.toString(),LogLevel.INFO,logMethod,logApiUrl, utils.createJsonStr(sessionId,usr),utils.createJsonStr(sessionId,usr),"200 OK");
        return usr;
    }

    @DeleteMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteUser(@PathVariable("id") Long id) {
        UUID sessionId = new Session().getUuid();
        String logMethod ="DELETE";
        String logApiUrl = "/api/users/ " + id;
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        Long userId = dataService.deleteUser(id);

        if (userId == 0L)
            throw new NotFoundException(sessionId, logMethod, logApiUrl, "Not found");
        else
            users.deleteUser(id);

        dataService.saveLog(sessionId.toString(),LogLevel.INFO,logMethod,logApiUrl, "","","200 OK");
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ApiError sendNotFoundResponse(NotFoundException except) {

        log.error(except.getSessionId()+ " | Error " + except.getMessage());

        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, except.getSessionId(), except.getMessage());
        dataService.saveLog(except.getSessionId().toString(),LogLevel.WARN,except.getMethod(),except.getApiUrl(), "",utils.createJsonStr(except.getSessionId(), apiError), "404 NOT FOUND");
        log.error(apiError.toString());

        return apiError;
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ApiError sendBadRequestResponse(BadRequestException except) {

        log.error(except.getSessionId()+ " | Error :" + except.getMessage());

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, except.getSessionId(), except.getMessage());
        dataService.saveLog(except.getSessionId().toString(),LogLevel.ERROR, except.getMethod(),except.getApiUrl(), "",utils.createJsonStr(except.getSessionId(), apiError), "400 BAD REQUEST");
        log.error(apiError.toString());

        return apiError;
    }

}
