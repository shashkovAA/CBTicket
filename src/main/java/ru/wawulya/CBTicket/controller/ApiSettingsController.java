package ru.wawulya.CBTicket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.wawulya.CBTicket.model.RequestResult;
import ru.wawulya.CBTicket.service.RestartService;

@Slf4j
@RestController
@RequestMapping(value = "/api")
@PropertySource(value = "file:/temp/cbticket/uploads/external.properties", ignoreResourceNotFound = true)
public class ApiSettingsController {

    private RestartService restartService;

    @Value("${datasource.mssql.testStr}")
    private String testStr;

    @Autowired
    public ApiSettingsController(RestartService restartService) {
        this.restartService = restartService;
    }

    @PostMapping("/settings/restart")
    public RequestResult restart() {
        restartService.restartApp();
        RequestResult result = new RequestResult("Success","Restart application is successfully.");
        return result;
    }

    @GetMapping("/settings/test")
    public String test() {
        return testStr;
    }


}
