package ru.wawulya.CBTicket.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.wawulya.CBTicket.data.*;
import ru.wawulya.CBTicket.enums.*;
import ru.wawulya.CBTicket.model.*;
import ru.wawulya.CBTicket.modelCache.Properties;
import ru.wawulya.CBTicket.modelCache.Users;
import ru.wawulya.CBTicket.modelDAO.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Data
@Service
public class DataService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserDataService userService;
    private LogDataService logService;
    private RoleDataService roleService;
    private CompCodeDataService compCodeDataService;
    private PropertyDataService propertyDataService;
    private TicketDataService ticketDataService;

    @Autowired
    public DataService(

                       TicketDataService ticketDataService,
                       CompCodeDataService compCodeDataService,
                       PropertyDataService propertyDataService,
                       UserDataService userService,
                       RoleDataService roleService,
                       LogDataService logService) {


        this.ticketDataService = ticketDataService;
        this.compCodeDataService = compCodeDataService;
        this.propertyDataService = propertyDataService;
        this.userService = userService;
        this.roleService= roleService;
        this.logService = logService;

    }

}
