package ru.wawulya.CBTicket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigFile {

    private String filename;
    private String lastModifyDate;

}
