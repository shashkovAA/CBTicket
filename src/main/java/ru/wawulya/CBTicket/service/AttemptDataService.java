package ru.wawulya.CBTicket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.wawulya.CBTicket.data.JpaAttemptRepository;
import ru.wawulya.CBTicket.model.ApiLog;
import ru.wawulya.CBTicket.model.Attempt;
import ru.wawulya.CBTicket.modelDAO.ApiLogDAO;
import ru.wawulya.CBTicket.modelDAO.AttemptDAO;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AttemptDataService {

    private JpaAttemptRepository attemptRepo;

    @Autowired
    public AttemptDataService(JpaAttemptRepository attemptRepo) {
        this.attemptRepo = attemptRepo;
    }

    public List<Attempt> findAll(){

        return attemptRepo.findAll().stream().map(AttemptDAO::toAttempt).collect(Collectors.toList());
    }

    public List<Attempt> findAll(Specification specification){

        return attemptRepo.findAll(specification).stream().map(AttemptDAO::toAttempt).collect(Collectors.toList());
    }

    public Page<Attempt> findAll(Pageable pageable){

        return attemptRepo.findAll(pageable).map(AttemptDAO::toAttempt);
    }

    public Page<Attempt> findAll(Specification specification, Pageable pageable){

        return attemptRepo.findAll(specification, pageable).map(AttemptDAO::toAttempt);
    }


}
