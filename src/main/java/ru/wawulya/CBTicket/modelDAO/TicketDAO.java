package ru.wawulya.CBTicket.modelDAO;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.wawulya.CBTicket.model.Ticket;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="ticket")
public class TicketDAO {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="cb_number", nullable = false)
    private String cbNumber;

    @Column(name="cb_date")
    private Timestamp cbDate;

    @CreationTimestamp
    @Column(name="create_date")
    private Timestamp createDate;

    @Column(name="attempt_count")
    private int attemptCount;

    private boolean finished;

    @OneToOne(mappedBy = "ticketDAO", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval=true)
    private TicketParamsDAO ticketParamsDAO;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="last_completion_code_id", nullable = false)
    private CompletionCodeDAO completionCodeDAO;

    @OneToMany(mappedBy = "ticketDAO", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval=true)
    private List<AttemptDAO> attemptDAOs = new ArrayList<>();


    public TicketDAO(String cbNumber, Timestamp cbDate, CompletionCodeDAO completionCode) {
        this.cbNumber = cbNumber;
        this.cbDate = cbDate;
        this.attemptCount = 0;
        this.completionCodeDAO = completionCode;
    }

    public TicketDAO(Ticket ticket, CompletionCodeDAO completionCodeDAO) {
        this.cbNumber = ticket.getCbNumber();
        this.cbDate = ticket.getCbDate();
        this.attemptCount = ticket.getAttemptCount();
        this.completionCodeDAO = completionCodeDAO;
    }

    public void addAttemptDAO(AttemptDAO attemptDAO) {
        this.attemptDAOs.add(attemptDAO);
    }

}

