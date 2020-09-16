package ru.wawulya.CBTicket.utility;

import lombok.Data;

@Data
public class ApiLogSearch {

    private String username;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String level;
    private String method;
    private String apiUrl;
    private String sessionId;
    private String host;
    private Integer page;
    private Integer size;

}
