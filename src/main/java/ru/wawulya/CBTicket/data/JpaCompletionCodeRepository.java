package ru.wawulya.CBTicket.data;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.wawulya.CBTicket.modelDAO.CompletionCodeDAO;
import ru.wawulya.CBTicket.modelDAO.TicketDAO;

import java.util.Optional;

public interface JpaCompletionCodeRepository extends JpaRepository<CompletionCodeDAO, Long> {

    CompletionCodeDAO findByName(String name);
    Optional<CompletionCodeDAO> findBySysname(String sysname);
}
