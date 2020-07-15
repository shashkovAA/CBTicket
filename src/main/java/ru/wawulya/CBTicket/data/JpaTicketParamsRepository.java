package ru.wawulya.CBTicket.data;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.wawulya.CBTicket.modelDAO.TicketParamsDAO;

public interface JpaTicketParamsRepository extends JpaRepository<TicketParamsDAO, Long> {
}
