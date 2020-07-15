package ru.wawulya.CBTicket.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class Utils {

    public Timestamp getTimeStamp(UUID sessionId, String jsonDateParamName, String customDate) {

        Timestamp timestamp = null;

        if (customDate.isEmpty()) {
            timestamp = new Timestamp(new Date().getTime());
            log.info(sessionId + " | Create "+jsonDateParamName+" from current time :" + timestamp);
        } else {
            log.info(sessionId + " | Get "+jsonDateParamName+" from Json :" + customDate);
            log.info(sessionId + " | Try convert [" + customDate + "] to timestamp");

            if (timestamp == null) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date date = dateFormat.parse(customDate);
                    timestamp = new Timestamp(date.getTime());
                    log.info(sessionId + " | Create timestamp is successfully [" + timestamp + "]");

                } catch(Exception e) { //this generic but you can control another types of exception
                    log.error(sessionId + " | Invalid format "+jsonDateParamName+".");
                }
            }
            if (timestamp == null) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                    Date date = dateFormat.parse(customDate);
                    timestamp = new Timestamp(date.getTime());
                    log.info(sessionId + " | Create timestamp is successfully [" + timestamp + "]");

                } catch (Exception e) { //this generic but you can control another types of exception
                    log.error(sessionId + " | Invalid format " + jsonDateParamName + ".");
                }
            }
        }

        return timestamp;
    }

    public String createJsonStr(UUID sessioId, Object object) {
        ObjectMapper Obj = new ObjectMapper();
        String output = "";
        try {
            output = Obj.writeValueAsString(object);
        } catch (JsonProcessingException except) {
            log.error(sessioId + " | Error :" + except.getMessage());
        }
        return output;
    }

}
