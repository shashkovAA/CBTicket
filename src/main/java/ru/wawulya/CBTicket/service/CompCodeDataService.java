package ru.wawulya.CBTicket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import ru.wawulya.CBTicket.data.JpaCompletionCodeRepository;
import ru.wawulya.CBTicket.model.CompletionCode;
import ru.wawulya.CBTicket.modelDAO.CompletionCodeDAO;

import java.util.Optional;

@Slf4j
@Configuration
public class CompCodeDataService {

    private JpaCompletionCodeRepository compCodeRepo;

    @Autowired
    public CompCodeDataService(JpaCompletionCodeRepository compCodeRepo) {
        this.compCodeRepo = compCodeRepo;
    }





}
