package ru.wawulya.CBTicket.modelDAO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.wawulya.CBTicket.model.Source;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "source")
public class SourceDAO {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String url;
    private String skpid;
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="prompt_id", nullable = false)
    private PromptDAO promptDAO;

    @OneToMany(mappedBy = "sourceDAO", fetch = FetchType.LAZY /*, cascade = CascadeType.ALL, */)
    List<TicketParamsDAO> ticketParamsDAOs = new ArrayList<>();

    public SourceDAO(String name, String url, String skpid, String description, PromptDAO promptDAO) {
        this.name = name;
        this.url = url;
        this.skpid = skpid;
        this.description = description;
        this.promptDAO = promptDAO;
    }

    public SourceDAO(Source source) {
        this.id = source.getId();
        this.name = source.getName();
        this.url = source.getUrl();
        this.skpid = source.getSkpid();
        this.promptDAO = new PromptDAO(source.getPrompt());
        this.description = source.getDescription();
    }


    public Source toSource() {
        Source source = new Source();
        source.setId(id);
        source.setName(name);
        source.setUrl(url);
        source.setSkpid(skpid);
        source.setDescription(description);
        source.setPrompt(promptDAO.toPrompt());
        return source;
    }
}
