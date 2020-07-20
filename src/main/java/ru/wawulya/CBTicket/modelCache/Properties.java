package ru.wawulya.CBTicket.modelCache;

import org.springframework.stereotype.Component;
import ru.wawulya.CBTicket.model.Property;
import ru.wawulya.CBTicket.model.User;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class Properties {

    private Map<Long, Property> properties = new TreeMap<>();

    public List<Property> getAllProperties() {
/*        List<Property> propertyList = new ArrayList<>();
        properties.forEach((id,property) -> propertyList.add(property));*/
        return properties.values().stream().collect(Collectors.toList());
    }

    public void addProperty(Property property) {
        properties.put(property.getId(),property);
    }

    public void updateProperty(Property property) {

        Property prop = properties.get(property.getId());

        if (prop != null) {
            prop.setName(property.getName());
            prop.setValue(property.getValue());
            prop.setDescription(property.getDescription());
            prop.setEditable(property.isEditable());
            prop.setRemovable(property.isRemovable());
        }
    }

    public void deleteProperty(Long id) {
        properties.remove(id);
    }

    public Property getPropertyById(Long id) {
        Property property = properties.get(id);
        return property;
    }

    public Property getPropertyByName(String name) {
        Property property = null;

        for (Property prop : properties.values()) {
            if (prop.getName().equals(name))
                property = prop;
        }

        return property;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        properties.forEach((id,property) -> sb.append(property.toString() + ",")  );
        return "Properties :" + sb.toString();
    }

}
