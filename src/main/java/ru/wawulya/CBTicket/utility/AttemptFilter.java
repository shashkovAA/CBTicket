package ru.wawulya.CBTicket.utility;

import lombok.Data;

@Data
public class AttemptFilter {

    private String ticketId;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String callId;
    private String operatorNumber;
    private String phantomNumber;
    private String compCode;
    private String ucid;
    private Integer page;
    private Integer size;
}
