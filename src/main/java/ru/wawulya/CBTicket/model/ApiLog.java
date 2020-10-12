package ru.wawulya.CBTicket.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@Data
@NoArgsConstructor
public class ApiLog {

    private Timestamp date;
    private String level;
    private String username;
    private String method;
    private String apiUrl;
    private String requestBody;
    private String responseBody;
    private String statusCode;
    private String duration;
    private String host;
    private String sessionId;
}
