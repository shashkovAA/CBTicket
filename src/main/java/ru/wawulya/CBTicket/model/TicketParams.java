package ru.wawulya.CBTicket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.wawulya.CBTicket.modelDAO.TicketParamsDAO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketParams {

    private Long id;
    private Long ticketId;
    private String cbUrl;
    private String ucidOld;
    private String cbType;
    private String cbSource;
    private String cbOriginator;
    private int cbMaxAttempts;
    private int cbAttemptsTimeout;

}
