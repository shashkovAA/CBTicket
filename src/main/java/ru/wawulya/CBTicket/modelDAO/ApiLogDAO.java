package ru.wawulya.CBTicket.modelDAO;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ru.wawulya.CBTicket.enums.LogLevel;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@NoArgsConstructor
@Table(name="apilog")
public class ApiLogDAO {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column
    @CreationTimestamp
    private Timestamp date;

    private String level;

    private String username;

    @Column(nullable = false)
    private String method;

    @Column(name="api_url", columnDefinition = "varchar(max)", nullable = false)
    private String apiUrl;

    @Column(name="request_body", columnDefinition = "varchar(max)")
    private String requestBody;

    @Column(name="response_body", columnDefinition = "varchar(max)")
    private String responseBody;

    @Column(name="status_code")
    private String statusCode;

    @Column(columnDefinition = "varchar(max)")
    private String message;

    @Column(name = "session_id", columnDefinition = "varchar(50)")
    private String sessionId;


    public ApiLogDAO(String level, String method, String apiUrl, String requestBody, String responseBody, String statusCode) {
        this.level = level;
        this.method = method;
        this.apiUrl = apiUrl;
        this.requestBody = requestBody;
        this.responseBody = responseBody;
        this.statusCode = statusCode;
    }

    public ApiLogDAO(String sessionId, String level, String method, String apiUrl, String requestBody, String responseBody, String statusCode) {
        this.level = level;
        this.method = method;
        this.apiUrl = apiUrl;
        this.requestBody = requestBody;
        this.responseBody = responseBody;
        this.statusCode = statusCode;
        this.sessionId = sessionId;
    }

    public ApiLogDAO(String sessionId, String level, String method, String apiUrl, String requestBody, String responseBody, String statusCode, String message) {
        this.level = level;
        this.method = method;
        this.apiUrl = apiUrl;
        this.requestBody = requestBody;
        this.responseBody = responseBody;
        this.statusCode = statusCode;
        this.sessionId = sessionId;
        this.message = message;
    }
}
