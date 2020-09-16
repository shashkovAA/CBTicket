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
    private String callId;

    @Column(name="attempt_start")
    private Timestamp attemptStart;

    @Column(name="attempt_stop")
    private Timestamp attemptStop;

    @Column(name="phantom_number")
    private String phantomNumber;

    @Column(name="operator_number")
    private String operatorNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="completion_code_id", nullable = false)
    private CompletionCodeDAO completionCodeDAO;

    public AttemptDAO(TicketDAO ticketDAO, CompletionCodeDAO completionCodeDAO) {

        this.ticketDAO = ticketDAO;
        this.completionCodeDAO = completionCodeDAO;
        this.attemptStart = new Timestamp(new Date().getTime());
    }

    public void update (Attempt attempt) {
        ucid = attempt.getUcid();
        callId = attempt.getCallId();
        phantomNumber = attempt.getPhantomNumber();
        operatorNumber = attempt.getOperatorNumber();
        attemptStart = attempt.getAttemptStart();
        attemptStop = attempt.getAttemptStop();
    }

    public Attempt toAttempt() {
        Attempt attempt = new Attempt();
        attempt.setId(id);
        attempt.setTicketId(ticketDAO.getId());
        attempt.setAttemptStart(attemptStart);
        attempt.setAttemptStop(attemptStop);
        attempt.setUcid(ucid);
        attempt.setCallId(callId);
        attempt.setPhantomNumber(phantomNumber);
        attempt.setOperatorNumber(operatorNumber);
        attempt.setCompletionCodeId(completionCodeDAO.getId());

        return attempt;
    }

}
