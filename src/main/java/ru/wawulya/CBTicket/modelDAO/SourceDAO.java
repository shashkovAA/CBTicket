package ru.wawulya.CBTicket.modelDAO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.wawulya.CBTicket.model.Source;

import javax.persistence.*;

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

    public SourceDAO(String name, String url, String skpid, String description, PromptDAO promptDAO) {
        this.name = name;
        this.url = url;
        this.skpid = skpid;
        this.description = description;
        this.promptDAO = promptDAO;
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
