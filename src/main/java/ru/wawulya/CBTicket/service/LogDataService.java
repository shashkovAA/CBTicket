package ru.wawulya.CBTicket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.wawulya.CBTicket.data.JpaApiLogRepository;
import ru.wawulya.CBTicket.enums.LogLevel;
import ru.wawulya.CBTicket.modelDAO.ApiLogDAO;

@Slf4j
@Service
public class LogDataService {

    private JpaApiLogRepository logRepo;

    @Autowired
    public LogDataService(JpaApiLogRepository logRepo) {
        this.logRepo = logRepo;
    }

    @Async
    public void saveLog(String sessionId, String level, String logMethod, String logApiUrl, String requestBody, String responseBody, String status) {
        logRepo.save(new ApiLogDAO(sessionId, LogLevel.INFO,logMethod,logApiUrl, requestBody,responseBody, status));
    }
    @Async
    public void saveLog(String sessionId, String user, String level, String logMethod, String logApiUrl, String requestBody, String responseBody, String status) {
        logRepo.save(new ApiLogDAO(sessionId, user, LogLevel.INFO,logMethod,logApiUrl, requestBody,responseBody, status));
    }
    @Async
    public void saveLog(String sessionId, String user, String level, String logMethod, String logApiUrl, String requestBody, String responseBody, String status, String host) {
        logRepo.save(new ApiLogDAO(sessionId, user, LogLevel.INFO,logMethod,logApiUrl, requestBody,responseBody, status, host));
    }
}
