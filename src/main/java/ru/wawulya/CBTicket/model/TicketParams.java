package ru.wawulya.CBTicket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.wawulya.CBTicket.modelDAO.TicketParamsDAO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketParams {

    private String cbUrl;
    private String ucidOld;
    private String cbType;
    private String cbSource;
    private String cbOriginator;
    private int cbMaxAttempts;
    private int cbAttemptsTimeout;

    public TicketParams (TicketParamsDAO ticketParamsDAO) {
        cbUrl = ticketParamsDAO.getCbUrl();
        ucidOld = ticketParamsDAO.getUcidOld();
        cbType = ticketParamsDAO.getCbType();
        cbSource = ticketParamsDAO.getCbSource();
        cbOriginator = ticketParamsDAO.getCbOriginator();
        cbMaxAttempts = ticketParamsDAO.getCbMaxAttempts();
        cbAttemptsTimeout = ticketParamsDAO.getCbAttemptsTimeout();
    }
}
