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

    @Column(name="viewname")
    private String viewName;

    @Transient
    @ManyToMany(mappedBy = "roles")
    private Set<UserDAO> users;

    public RoleDAO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public RoleDAO(Long id, String name, String viewName) {
        this.id = id;
        this.name = name;
        this.viewName = viewName;
    }

    public Role toRole() {
        Role role = new Role();
        role.setId(id);
        role.setName(name);
        role.setViewName(viewName);
        return role;
    }

}
