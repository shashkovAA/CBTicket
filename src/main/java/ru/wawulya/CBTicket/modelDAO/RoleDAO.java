package ru.wawulya.CBTicket.modelDAO;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.wawulya.CBTicket.model.Role;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@Table(name = "roles")
public class RoleDAO {
    @Id
    private Long id;
    private String name;

    @Transient
    @ManyToMany(mappedBy = "roles")
    private Set<UserDAO> users;

    public RoleDAO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Role toRole() {
        Role role = new Role();
        role.setName(this.name);
        return role;
    }

}
