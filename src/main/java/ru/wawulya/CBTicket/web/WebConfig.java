package ru.wawulya.CBTicket.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("start");
        registry.addViewController("/settings").setViewName("settings");
        registry.addViewController("/account").setViewName("account");
        registry.addViewController("/logs").setViewName("logs");
        registry.addViewController("/test6").setViewName("test6");

    }
}
