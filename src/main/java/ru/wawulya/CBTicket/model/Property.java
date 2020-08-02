package ru.wawulya.CBTicket.model;

import lombok.Data;
import ru.wawulya.CBTicket.modelDAO.PropertyDAO;

@Data
public class Property {
    private Long id;
    private String name;
    private String value;
    private String description;
    private boolean editable;

    public PropertyDAO toPropertyDAO() {
        PropertyDAO propertyDAO = new PropertyDAO();
        propertyDAO.setId(id);
        propertyDAO.setName(name);
        propertyDAO.setValue(value);
        propertyDAO.setDescription(description);
        propertyDAO.setEditable(editable);
        return propertyDAO;
    }

}
