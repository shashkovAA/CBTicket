package ru.wawulya.CBTicket.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiMethodInfo {

    private String method;
    private String url;
    private String requestBody;
    private String responseBody;
    private String description;
}
