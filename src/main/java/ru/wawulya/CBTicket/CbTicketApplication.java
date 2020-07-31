package ru.wawulya.CBTicket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import ru.wawulya.CBTicket.service.DataService;

@Slf4j
@SpringBootApplication
public class CbTicketApplication  {

	public static void main(String[] args) {
		SpringApplication.run(CbTicketApplication.class, args);
	}

}
