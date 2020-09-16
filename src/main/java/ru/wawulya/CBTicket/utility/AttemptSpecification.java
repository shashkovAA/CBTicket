package ru.wawulya.CBTicket.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import ru.wawulya.CBTicket.modelDAO.ApiLogDAO;
import ru.wawulya.CBTicket.modelDAO.AttemptDAO;

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
public class AttemptSpecification implements Specification<AttemptDAO> {

    private AttemptFilter attemptFilter;

    public AttemptSpecification(AttemptFilter attemptFilter) {
        this.attemptFilter = attemptFilter;
    }

    @Override
    public Specification<AttemptDAO> and(Specification<AttemptDAO> other) {
        return null;
    }

    @Override
    public Specification<AttemptDAO> or(Specification<AttemptDAO> other) {
        return null;
    }

    @Override
    public Predicate toPredicate(Root<AttemptDAO> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

        final List<Predicate> predicates = new ArrayList<>();

        if(attemptFilter.getTicketId()!= null && !attemptFilter.getTicketId().equals(""))
            predicates.add(criteriaBuilder.equal(root.get("ticketDAO").get("id"),  attemptFilter.getTicketId()));

        if(attemptFilter.getCallId()!= null && !attemptFilter.getCallId().equals(""))
            predicates.add(criteriaBuilder.like(root.get("callId"), "%" + attemptFilter.getCallId() + "%"));

        if(attemptFilter.getUcid()!= null && !attemptFilter.getUcid().equals(""))
            predicates.add(criteriaBuilder.like(root.get("ucid"), "%" + attemptFilter.getUcid() + "%"));

        if(attemptFilter.getPhantomNumber() != null && !attemptFilter.getPhantomNumber().equals(""))
            predicates.add(criteriaBuilder.like(root.get("phantomNumber"), "%" + attemptFilter.getPhantomNumber() + "%"));

        if(attemptFilter.getOperatorNumber() != null && !attemptFilter.getOperatorNumber().equals(""))
            predicates.add(criteriaBuilder.like(root.get("operatorNumber"), "%" + attemptFilter.getOperatorNumber() + "%"));

        if(attemptFilter.getCompCode() != null && !attemptFilter.getCompCode().equals(""))
            predicates.add(criteriaBuilder.equal(root.get("completionCodeDAO").get("id"), attemptFilter.getCompCode()));

        if(attemptFilter.getStartDate() != null && !attemptFilter.getStartDate().equals("")) {
            String dateTimeStr = attemptFilter.getStartDate() + " " + attemptFilter.getStartTime();
            Timestamp timestamp = getTimestamp(dateTimeStr);
            if (timestamp != null)
                predicates.add(criteriaBuilder.greaterThan(root.get("attemptStart"), timestamp));
        }

        if(attemptFilter.getEndDate() != null && !attemptFilter.getEndDate().equals("")) {
            String dateTimeStr = attemptFilter.getEndDate() + " " + attemptFilter.getEndTime();
            Timestamp timestamp = getTimestamp(dateTimeStr);
            if (timestamp != null)
                predicates.add(criteriaBuilder.lessThan(root.get("attemptStart"), timestamp));
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
