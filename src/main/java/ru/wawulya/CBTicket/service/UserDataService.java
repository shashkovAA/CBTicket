package ru.wawulya.CBTicket.service;

import lombok.extern.slf4j.Slf4j;
import org.omg.CosNaming.BindingIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.wawulya.CBTicket.data.JpaRoleRepository;
import ru.wawulya.CBTicket.data.JpaUserRepository;
import ru.wawulya.CBTicket.model.User;
import ru.wawulya.CBTicket.modelCache.Users;
import ru.wawulya.CBTicket.modelDAO.RoleDAO;
import ru.wawulya.CBTicket.modelDAO.UserDAO;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserDataService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    private JpaUserRepository userRepo;
    private JpaRoleRepository roleRepo;
    private Users users;

    @Autowired
    public UserDataService(JpaUserRepository userRepo, JpaRoleRepository roleRepo, Users users) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.users = users;

        initUsers();
    }

    public void initUsers() {

        log.info("Initializing users ...");

        List<User> userList = getAllUsers();
        log.debug("Loaded " + userList.size()+ " users from DB.");

        userList.forEach(user-> users.addUser(user));
        log.debug(users.toString());

        users.addUser(createDefaultUser());

    }

    public List<User> getAllUsers() {
        List<UserDAO> usersDAO = userRepo.findAll();

        List<User> users = usersDAO
                .stream()
                .map(u -> u.toUser())
                .collect(Collectors.toList());
        return users;
    }

    public User addUser(User user) {
        UserDAO userDAO;
        Set<RoleDAO> roles = getRoleDAOs(user);
        User usr = null;
        try {
            String encrytedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encrytedPassword);
            userDAO = userRepo.save(user.toUserDAO(roles));
            usr = userDAO.toUser();
        } catch (Exception except) {
            log.error("Inserting user with non-unique username is not allowed!");
        }

        return usr;
    }

    public User createDefaultUser() {
        User defaultUser = new User();
        defaultUser.setId(0L);
        defaultUser.setUsername("partner");
        defaultUser.setPassword("$2a$10$4Qk1ZhxOwlitmE63mljScOx9oKSY9jtMurwYdDV8aRNXV76sLrE2i");
        defaultUser.setEnabled(true);
        defaultUser.setRoles(Collections.singleton(roleRepo.findByName("ROLE_USER").toRole()));
        return defaultUser;
    }

    @Async
    public User updateUser(User user) {

        UserDAO userDAO= null;
        Set<RoleDAO> roles = getRoleDAOs(user);
        Optional<UserDAO> oUserDAO = userRepo.findById(user.getId());

        if (oUserDAO.isPresent()) {

            userDAO = oUserDAO.get();

            userDAO.setUsername(user.getUsername());
            userDAO.setPassword(passwordEncoder.encode(user.getPassword()));
            userDAO.setFullname(user.getFullname());
            userDAO.setEnabled(user.isEnabled());
            userDAO.setRoles(roles);

        }
        userDAO = userRepo.save(userDAO);

        return userDAO.toUser();
    }

    private Set<RoleDAO> getRoleDAOs(User user) {
        return user.getAuthorities().stream().map(r->roleRepo.findByName(r.getAuthority())).collect(Collectors.toSet());
    }

    @Async
    public Long deleteUser(Long id) {
        Long userId = 0L;

        Optional<UserDAO> oUserDAO = userRepo.findById(id);

        if (oUserDAO.isPresent()) {
            userId = oUserDAO.get().getId();
            userRepo.deleteById(userId);
        }

        return userId;
    }

}
