package ru.wawulya.CBTicket.error;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Data
public class ApiError {
    private String sessionId;
    private String timestamp;
    private HttpStatus status;
    private String message;


    public ApiError(HttpStatus status, UUID sessionId, String message) {
        this.status = status;
        this.sessionId = sessionId.toString();
        this.message = message;
        this.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }



}
