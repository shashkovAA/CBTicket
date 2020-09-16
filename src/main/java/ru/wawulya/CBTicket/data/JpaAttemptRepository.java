package ru.wawulya.CBTicket.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.wawulya.CBTicket.modelDAO.ApiLogDAO;
import ru.wawulya.CBTicket.modelDAO.AttemptDAO;

import java.util.List;


public interface JpaAttemptRepository extends JpaRepository<AttemptDAO, Long>, JpaSpecificationExecutor {

    Page<AttemptDAO> findAll(Pageable pageable);
    Page <AttemptDAO> findAll(Specification specification, Pageable pageable);
    List<AttemptDAO> findAll(Specification specification);
}
