package ru.wawulya.CBTicket.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.wawulya.CBTicket.model.User;
import ru.wawulya.CBTicket.modelCache.Users;


@Slf4j
@Service
public class UserRepositoryUserDetailsService implements UserDetailsService {

    private JpaUserRepository userRepo;
    private Users users;

    @Autowired
    public UserRepositoryUserDetailsService(JpaUserRepository userRepo, Users users) {
        this.userRepo = userRepo;
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //Для поиска в БД
        //User user = userRepo.findByUsername(username).toUser();

        //Для поиска в кэше
        User user = users.findByUsername(username);

        if (user != null) {
            log.warn("User found :" + user.toString());
            return user;
        }
        throw new UsernameNotFoundException(
                "User '" + username + "' not found");
    }
}
