package ru.wawulya.CBTicket.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import ru.wawulya.CBTicket.model.TicketParams;
import ru.wawulya.CBTicket.modelDAO.TicketDAO;
import ru.wawulya.CBTicket.modelDAO.TicketParamsDAO;

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
public class TicketParamsSpecification implements Specification<TicketParamsDAO> {

    private TicketParamsFilter ticketParamsFilter;

    public TicketParamsSpecification(TicketParamsFilter ticketParamsFilter) {
        this.ticketParamsFilter = ticketParamsFilter;
    }

    @Override
    public Specification<TicketParamsDAO> and(Specification<TicketParamsDAO> other) {
        return null;
    }

    @Override
    public Specification<TicketParamsDAO> or(Specification<TicketParamsDAO> other) {
        return null;
    }

    @Override
    public Predicate toPredicate(Root<TicketParamsDAO> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

        final List<Predicate> predicates = new ArrayList<>();

        if(ticketParamsFilter.getTicketId()!= null && !ticketParamsFilter.getTicketId().equals(""))
            predicates.add(criteriaBuilder.equal(root.get("ticketDAO").get("id"),  ticketParamsFilter.getTicketId()));

        if(ticketParamsFilter.getCbMaxAttempts()!= null && !ticketParamsFilter.getCbMaxAttempts().equals(""))
            predicates.add(criteriaBuilder.equal(root.get("cbMaxAttempts"), ticketParamsFilter.getCbMaxAttempts()));

        if(ticketParamsFilter.getCbAttemptsTimeout()!= null && !ticketParamsFilter.getCbAttemptsTimeout().equals(""))
            predicates.add(criteriaBuilder.equal(root.get("cbAttemptsTimeout"), ticketParamsFilter.getCbAttemptsTimeout()));

        if(ticketParamsFilter.getCbSource() != null && !ticketParamsFilter.getCbSource().equals(""))
            predicates.add(criteriaBuilder.like(root.get("cbSource"), "%" + ticketParamsFilter.getCbSource() + "%"));

        if(ticketParamsFilter.getCbType() != null && !ticketParamsFilter.getCbType().equals(""))
            predicates.add(criteriaBuilder.like(root.get("cbType"), "%" + ticketParamsFilter.getCbType() + "%"));

        if(ticketParamsFilter.getCbOriginator() != null && !ticketParamsFilter.getCbOriginator().equals(""))
            predicates.add(criteriaBuilder.like(root.get("cbOriginator"), "%" + ticketParamsFilter.getCbOriginator() + "%"));

        if(ticketParamsFilter.getCbUrl() != null && !ticketParamsFilter.getCbUrl().equals(""))
            predicates.add(criteriaBuilder.like(root.get("cbUrl"), "%" + ticketParamsFilter.getCbUrl() + "%"));

        if(ticketParamsFilter.getUcidOld() != null && !ticketParamsFilter.getUcidOld().equals(""))
            predicates.add(criteriaBuilder.like(root.get("ucidOld"), "%" + ticketParamsFilter.getUcidOld() + "%"));


        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }

}
