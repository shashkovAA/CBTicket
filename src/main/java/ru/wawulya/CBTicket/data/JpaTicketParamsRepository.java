package ru.wawulya.CBTicket.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.wawulya.CBTicket.modelDAO.TicketDAO;
import ru.wawulya.CBTicket.modelDAO.TicketParamsDAO;

import java.util.List;

public interface JpaTicketParamsRepository extends JpaRepository<TicketParamsDAO, Long>, JpaSpecificationExecutor {

    Page<TicketParamsDAO> findAll(Pageable pageable);
    Page<TicketParamsDAO> findAll(Specification specification, Pageable pageable);
    List<TicketParamsDAO> findAll(Specification specification);
}
