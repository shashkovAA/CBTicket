package ru.wawulya.CBTicket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.wawulya.CBTicket.model.ApiMethodList;

@Slf4j
@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    ApiMethodList apiMethodList;

    @GetMapping
    public String showHomePage(Model model) {
        log.info("Start home controller!!");
        model.addAttribute("apiPropertyMethodList", apiMethodList.getListApiPropertyMethodsInfo());
        model.addAttribute("apiCBTicketMethodList", apiMethodList.getListApiCBTicketMethodsInfo());
        return "home";
    }


}
