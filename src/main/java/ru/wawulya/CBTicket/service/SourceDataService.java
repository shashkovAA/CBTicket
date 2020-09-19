package ru.wawulya.CBTicket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.wawulya.CBTicket.data.JpaSourceRepository;
import ru.wawulya.CBTicket.model.Prompt;
import ru.wawulya.CBTicket.modelDAO.PromptDAO;
import ru.wawulya.CBTicket.modelDAO.SourceDAO;

import java.util.Optional;

@Slf4j
@Service
public class SourceDataService {

    private JpaSourceRepository sourceRepo;
    private PromptDataService promptDataService;

    public SourceDataService(JpaSourceRepository sourceRepo, PromptDataService promptDataService) {
        this.promptDataService = promptDataService;
        this.sourceRepo = sourceRepo;
        initSources();
    }

    private void initSources() {

        log.info("Initializing sources ...");

        Optional<SourceDAO> oSourceDAO = null;

        oSourceDAO = sourceRepo.findByNameIgnoringCase("default");
        if (!oSourceDAO.isPresent()) {
            log.debug("Source [default] not found in database and will be added");
            PromptDAO promptDAO = promptDataService.getDefaultPrompt().get();
            sourceRepo.save(new SourceDAO("default", null, null, "Default source", promptDAO));
        }
    }

}
