package ru.wawulya.CBTicket.data;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.wawulya.CBTicket.model.User;
import ru.wawulya.CBTicket.modelDAO.UserDAO;

public interface JpaUserRepository extends JpaRepository<UserDAO, Long> {

    UserDAO findByUsername(String username);
    }
