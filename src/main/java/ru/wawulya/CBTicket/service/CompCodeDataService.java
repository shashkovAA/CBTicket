package ru.wawulya.CBTicket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.wawulya.CBTicket.data.JpaCompletionCodeRepository;
import ru.wawulya.CBTicket.enums.CompCodeNameEnum;
import ru.wawulya.CBTicket.enums.CompCodeSysnameEnum;
import ru.wawulya.CBTicket.model.CompletionCode;
import ru.wawulya.CBTicket.modelDAO.CompletionCodeDAO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CompCodeDataService {

    private JpaCompletionCodeRepository compCodeRepo;

    @Autowired
    public CompCodeDataService(JpaCompletionCodeRepository compCodeRepo) {
        this.compCodeRepo = compCodeRepo;

        this.initCompletionCodes();
    }

    public void initCompletionCodes() {

        log.info("Initialize completion codes ...");

        Optional<CompletionCodeDAO> oCompletionCodeDAO = null;

        oCompletionCodeDAO = compCodeRepo.findBySysname(CompCodeSysnameEnum.NOT_CALLED);
        if (!oCompletionCodeDAO.isPresent()) {
            log.debug("CompletionCode [" + CompCodeSysnameEnum.NOT_CALLED + "] not found in database and will be added");
            compCodeRepo.save(new CompletionCodeDAO(CompCodeNameEnum.NOT_CALLED, CompCodeSysnameEnum.NOT_CALLED, "Обратный вызов не совершался", true));
        }

        oCompletionCodeDAO = compCodeRepo.findBySysname(CompCodeSysnameEnum.DIALING);
        if (!oCompletionCodeDAO.isPresent()) {
            log.debug("CompletionCode [" + CompCodeSysnameEnum.DIALING + "] not found in database and will be added");
            compCodeRepo.save(new CompletionCodeDAO(CompCodeNameEnum.DIALING, CompCodeSysnameEnum.DIALING, "Обратный вызов в процессе набора", false));
        }

        oCompletionCodeDAO = compCodeRepo.findBySysname(CompCodeSysnameEnum.COMPLETED);
        if (!oCompletionCodeDAO.isPresent()) {
            log.debug("CompletionCode [" + CompCodeSysnameEnum.COMPLETED + "] not found in database and will be added");
            compCodeRepo.save(new CompletionCodeDAO(CompCodeNameEnum.COMPLETED, CompCodeSysnameEnum.COMPLETED, "Обратный вызов завершен", false));
        }

        oCompletionCodeDAO = compCodeRepo.findBySysname(CompCodeSysnameEnum.CANCELED);
        if (!oCompletionCodeDAO.isPresent()) {
            log.debug("CompletionCode [" + CompCodeSysnameEnum.CANCELED + "] not found in database and will be added");
            compCodeRepo.save(new CompletionCodeDAO(CompCodeNameEnum.CANCELED, CompCodeSysnameEnum.CANCELED, "Обратный вызов отменен", false));
        }

        oCompletionCodeDAO = compCodeRepo.findBySysname(CompCodeSysnameEnum.BUSY);
        if (!oCompletionCodeDAO.isPresent()) {
            log.debug("CompletionCode [" + CompCodeSysnameEnum.BUSY + "] not found in database and will be added");
            compCodeRepo.save(new CompletionCodeDAO(CompCodeNameEnum.BUSY, CompCodeNameEnum.BUSY, "Обратный вызов не завершен. Получен сигнал занятости абонента", true));
        }

        oCompletionCodeDAO = compCodeRepo.findBySysname(CompCodeSysnameEnum.NETWORK_BUSY);
        if (!oCompletionCodeDAO.isPresent()) {
            log.debug("CompletionCode [" + CompCodeSysnameEnum.NETWORK_BUSY + "] not found in database and will be added");
            compCodeRepo.save(new CompletionCodeDAO(CompCodeNameEnum.NETWORK_BUSY, CompCodeSysnameEnum.NETWORK_BUSY, "Обратный вызов не завершен. Получен сигнал занятости не сети", true));
        }

        oCompletionCodeDAO = compCodeRepo.findBySysname(CompCodeSysnameEnum.NO_ANSWER);
        if (!oCompletionCodeDAO.isPresent()) {
            log.debug("CompletionCode [" + CompCodeSysnameEnum.NO_ANSWER + "] not found in database and will be added");
            compCodeRepo.save(new CompletionCodeDAO(CompCodeNameEnum.NO_ANSWER, CompCodeSysnameEnum.NO_ANSWER, "Обратный вызов не завершен. Нет ответа абонента", true));
        }

        oCompletionCodeDAO = compCodeRepo.findBySysname(CompCodeSysnameEnum.DROP_BY_OPERATOR);
        if (!oCompletionCodeDAO.isPresent()) {
            log.debug("CompletionCode [" + CompCodeSysnameEnum.DROP_BY_OPERATOR + "] not found in database and will be added");
            compCodeRepo.save(new CompletionCodeDAO(CompCodeNameEnum.DROP_BY_OPERATOR, CompCodeSysnameEnum.DROP_BY_OPERATOR, "Обратный вызов не завершен. Отменено оператором", true));
        }

        oCompletionCodeDAO = compCodeRepo.findBySysname(CompCodeSysnameEnum.UNDEFINED);
        if (!oCompletionCodeDAO.isPresent()) {
            log.debug("CompletionCode [" + CompCodeSysnameEnum.UNDEFINED + "] not found in database and will be added");
            compCodeRepo.save(new CompletionCodeDAO(CompCodeNameEnum.UNDEFINED, CompCodeSysnameEnum.UNDEFINED, "Статус обратного вызова не определен", true));
        }
    }

    public CompletionCode getOrCreateCompCode(String sysname) {

        CompletionCode completionCode = null;
        Optional<CompletionCodeDAO> oCompletionCodeDAO = compCodeRepo.findBySysname(sysname);

        if (oCompletionCodeDAO.isPresent())
            completionCode = oCompletionCodeDAO.get().toCompletionCode();
        else if (!sysname.isEmpty()) {
            CompletionCodeDAO completionCodeDAO = new CompletionCodeDAO(sysname,sysname,"Auto generated",false);
            completionCode = compCodeRepo.save(completionCodeDAO).toCompletionCode();
        }

        return completionCode;
    }

    public List<CompletionCode> findAllCompCodes() {

        List<CompletionCodeDAO> CompletionCodeDAO = compCodeRepo.findAll();

        List<CompletionCode> completionCodeList = CompletionCodeDAO
                .stream()
                .map(c -> new CompletionCode(c))
                .collect(Collectors.toList());

        return completionCodeList;
    }

    public CompletionCode findCompCodeBySysname (String name) {

        CompletionCode completionCode = null;

        Optional<CompletionCodeDAO> oCompletionCodeDAO = compCodeRepo.findBySysname(name);
        if (oCompletionCodeDAO.isPresent()) {
            completionCode = oCompletionCodeDAO.get().toCompletionCode();
        }
        return completionCode;
    }

    public CompletionCodeDAO findCompCodeDAOBySysname (String name) {

        CompletionCodeDAO completionCodeDAO = null;

        Optional<CompletionCodeDAO> oCompletionCodeDAO = compCodeRepo.findBySysname(name);
        if (oCompletionCodeDAO.isPresent()) {
            completionCodeDAO = oCompletionCodeDAO.get();
        }
        return completionCodeDAO;
    }



}
