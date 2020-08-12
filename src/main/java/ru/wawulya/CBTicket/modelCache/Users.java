package ru.wawulya.CBTicket.modelCache;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.wawulya.CBTicket.model.User;

import java.util.*;

@Component
public class Users {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Map<Long, User> users = new TreeMap<>();

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        users.forEach((id,user) -> userList.add(user));
        return userList;
    }

    public void addUser(User user) {
        users.put(user.getId(),user);
    }

    public void updateUser(User user) {

        User usr = users.get(user.getId());

        if (usr != null) {
            usr.setUsername(user.getUsername());
            usr.setPassword(passwordEncoder.encode(user.getPassword()));
            usr.setFullname(user.getFullname());
            usr.setEnabled(user.isEnabled());
            usr.setRoles(user.getRoles());
        }
    }

    public void deleteUser(Long id) {
        users.remove(id);
    }

    public User findByUsername(String username) {
        User  usr = null;

        for (User user : users.values()) {
            if (user.getUsername().equals(username)) usr = user;
        }

        return usr;
    }

    public int size() {
        return users.size();
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        users.forEach((id,user) -> sb.append(user.toString()) );
        return "Users :" + sb.toString();
    }
}
