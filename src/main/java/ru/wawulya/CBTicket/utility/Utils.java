package ru.wawulya.CBTicket.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
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

    public  String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

    public String humanReadableByteCountBin(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %cB", value / 1024.0, ci.current());
    }

    public String convertMilsToDate(Long mils) {

        DateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date result = new Date(mils);

        return simple.format(result);
    }

}
