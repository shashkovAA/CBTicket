package ru.wawulya.CBTicket.model;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.wawulya.CBTicket.modelDAO.UserDAO;

import java.util.Arrays;
import java.util.Collection;

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


    public UserDAO toUserDAO() {
        UserDAO userDAO = new UserDAO();
        userDAO.setId(this.id);
        userDAO.setUsername(this.username);
        userDAO.setPassword(this.password);
        userDAO.setFullname(this.fullname);
        userDAO.setEnabled(this.enabled);
        return userDAO;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
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

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", fullname='" + fullname + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", isEnabled=" + enabled +
                '}';
    }
}
