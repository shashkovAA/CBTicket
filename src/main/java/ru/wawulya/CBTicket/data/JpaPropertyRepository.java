package ru.wawulya.CBTicket.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.wawulya.CBTicket.modelDAO.PropertyDAO;

import java.util.Optional;

public interface JpaPropertyRepository extends JpaRepository<PropertyDAO, Long> {

    Optional<PropertyDAO> findByNameIgnoringCase(String name);

    Page<PropertyDAO> findAll(Pageable pageable);

}
