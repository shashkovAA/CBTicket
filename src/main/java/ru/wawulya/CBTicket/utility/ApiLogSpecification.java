package ru.wawulya.CBTicket.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import ru.wawulya.CBTicket.modelDAO.ApiLogDAO;

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
public class ApiLogSpecification implements Specification<ApiLogDAO> {

    private ApiLogSearch apiLogSearch;

    public ApiLogSpecification(ApiLogSearch apiLogSearch) {
        this.apiLogSearch = apiLogSearch;
    }

    @Override
    public Specification<ApiLogDAO> and(Specification<ApiLogDAO> other) {
        return null;
    }

    @Override
    public Specification<ApiLogDAO> or(Specification<ApiLogDAO> other) {
        return null;
    }

    @Override
    public Predicate toPredicate(Root<ApiLogDAO> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

        final List<Predicate> predicates = new ArrayList<>();

        if(apiLogSearch.getUsername()!= null && !apiLogSearch.getUsername().equals(""))
            predicates.add(criteriaBuilder.like(root.get("username"), "%" + apiLogSearch.getUsername() + "%"));

        if(apiLogSearch.getLevel()!= null && !apiLogSearch.getLevel().equals(""))
            predicates.add(criteriaBuilder.like(root.get("level"), "%" + apiLogSearch.getLevel() + "%"));

        if(apiLogSearch.getMethod()!= null && !apiLogSearch.getMethod().equals(""))
            predicates.add(criteriaBuilder.like(root.get("method"), "%" + apiLogSearch.getMethod() + "%"));

        if(apiLogSearch.getApiUrl() != null && !apiLogSearch.getApiUrl().equals(""))
            predicates.add(criteriaBuilder.like(root.get("apiUrl"), "%" + apiLogSearch.getApiUrl() + "%"));

        if(apiLogSearch.getHost() != null && !apiLogSearch.getHost().equals(""))
            predicates.add(criteriaBuilder.like(root.get("host"), "%" + apiLogSearch.getHost() + "%"));

        if(apiLogSearch.getSessionId() != null && !apiLogSearch.getSessionId().equals(""))
            predicates.add(criteriaBuilder.like(root.get("sessionId"), "%" + apiLogSearch.getSessionId() + "%"));

        if(apiLogSearch.getStartDate() != null && !apiLogSearch.getStartDate().equals("")) {
            String dateTimeStr = apiLogSearch.getStartDate() + " " + apiLogSearch.getStartTime();
            Timestamp timestamp = getTimestamp(dateTimeStr);
            if (timestamp != null)
                predicates.add(criteriaBuilder.greaterThan(root.get("date"), timestamp));
        }

        if(apiLogSearch.getEndDate() != null && !apiLogSearch.getEndDate().equals("")) {
            String dateTimeStr = apiLogSearch.getEndDate() + " " + apiLogSearch.getEndTime();
            Timestamp timestamp = getTimestamp(dateTimeStr);
            if (timestamp != null)
                predicates.add(criteriaBuilder.lessThan(root.get("date"), timestamp));
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
            log.error(" | Invalid format "+str+".");
        }

        return timestamp;
    }
}
