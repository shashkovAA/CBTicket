package ru.wawulya.CBTicket.model;

import lombok.*;
import ru.wawulya.CBTicket.modelDAO.CompletionCodeDAO;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CompletionCode {
    private Long id;
    private String name;
    private String sysname;
    private String description;
    private boolean recall;

    public CompletionCode(CompletionCodeDAO completionCodeDAO) {
        id = completionCodeDAO.getId();
        name = completionCodeDAO.getName();
        sysname = completionCodeDAO.getSysname();
        description = completionCodeDAO.getDescription();
        recall = completionCodeDAO.isRecall();
    }
}



