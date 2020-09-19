package ru.wawulya.CBTicket.model;

import lombok.Data;

@Data
public class Source {

    private Long id;
    private String name;
    private String url;
    private String skpid;
    private Prompt prompt;
    private String description;

}

