package ru.wawulya.CBTicket.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
@Component
public class Users {

    private Map<Long,User> users = new HashMap<>();

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
            usr.setPassword(user.getPassword());
            usr.setFullname(user.getFullname());
            usr.setEnabled(user.isEnabled());
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
