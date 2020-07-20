package ru.wawulya.CBTicket.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.wawulya.CBTicket.model.Ticket;
import ru.wawulya.CBTicket.modelDAO.TicketDAO;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface JpaTicketRepository extends JpaRepository<TicketDAO, Long> {

    @Query(value = "select t.* from ticket t inner join completion_code c on t.last_completion_code_id = c.id  where t.finished = 0 and c.recall = 1 and cb_date < :timestamp order by cb_date",
            nativeQuery = true)
    List<TicketDAO> findCallBackList(@Param("timestamp") Timestamp curTime);

    @Query(value = "select top 1 t.* from ticket t where t.last_completion_code_id = :id order by cb_date",
            nativeQuery = true)
    TicketDAO findCallBackListInDialingState(@Param("id") Long id);


    /*@Modifying
    @Transactional
    @Query(value = "update ticket set attempt_count = :attempts where id = :ticketId",
            nativeQuery = true)
    int updateTicketAttemptCount(@Param("attempts") int attempts, @Param("ticketId") Long id);

    @Modifying
    @Transactional
    @Query(value = "update ticket set last_completion_code_id= :compCodeId where id = :ticketId",
            nativeQuery = true)
    int updateTicketCompletionCode(@Param("compCodeId") Long compcodeId, @Param("ticketId") Long id);*/


    @Query(value = "select * from ticket t where  t.cb_number = :cbNumber order by cb_date",
            nativeQuery = true)
    List<TicketDAO> findAllByCbNumber(String cbNumber);

}
