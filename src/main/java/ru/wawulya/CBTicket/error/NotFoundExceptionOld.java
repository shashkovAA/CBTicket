package ru.wawulya.CBTicket.error;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Data
@AllArgsConstructor
public class NotFoundExceptionOld extends RuntimeException {
    private UUID sessionId;
    private String method;
    private String apiUrl;
    private String message;

}
