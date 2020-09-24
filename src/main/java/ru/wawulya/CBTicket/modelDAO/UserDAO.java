package ru.wawulya.CBTicket.modelDAO;


import lombok.Data;
import lombok.NoArgsConstructor;

import ru.wawulya.CBTicket.model.User;

import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@Table(name="users")
public class UserDAO {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String fullname;

    @Column(nullable = false, unique=true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name="enabled")
    private boolean isEnabled;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<RoleDAO> roles = new HashSet<>();


    public User toUser() {
        User user = new User();
        user.setId(this.id);
        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setFullname(this.fullname);
        user.setEnabled(this.isEnabled);
        user.setRoles(this.roles.stream().map(RoleDAO::toRole).collect(Collectors.toSet()));
        return user;
    }


}
