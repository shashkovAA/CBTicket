package ru.wawulya.CBTicket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "ticket")
@Validated
public class AppConfig {


}
