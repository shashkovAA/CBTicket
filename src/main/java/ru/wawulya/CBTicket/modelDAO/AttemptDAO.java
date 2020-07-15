package ru.wawulya.CBTicket.modelDAO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.wawulya.CBTicket.model.Attempt;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="attempt")
public class AttemptDAO {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ticket_id", nullable = false)
    private TicketDAO ticketDAO;

    private String ucid;
    private String callid;
    private Timestamp attempt_start;
    private Timestamp attempt_stop;
    private String phantom_number;
    private String operator_number;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="completion_code_id", nullable = false)
    private CompletionCodeDAO completionCodeDAO;

    public AttemptDAO(TicketDAO ticketDAO, CompletionCodeDAO completionCodeDAO) {

        this.ticketDAO = ticketDAO;
        this.completionCodeDAO = completionCodeDAO;
        this.attempt_start = new Timestamp(new Date().getTime());
    }

    public void update (Attempt attempt) {
        ucid = attempt.getUcid();
        callid = attempt.getCallId();
        phantom_number = attempt.getPhantomNumber();
        operator_number = attempt.getOperatorNumber();
        attempt_start = attempt.getAttemptStart();
        attempt_stop = attempt.getAttemptStop();
    }

}
