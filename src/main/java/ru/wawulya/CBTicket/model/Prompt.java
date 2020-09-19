package ru.wawulya.CBTicket.model;

import lombok.Data;

@Data
public class Prompt {
    private Long id;
    private String name;
    private String filepath;
    private String filename;
    private String description;
}
