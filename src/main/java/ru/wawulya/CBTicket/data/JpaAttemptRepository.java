package ru.wawulya.CBTicket.data;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.wawulya.CBTicket.modelDAO.AttemptDAO;


public interface JpaAttemptRepository extends JpaRepository<AttemptDAO, Long> {
}
