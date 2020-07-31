package ru.wawulya.CBTicket.modelDAO;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.wawulya.CBTicket.model.Property;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="property")
public class PropertyDAO {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @CsvBindByName
    @Column(nullable = false, unique=true)
    private String name;

    @CsvBindByName
    @Column(nullable = false)
    private String value;

    @CsvBindByName
    private String description;

    @CsvBindByName
    @Column(columnDefinition = "bit default 1")
    private boolean editable;

    @CsvBindByName
    @Column(columnDefinition = "bit default 1")
    private boolean removable;

    public PropertyDAO(String name, String value, String description, boolean editable, boolean removable) {
        this.name = name;
        this.value = value;
        this.description = description;
        this.editable = editable;
        this.removable = removable;
    }

    public Property toProperty() {
        Property prop = new Property();
        prop.setId(this.id);
        prop.setName(this.name);
        prop.setValue(this.value);
        prop.setDescription(this.description);
        prop.setEditable(this.editable);
        prop.setRemovable(this.removable);
        return prop;
    }

    @Override
    public String toString() {
        return "PropertyDAO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", description='" + description + '\'' +
                ", editable=" + editable +
                ", removable=" + removable +
                '}';
    }
}
