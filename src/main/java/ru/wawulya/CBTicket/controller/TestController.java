package ru.wawulya.CBTicket.controller;

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

    @Autowired
    private Utils utils;

    public TestController() {
    }

    @GetMapping
    public String showTestForm(Model model) {
        model.addAttribute("apiCBTicketMethodList", apiMethodList.getListApiCBTicketMethodsInfo());
        model.addAttribute("compCodeList", apiCompletionCodeController.getAll());

        return "test2";
    }

    @GetMapping(value = "/ticket/job", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Ticket> testGtTicketsForCallBack(@RequestParam(name = "count") int count) {

        List<Ticket> callList = apiTicketController.getTicketsForCallBack(count);
        CompletionCode completionCode = apiCompletionCodeController.getCompCodeBySysName("connection.disconnect.transfer");

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

        RequestResult result = apiTicketController.updateTicket(ticket);

        return callList;
    }
}
