package ru.wawulya.CBTicket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.wawulya.CBTicket.data.JpaRoleRepository;
import ru.wawulya.CBTicket.model.Role;
import ru.wawulya.CBTicket.modelDAO.RoleDAO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoleDataService {

    private JpaRoleRepository roleRepo;

    @Autowired
    public RoleDataService(JpaRoleRepository roleRepo) {
        this.roleRepo = roleRepo;

        initRoles();
    }

    public void initRoles() {
        log.info("Initializing roles ...");

        Optional<RoleDAO> oRoleDAO;

        oRoleDAO = roleRepo.findById(1L);
        if (!oRoleDAO.isPresent()) {
            log.debug("Role [ROLE_USER] not found in database and will be added");
            roleRepo.save(new RoleDAO(1L, "ROLE_USER", "USER"));
        }

        oRoleDAO = roleRepo.findById(2L);
        if (!oRoleDAO.isPresent()) {
            log.debug("Role [ROLE_ADMIN] not found in database and will be added");
            roleRepo.save(new RoleDAO(2L, "ROLE_ADMIN", "ADMIN"));
        }

        oRoleDAO = roleRepo.findById(3L);
        if (!oRoleDAO.isPresent()) {
            log.debug("Role [ROLE_API] not found in database and will be added");
            roleRepo.save(new RoleDAO(3L, "ROLE_API", "API"));
       }
    }

    public RoleDAO findByName (String name) {
        return roleRepo.findByName(name);
    }

   /* public List<RoleDAO> findAll() {
        return roleRepo.findAll();
    }*/

    public List<Role> findAll() {
        return roleRepo.findAll().stream().map(r->r.toRole()).collect(Collectors.toList());
    }
}
