package ru.wawulya.CBTicket.utility;

import lombok.Data;

@Data
public class TicketFilter {

    private String id;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String cbNumber;
    private String attemptCount;
    private Boolean finished;
    private String compCodeName;
    private Integer page;
    private Integer size;

}
