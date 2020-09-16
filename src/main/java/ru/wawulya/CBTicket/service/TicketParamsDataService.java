package ru.wawulya.CBTicket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.wawulya.CBTicket.data.JpaTicketParamsRepository;
import ru.wawulya.CBTicket.data.JpaTicketRepository;
import ru.wawulya.CBTicket.model.Ticket;
import ru.wawulya.CBTicket.model.TicketParams;
import ru.wawulya.CBTicket.modelDAO.TicketDAO;
import ru.wawulya.CBTicket.modelDAO.TicketParamsDAO;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketParamsDataService {

    private JpaTicketParamsRepository ticketParamsRepo;

    @Autowired
    public TicketParamsDataService(JpaTicketParamsRepository ticketParamsRepo) {
        this.ticketParamsRepo = ticketParamsRepo;
    }

    public List<TicketParams> findAll(Specification specification){

        return ticketParamsRepo.findAll(specification).stream().map(TicketParamsDAO::toTicketParams).collect(Collectors.toList());
    }

    public Page<TicketParams> findAll(Pageable pageable){

        return ticketParamsRepo.findAll(pageable).map(TicketParamsDAO::toTicketParams);
    }

    public Page<TicketParams> findAll(Specification specification, Pageable pageable){

        return ticketParamsRepo.findAll(specification, pageable).map(TicketParamsDAO::toTicketParams);
    }
}
