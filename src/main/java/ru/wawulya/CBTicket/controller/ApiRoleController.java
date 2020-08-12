package ru.wawulya.CBTicket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.wawulya.CBTicket.enums.LogLevel;
import ru.wawulya.CBTicket.model.CompletionCode;
import ru.wawulya.CBTicket.model.Role;
import ru.wawulya.CBTicket.model.Session;
import ru.wawulya.CBTicket.model.User;
import ru.wawulya.CBTicket.service.DataService;
import ru.wawulya.CBTicket.service.RoleDataService;
import ru.wawulya.CBTicket.utility.Utils;

import javax.servlet.http.HttpServletRequest;
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
    public List<Role> getAllRoles(HttpServletRequest request, Authentication auth) {
        UUID sessionId = getSession().getUuid();
        String logMethod ="GET";
        String logApiUrl = "/api/roles";
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl );

        List<Role> roleList = dataService.getRoleService().findAll().stream().map(r->r.toRole()).collect(Collectors.toList());

        dataService.getLogService().saveLog(sessionId.toString(), LogLevel.INFO,logMethod,logApiUrl,"",utils.createJsonStr(sessionId,roleList),"200 OK");
        return roleList;
    }

    @Lookup
    public Session getSession() {
        return null;
    }

}
