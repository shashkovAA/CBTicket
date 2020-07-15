package ru.wawulya.CBTicket.model;

import lombok.*;
import ru.wawulya.CBTicket.modelDAO.TicketDAO;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket{

    private Long id;
    private String cbNumber;
    private Timestamp cbDate;
    private int attemptCount;
    private boolean finished;
    private TicketParams ticketParams;
    private List<Attempt> attempts;
    private CompletionCode lastCompletionCode;


    public Ticket(TicketDAO ticketDAO) {

        id = ticketDAO.getId();
        cbNumber = ticketDAO.getCbNumber();
        cbDate = ticketDAO.getCbDate();
        attemptCount = ticketDAO.getAttemptCount();
        finished = ticketDAO.isFinished();
        ticketParams = new TicketParams(ticketDAO.getTicketParamsDAO());
        lastCompletionCode = new CompletionCode(ticketDAO.getCompletionCodeDAO());
        attempts = ticketDAO.getAttemptDAOs().stream().map(a -> new Attempt(a)).collect(Collectors.toList());
    }

}