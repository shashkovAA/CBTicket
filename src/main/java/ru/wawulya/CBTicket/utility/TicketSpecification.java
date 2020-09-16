package ru.wawulya.CBTicket.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import ru.wawulya.CBTicket.modelDAO.AttemptDAO;
import ru.wawulya.CBTicket.modelDAO.TicketDAO;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class TicketSpecification implements Specification<TicketDAO> {

    private TicketFilter ticketFilter;

    public TicketSpecification(TicketFilter ticketFilter) {
        this.ticketFilter = ticketFilter;
    }

    @Override
    public Specification<TicketDAO> and(Specification<TicketDAO> other) {
        return null;
    }

    @Override
    public Specification<TicketDAO> or(Specification<TicketDAO> other) {
        return null;
    }

    @Override
    public Predicate toPredicate(Root<TicketDAO> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

        final List<Predicate> predicates = new ArrayList<>();

        if(ticketFilter.getId()!= null && !ticketFilter.getId().equals(""))
            predicates.add(criteriaBuilder.equal(root.get("id"),  ticketFilter.getId()));

        if(ticketFilter.getCbNumber()!= null && !ticketFilter.getCbNumber().equals(""))
            predicates.add(criteriaBuilder.like(root.get("cbNumber"), "%" + ticketFilter.getCbNumber() + "%"));

        if(ticketFilter.getAttemptCount()!= null && !ticketFilter.getAttemptCount().equals(""))
            predicates.add(criteriaBuilder.equal(root.get("attemptCount"), ticketFilter.getAttemptCount()));

        if(ticketFilter.getCompCodeName() != null && !ticketFilter.getCompCodeName().equals(""))
            predicates.add(criteriaBuilder.like(root.get("completionCodeDAO").get("name"), "%" + ticketFilter.getCompCodeName() + "%"));

        if(ticketFilter.getFinished() != null && !ticketFilter.getFinished().equals(""))
            predicates.add(criteriaBuilder.equal(root.get("finished"), ticketFilter.getFinished()));

        if(ticketFilter.getStartDate() != null && !ticketFilter.getStartDate().equals("")) {
            String dateTimeStr = ticketFilter.getStartDate() + " " + ticketFilter.getStartTime();
            Timestamp timestamp = getTimestamp(dateTimeStr);
            if (timestamp != null)
                predicates.add(criteriaBuilder.greaterThan(root.get("cbDate"), timestamp));
        }

        if(ticketFilter.getEndDate() != null && !ticketFilter.getEndDate().equals("")) {
            String dateTimeStr = ticketFilter.getEndDate() + " " + ticketFilter.getEndTime();
            Timestamp timestamp = getTimestamp(dateTimeStr);
            if (timestamp != null)
                predicates.add(criteriaBuilder.lessThan(root.get("cbDate"), timestamp));
        }


        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    private Timestamp getTimestamp(String str) {
        Timestamp timestamp = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            Date date = dateFormat.parse(str);
            timestamp = new Timestamp(date.getTime());
            log.info(" | Create timestamp is successfully [" + timestamp + "]");

        } catch(Exception e) { //this generic but you can control another types of exception
            log.error(" | Invalid format " + str + ".");
        }

        return timestamp;
    }
}
