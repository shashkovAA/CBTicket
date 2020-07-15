package ru.wawulya.CBTicket.error;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class NotFoundException extends RuntimeException {
    private UUID sessionId;
    private String method;
    private String apiUrl;
    private String message;
}
