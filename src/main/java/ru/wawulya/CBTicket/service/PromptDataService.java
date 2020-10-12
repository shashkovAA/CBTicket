package ru.wawulya.CBTicket.service;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.wawulya.CBTicket.data.JpaPromptRepository;
import ru.wawulya.CBTicket.model.Prompt;
import ru.wawulya.CBTicket.model.User;
import ru.wawulya.CBTicket.modelDAO.PromptDAO;
import ru.wawulya.CBTicket.modelDAO.RoleDAO;
import ru.wawulya.CBTicket.modelDAO.UserDAO;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PromptDataService {

    private JpaPromptRepository promptRepo;

    @Autowired
    public PromptDataService(JpaPromptRepository promptRepo) {
        this.promptRepo = promptRepo;
        initPrompts();
    }

    private void initPrompts() {

        log.info("Initializing prompts ...");

        Optional<PromptDAO> oPromptDAO = null;

        oPromptDAO = promptRepo.findByNameIgnoringCase("default");
        if (!oPromptDAO.isPresent()) {
            log.debug("Prompt [default] not found in database and will be added");
            promptRepo.save(new PromptDAO("default", "http://127.0.0.1/media/", "default.wav", "Default prompt file"));
        }
    }

    public Optional<PromptDAO> getDefaultPrompt() {
        return promptRepo.findByNameIgnoringCase("default");
    }

    public List<Prompt> findAll() {
        return promptRepo.findAll().stream().map(PromptDAO::toPrompt).collect(Collectors.toList());
    }

    public Prompt addPrompt(Prompt prompt) {
        Prompt prompt_s = null;
        try {
            prompt_s = promptRepo.save(new PromptDAO(prompt)).toPrompt();
        } catch (Exception except) {
            log.error("Inserting prompt with non-unique name is not allowed!");
        }
        return prompt_s;
    }

    @Async
    public Prompt updatePrompt(Prompt prompt) {

        PromptDAO promptDAO = null;
        Optional<PromptDAO> oPrompt = promptRepo.findById(prompt.getId());

        if (oPrompt.isPresent()) {

            promptDAO = oPrompt.get();

            promptDAO.setName(prompt.getName());
            promptDAO.setFilepath(prompt.getFilepath());
            promptDAO.setFilename(prompt.getFilename());
            promptDAO.setDescription(prompt.getDescription());
        }
        promptDAO = promptRepo.save(promptDAO);

        return promptDAO.toPrompt();
    }


    public Long deletePrompt(String sessionId, Long id) {
        Long promptId = 0L;

        Optional<PromptDAO> oPromptDAO = promptRepo.findById(id);

        if (oPromptDAO.isPresent()) {
            promptId = oPromptDAO.get().getId();
            try {
                promptRepo.deleteById(promptId);
            }
            catch (Exception ex ) {
                log.error(sessionId + " | SQL Error :" + ex.getMessage());
                promptId = 0L;
            }
        }

        return promptId;
    }



}
