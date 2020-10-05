package ru.wawulya.CBTicket.controller;

import com.sun.deploy.net.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.wawulya.CBTicket.model.*;
import ru.wawulya.CBTicket.modelDAO.TicketDAO;
import ru.wawulya.CBTicket.service.DataService;
import ru.wawulya.CBTicket.utility.Utils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


@Slf4j
@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    ApiMethodList apiMethodList;

    @Autowired
    private ApiTicketController apiTicketController;

    @Autowired
    private ApiCompletionCodeController apiCompletionCodeController;

    private Utils utils;
    private DataService dataService;

    public TestController(DataService dataService, Utils utils) {
        this.dataService = dataService;
        this.utils = utils;
    }

   /* @GetMapping
    public String showTestForm(Model model) {
        //model.addAttribute("apiCBTicketMethodList", apiMethodList.getListApiCBTicketMethodsInfo());
        //model.addAttribute("compCodeList", dataService.getCompCodeDataService().findAllCompCodes());
        return "test";
    }*/

    @GetMapping(value = "/ticket/job", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Ticket> testGtTicketsForCallBack(@RequestParam(name = "count") int count) {

        //List<Ticket> callList = apiTicketController.getTicketsForCallBack(count);
       /* CompletionCode completionCode = apiCompletionCodeController.getCompCodeBySysName("connection.disconnect.transfer", HttpResponse.class);

        Ticket ticket = callList.get(0);
        Attempt attempt = ticket.getAttempts().get(ticket.getAttemptCount()-1);

        ticket.setLastCompletionCode(completionCode);
        if (!completionCode.isRecall())
            ticket.setFinished(true);

        attempt.setCallId("11230");
        attempt.setUcid("11011222220000022222");
        attempt.setAttemptStop(new Timestamp(new Date().getTime() + 15000));
        attempt.setCompletionCode(completionCode);
        attempt.setPhantomNumber("11203");
        attempt.setOperatorNumber("70035");

        RequestResult result = apiTicketController.updateTicket(ticket);*/

        return null;
    }

    @GetMapping(value = "/params", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String testParams(@RequestParam(name = "count") int count) {

        log.debug("count = " + count);
        return "Success";
    }
}
