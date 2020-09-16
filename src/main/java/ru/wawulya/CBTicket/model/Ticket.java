package ru.wawulya.CBTicket.model;

import lombok.*;
import ru.wawulya.CBTicket.modelDAO.AttemptDAO;
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
    private Timestamp createDate;
    private int attemptCount;
    private boolean finished;
    private TicketParams ticketParams;
    private List<Attempt> attempts;
    private CompletionCode lastCompletionCode;


    public Ticket(TicketDAO ticketDAO) {

        id = ticketDAO.getId();
        cbNumber = ticketDAO.getCbNumber();
        cbDate = ticketDAO.getCbDate();
        createDate = ticketDAO.getCreateDate();
        attemptCount = ticketDAO.getAttemptCount();
        finished = ticketDAO.isFinished();
        ticketParams = ticketDAO.getTicketParamsDAO().toTicketParams();
        lastCompletionCode = ticketDAO.getCompletionCodeDAO().toCompletionCode();
        attempts = ticketDAO.getAttemptDAOs().stream().map(AttemptDAO::toAttempt).collect(Collectors.toList());
    }

}