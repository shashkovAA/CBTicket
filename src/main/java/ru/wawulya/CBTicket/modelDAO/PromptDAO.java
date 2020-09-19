package ru.wawulya.CBTicket.modelDAO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.wawulya.CBTicket.model.Prompt;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "prompt")
public class PromptDAO {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String filepath;
    private String filename;
    private String description;

    @OneToMany(mappedBy = "promptDAO", fetch = FetchType.LAZY /*, cascade = CascadeType.ALL, */)
    List<SourceDAO> sourcesDAO = new ArrayList<>();

    public PromptDAO (String name, String filepath, String filename, String description) {
        this.name = name;
        this.filepath = filepath;
        this.filename = filename;
        this.description = description;
    }

    public Prompt toPrompt() {
        Prompt prompt = new Prompt();
        prompt.setId(id);
        prompt.setName(name);
        prompt.setFilepath(filepath);
        prompt.setFilename(filename);
        prompt.setDescription(description);
        return prompt;
    }
}

