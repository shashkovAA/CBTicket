package ru.wawulya.CBTicket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.wawulya.CBTicket.data.JpaPromptRepository;
import ru.wawulya.CBTicket.modelDAO.PromptDAO;

import java.util.Optional;

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



}
