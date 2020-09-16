package ru.wawulya.CBTicket.utility;

import lombok.Data;

@Data
public class TicketParamsFilter {

    private String ticketId;
    private String cbMaxAttempts;
    private String cbAttemptsTimeout;
    private String cbSource;
    private String cbType;
    private String cbOriginator;
    private String cbUrl;
    private String ucidOld;
    private Integer page;
    private Integer size;

}
