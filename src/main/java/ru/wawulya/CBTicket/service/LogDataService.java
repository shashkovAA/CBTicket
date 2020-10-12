package ru.wawulya.CBTicket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.wawulya.CBTicket.data.JpaApiLogRepository;
import ru.wawulya.CBTicket.enums.LogLevel;
import ru.wawulya.CBTicket.model.ApiLog;
import ru.wawulya.CBTicket.modelDAO.ApiLogDAO;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LogDataService {

    private JpaApiLogRepository logRepo;

    @Autowired
    public LogDataService(JpaApiLogRepository logRepo) {
        this.logRepo = logRepo;
    }

    public List<ApiLogDAO> findAll(){

        return logRepo.findAll();
    }

    public Page<ApiLog> findAll(Pageable pageable){

        return logRepo.findAll(pageable).map(ApiLogDAO::toApiLog);
    }

    public Page<ApiLog> findAll(Specification specification, Pageable pageable){

        return logRepo.findAll(specification, pageable).map(ApiLogDAO::toApiLog);
    }

    public List<ApiLog> findAll(Specification specification){

        return logRepo.findAll(specification).stream().map(r->r.toApiLog()).collect(Collectors.toList());
    }

    @Async
    public void saveLog(ApiLog logRecord) {
        logRecord.setRequestBody("");
        logRecord.setResponseBody("");
        logRepo.save(new ApiLogDAO(logRecord));
    }

}
