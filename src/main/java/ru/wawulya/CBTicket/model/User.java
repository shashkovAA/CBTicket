package ru.wawulya.CBTicket.model;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.wawulya.CBTicket.modelDAO.RoleDAO;
import ru.wawulya.CBTicket.modelDAO.UserDAO;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String fullname;
    private String username;
    private String password;
    private boolean enabled;
    private Set<Role> roles = new HashSet<>();


    public UserDAO toUserDAO(Set<RoleDAO> roles) {
        UserDAO userDAO = new UserDAO();
        userDAO.setId(this.id);
        userDAO.setUsername(this.username);
        userDAO.setPassword(this.password);
        userDAO.setFullname(this.fullname);
        userDAO.setEnabled(this.enabled);
        userDAO.setRoles(roles);
        return userDAO;
    }

    public void addRole (Role role) {
        roles.add(role);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }


}
