package ru.wawulya.CBTicket.model;

import lombok.Data;

@Data
public class Property {
    private Long id;
    private String name;
    private String value;
    private String description;
    private boolean editable;
    private boolean removable;
}
