package ru.wawulya.CBTicket.modelDAO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.wawulya.CBTicket.model.Ticket;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="ticket_params")
public class TicketParamsDAO {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ticket_id", /*referencedColumnName = "Id",*/ nullable = false)
    private TicketDAO ticketDAO;

    @Column(name="cb_url")
    private String cbUrl;
    @Column(name="ucid_old")
    private String ucidOld;
    @Column(name="cb_type")
    private String cbType;
    @Column(name="cb_source")
    private String cbSource;
    @Column(name="cb_originator")
    private String cbOriginator;
    @Column(name="cb_max_attempts")
    private int cbMaxAttempts;
    @Column(name="cb_attempts_timeout")
    private int cbAttemptsTimeout;

    public TicketParamsDAO(TicketDAO ticketDAO, String cbUrl, String ucidOld, String cbType, String cbSource, String cbVdnNumber, int cbMaxAttempts, int cbAttemptsTimeout) {
        this.ticketDAO = ticketDAO;
        this.cbUrl = cbUrl;
        this.ucidOld = ucidOld;
        this.cbType = cbType;
        this.cbSource = cbSource;
        this.cbOriginator = cbVdnNumber;
        this.cbMaxAttempts = cbMaxAttempts;
        this.cbAttemptsTimeout = cbAttemptsTimeout;
    }
}
