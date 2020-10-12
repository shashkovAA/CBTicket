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

    @Autowired
    public ApiRoleController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping(value = "/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Role> getRoles(HttpServletRequest request, HttpServletResponse response) {

        return dataService.getRoleService().findAll();
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


}
