package ru.wawulya.CBTicket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.wawulya.CBTicket.modelDAO.AttemptDAO;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attempt {

    private Long id;
    private Long ticketId;
    private String ucid;
    private String callId;
    private Timestamp attemptStart;
    private Timestamp attemptStop;
    private String phantomNumber;
    private String operatorNumber;
    private Long completionCodeId;

}
