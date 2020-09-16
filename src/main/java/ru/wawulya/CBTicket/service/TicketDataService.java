package ru.wawulya.CBTicket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.wawulya.CBTicket.data.JpaTicketRepository;
import ru.wawulya.CBTicket.enums.CompCodeSysnameEnum;
import ru.wawulya.CBTicket.model.Attempt;
import ru.wawulya.CBTicket.model.Ticket;
import ru.wawulya.CBTicket.modelDAO.AttemptDAO;
import ru.wawulya.CBTicket.modelDAO.CompletionCodeDAO;
import ru.wawulya.CBTicket.modelDAO.TicketDAO;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TicketDataService {

    private JpaTicketRepository ticketRepo;
    private CompCodeDataService compCodeDataService;

    @Autowired
    public TicketDataService(JpaTicketRepository ticketRepo, CompCodeDataService compCodeDataService) {
        this.ticketRepo = ticketRepo;
        this.compCodeDataService = compCodeDataService;
    }

    public TicketDAO save (TicketDAO ticketDAO) {
        return ticketRepo.save(ticketDAO);
    }

    public Ticket getTicketById (Long id) {
        Ticket ticket = null;

        Optional<TicketDAO> oTicketDAO = ticketRepo.findById(id);

        if (oTicketDAO.isPresent())
            ticket = new Ticket(oTicketDAO.get());

        return ticket;
    }

    public List<Ticket> getTicketsByNumber (String number) {
        List<Ticket> tikets = new ArrayList<>();

        List<TicketDAO> ticketDAOs = ticketRepo.findAllByCbNumber(number);

        if(ticketDAOs.size() != 0) {
            tikets = ticketDAOs
                    .stream()
                    .map(t -> new Ticket(t))
                    .collect(Collectors.toList());
        }

        return tikets;
    }

    public List<Ticket> getTicketsForCallBack (Timestamp currentTime, int count) {

        List<Ticket> tikets = new ArrayList<>();

        CompletionCodeDAO complCodeCalling = compCodeDataService.findCompCodeDAOBySysname(CompCodeSysnameEnum.DIALING);

        List<TicketDAO> jobList = ticketRepo.findCallBackList(currentTime)
                .stream()
                .filter(t -> t.getAttemptCount() < t.getTicketParamsDAO().getCbMaxAttempts())
                .limit(count)
                .peek(t -> t.setAttemptCount(t.getAttemptCount() + 1))
                .peek(t -> t.setCompletionCodeDAO(complCodeCalling))
                .collect(Collectors.toList());

        jobList.forEach(ticketDAO -> {
            AttemptDAO attemptDAO = new AttemptDAO(ticketDAO,complCodeCalling);
            ticketDAO.addAttemptDAO(attemptDAO);
            ticketRepo.save(ticketDAO);
        });

        tikets = jobList
                .stream()
                .map(t -> new Ticket(t))
                .collect(Collectors.toList());

        return tikets;
    }

    public Ticket updateTicket(Ticket ticket, Timestamp currentTime) {

        CompletionCodeDAO lastComplCodeDAO = new CompletionCodeDAO(ticket.getLastCompletionCode());

        int lastAttemptIndex = ticket.getAttemptCount() - 1;
        Attempt lastAttempt = ticket.getAttempts().get(lastAttemptIndex);

        TicketDAO ticketDAO = ticketRepo.getOne(ticket.getId());
        ticketDAO.setCompletionCodeDAO(lastComplCodeDAO);

        if (!lastComplCodeDAO.isRecall())
            ticketDAO.setFinished(true);

        if (ticket.getAttemptCount() >= ticket.getTicketParams().getCbMaxAttempts())
            ticketDAO.setFinished(true);
        else {
            //Timestamp curCbDate = ticket.getCbDate();
            Timestamp nextCbDate = currentTime;
            int timeout = ticket.getTicketParams().getCbAttemptsTimeout();
            nextCbDate.setTime(currentTime.getTime() + TimeUnit.MINUTES.toMillis(timeout));

            ticketDAO.setCbDate(nextCbDate);
        }

        AttemptDAO attemptDAO = ticketDAO.getAttemptDAOs().get(lastAttemptIndex);
        attemptDAO.update(lastAttempt);
        attemptDAO.setAttemptStop(currentTime);
        attemptDAO.setCompletionCodeDAO(lastComplCodeDAO);

        ticketRepo.save(ticketDAO);

        return new Ticket(ticketDAO);
    }

    public Ticket cancelTicketById(Long id) {

        Ticket ticket = null;
        TicketDAO ticketDAO = null;

        CompletionCodeDAO completionCodeDAO = compCodeDataService.findCompCodeDAOBySysname(CompCodeSysnameEnum.CANCELED);

        Optional<TicketDAO> opTicketDAO = ticketRepo.findById(id);

        if (opTicketDAO.isPresent()) {
            ticketDAO = opTicketDAO.get();
            ticketDAO.setCompletionCodeDAO(completionCodeDAO);
            ticketDAO.setFinished(true);
            ticketRepo.save(ticketDAO);

        }

        return new Ticket(ticketDAO);

    }

    public Ticket deleteTicketById(Long id) {

        Ticket ticket = null;

        Optional<TicketDAO> opTicketDAO = ticketRepo.findById(id);

        if (!opTicketDAO.isPresent()) {
            return ticket;
        }

        TicketDAO ticketDAO = opTicketDAO.get();

        if (ticketDAO.isFinished()) {
            ticket = new Ticket(ticketDAO);
            ticketRepo.delete(ticketDAO);
        }

        return ticket;

    }

    public Ticket getOneTicketInDialingState() {

        Ticket ticket = null;

        CompletionCodeDAO complCodeCalling = compCodeDataService.findCompCodeDAOBySysname(CompCodeSysnameEnum.DIALING);

        TicketDAO ticketDAO = ticketRepo.findCallBackListInDialingState(complCodeCalling.getId());

        if (ticketDAO !=null)
            ticket = new Ticket(ticketDAO);

        return ticket;
    }

    public List<Ticket> findAllByCbNumber(String cbNumber) {

        List<TicketDAO> uncompleteTickets= ticketRepo.findAllByCbNumber(cbNumber);
        List<Ticket> tickets = uncompleteTickets.stream().filter(t->!t.isFinished()).map(t->new Ticket(t)).collect(Collectors.toList());

        return tickets;
    }

    public List<Ticket> findAll(Specification specification){

        return ticketRepo.findAll(specification).stream().map(TicketDAO::toTicket).collect(Collectors.toList());
    }

    public Page<Ticket> findAll(Pageable pageable){

        return ticketRepo.findAll(pageable).map(TicketDAO::toTicket);
    }

    public Page<Ticket> findAll(Specification specification, Pageable pageable){

        return ticketRepo.findAll(specification, pageable).map(TicketDAO::toTicket);
    }
}
