package ru.wawulya.CBTicket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.wawulya.CBTicket.data.JpaSourceRepository;
import ru.wawulya.CBTicket.model.Prompt;
import ru.wawulya.CBTicket.model.Source;
import ru.wawulya.CBTicket.modelDAO.PromptDAO;
import ru.wawulya.CBTicket.modelDAO.SourceDAO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        SourceDAO sourceDAO = sourceRepo.findByNameIgnoringCase("default");

        if (sourceDAO == null) {
            log.debug("Source [default] not found in database and will be added");
            PromptDAO promptDAO = promptDataService.getDefaultPrompt().get();
            sourceRepo.save(new SourceDAO("default", null, null, "Default source", promptDAO));
        }
    }

    public SourceDAO getDefaultSource() {
        return sourceRepo.findByNameIgnoringCase("default");
    }

    public List<Source> findAll() {
        return sourceRepo.findAll().stream().map(SourceDAO::toSource).collect(Collectors.toList());
    }

    public SourceDAO findByName(String name) {
        return sourceRepo.findByNameIgnoringCase(name);
    }

    public SourceDAO findByNameAndUrlOrDefault(String name, String url) {
        SourceDAO sourceDAO = null;

        if (!name.isEmpty() && !url.isEmpty())
            sourceDAO = sourceRepo.findByNameAndUrlIgnoringCase(name, url);
        else if (!url.isEmpty())
            sourceDAO = sourceRepo.findByUrlIgnoringCase(url);
        else if (!name.isEmpty())
            sourceDAO = sourceRepo.findByNameIgnoringCase(name);

        if (sourceDAO != null)
            return sourceDAO;
        else
            return getDefaultSource();
    }

    public Source addSource(Source source) {

        Source tmpSource = null;
        try {
            tmpSource = sourceRepo.save(new SourceDAO(source)).toSource();
        } catch (Exception except) {
            log.error("Inserting source with non-unique name is not allowed!");
        }
        return tmpSource;
    }

    @Async
    public Source updateSource(Source source) {
        SourceDAO sourceDAO = null;
        Optional<SourceDAO> oSource = sourceRepo.findById(source.getId());

        if (oSource.isPresent()) {

            sourceDAO = oSource.get();

            sourceDAO.setName(source.getName());
            sourceDAO.setUrl(source.getUrl());
            sourceDAO.setSkpid(source.getSkpid());
            sourceDAO.setPromptDAO(new PromptDAO(source.getPrompt()));
            sourceDAO.setDescription(source.getDescription());
        }
        sourceDAO = sourceRepo.save(sourceDAO);

        return sourceDAO.toSource();
    }

    @Async
    public Long deleteSource(Long id) {

        Long sourceId = 0L;

        Optional<SourceDAO> oSourceDAO = sourceRepo.findById(id);

        if (oSourceDAO.isPresent()) {
            sourceId = oSourceDAO.get().getId();
            sourceRepo.deleteById(sourceId);
        }

        return sourceId;
    }

}
