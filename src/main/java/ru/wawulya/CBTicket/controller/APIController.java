package ru.wawulya.CBTicket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.wawulya.CBTicket.data.JpaUserRepository;
import ru.wawulya.CBTicket.model.Session;
import ru.wawulya.CBTicket.model.User;
import ru.wawulya.CBTicket.modelDAO.UserDAO;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/api")
public class APIController {

    private JpaUserRepository userRepo;

    public APIController(JpaUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping(value = "/test/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User testGetUser(@PathVariable String username) {
        UUID sessionId = getSession().getUuid();
        log.info(sessionId + " | REST GET /test/" + username);

        UserDAO userDAO = userRepo.findByUsername(username);
        log.info(sessionId + " | Get User from DB " + userDAO.toString());

        return userDAO.toUser();
    }

    @Lookup
    public Session getSession() {
        return null;
    }

}
