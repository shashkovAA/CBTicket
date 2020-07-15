package ru.wawulya.CBTicket.data;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.wawulya.CBTicket.modelDAO.ApiLogDAO;

public interface JpaApiLogtRepository extends JpaRepository<ApiLogDAO, Long> {

}
