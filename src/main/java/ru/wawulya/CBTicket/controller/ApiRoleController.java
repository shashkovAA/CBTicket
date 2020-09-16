package ru.wawulya.CBTicket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.wawulya.CBTicket.enums.LogLevel;
import ru.wawulya.CBTicket.model.CompletionCode;
import ru.wawulya.CBTicket.model.Role;
import ru.wawulya.CBTicket.model.Session;
import ru.wawulya.CBTicket.model.User;
import ru.wawulya.CBTicket.service.DataService;
import ru.wawulya.CBTicket.service.RoleDataService;
import ru.wawulya.CBTicket.utility.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/api")
public class ApiRoleController {

    private DataService dataService;
    private Utils utils;

    @Autowired
    public ApiRoleController(DataService dataService, Utils utils) {
        this.dataService = dataService;
        this.utils = utils;
    }

    @GetMapping(value = "/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Role> getRoles(HttpServletRequest request, HttpServletResponse response) {

        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        List<Role> list = dataService.getRoleService().findAll();

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
        return list;
    }

    /*@GetMapping(value = "/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Role> getAllRoles(HttpServletRequest request, Authentication auth) {
        UUID sessionId = getSession().getUuid();
        String logMethod ="GET";
        String logApiUrl = "/api/roles";
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl );

        List<Role> roleList = dataService.getRoleService().findAll();

        dataService.getLogService().saveLog(sessionId.toString(), LogLevel.INFO,logMethod,logApiUrl,"",utils.createJsonStr(sessionId,roleList),"200 OK");
        return roleList;
    }*/

    @Lookup
    public Session getSession() {
        return null;
    }

}
