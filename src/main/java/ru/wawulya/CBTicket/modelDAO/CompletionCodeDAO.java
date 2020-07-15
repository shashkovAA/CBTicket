package ru.wawulya.CBTicket.modelDAO;

import lombok.*;
import ru.wawulya.CBTicket.model.CompletionCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="completion_code")
public class CompletionCodeDAO {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String sysname;

    private String description;

    private boolean recall;

    @OneToMany(mappedBy = "completionCodeDAO", fetch = FetchType.LAZY /*, cascade = CascadeType.ALL, */)
    List<TicketDAO> ticketsDAO = new ArrayList<>();

    @OneToMany(mappedBy = "completionCodeDAO", fetch = FetchType.LAZY /*, cascade = CascadeType.ALL, */)
    List<AttemptDAO> attemptDAOS = new ArrayList<>();

    public CompletionCodeDAO(String name, String sysname, String description, boolean recall) {
        this.name = name;
        this.sysname = sysname;
        this.description = description;
        this.recall = recall;
    }

    public CompletionCode toCompletionCode () {

        CompletionCode compCode = new CompletionCode();
        compCode.setId(this.getId());
        compCode.setName(this.getName());
        compCode.setSysname(this.getSysname());
        compCode.setDescription(this.getDescription());
        compCode.setRecall(this.isRecall());

        return compCode;
    }

    public CompletionCodeDAO(CompletionCode c) {
        id = c.getId();
        name = c.getName();
        sysname = c.getSysname();
        description = c.getDescription();
        recall = c.isRecall();
    }

}



