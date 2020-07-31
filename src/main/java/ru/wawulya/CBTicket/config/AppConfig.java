package ru.wawulya.CBTicket.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
//@PropertySource("classpath:application.properties")
public class AppConfig {

    //@Value("${external.config.file}")
    private static String configFile;

}
