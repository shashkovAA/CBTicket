package ru.wawulya.CBTicket.utility;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.wawulya.CBTicket.enums.LogLevel;
import ru.wawulya.CBTicket.error.ApiError;
import ru.wawulya.CBTicket.error.BadRequestException;
import ru.wawulya.CBTicket.error.ForbiddenException;
import ru.wawulya.CBTicket.error.NotFoundException;
import ru.wawulya.CBTicket.model.ApiLog;
import ru.wawulya.CBTicket.service.DataService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class LoggingHandler {

    @Autowired
    private DataService dataService;

    @Autowired
    private Utils utils;

    /*@Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controller() {
    }*/
    /*@Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void controller(){
    }*/

    @Pointcut("execution(* *(..)) && within(ru.wawulya.CBTicket.controller.*))")
    public void atExecution() {
    }


    //before -> Any resource annotated with @Controller annotation
    //and all method and function taking HttpServletRequest as first parameter
   /* @Before("getMapping() && atExecution()")
    public void logBefore(JoinPoint joinPoint) {
        log.debug(joinPoint.toString());
        log.debug(String.valueOf(joinPoint.getArgs().length));
    }*/
/*      log.debug("Entering in Method :  " + joinPoint.getSignature().getName());
        log.debug("Class Name :  " + joinPoint.getSignature().getDeclaringTypeName());
        log.debug("Arguments :  " + Arrays.toString(joinPoint.getArgs()));
        log.debug("Target class : " + joinPoint.getTarget().getClass().getName());

        if (null != request) {
            log.debug("Start Header Section of request ");
            log.debug("Method Type : " + request.getMethod());
*//*            Enumeration headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = (String) headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                log.debug("Header Name: " + headerName + " Header Value : " + headerValue);
            }*//*
            log.debug("Request Path info :" + request.getServletPath());
            log.debug("End Header Section of request ");
        }*/


    //After -> All method within resource annotated with @Controller annotation
    // and return a  value
    /*@AfterReturning(pointcut = "atExecution() && args(request,response,..)", returning = "result")
    public void logAfter(JoinPoint joinPoint, HttpServletRequest request, HttpServletResponse response, Object result) {
       StringBuilder sb = new StringBuilder();

       if (request != null && response != null) {

           sb.append(request.getMethod() + " | ");
           sb.append(request.getRequestURI() + " | ");
           sb.append(String.valueOf(response.getStatus()) + " | ");
           sb.append(utils.createJsonStr(null, result) + " | ");
           sb.append(request.getRemoteUser() + " | ");
           sb.append(request.getRemoteAddr());

       }

        log.warn(sb.toString());*/


        /*String returnValue = this.getValue(result);
        log.debug("Method Return value : " + returnValue);
        try {
            log.info("Response object:" + objectMapper.writeValueAsString(result));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }*/
        //log.warn(utils.createJsonStr(null, result));

    //After -> Any method within resource annotated with @Controller annotation
    // throws an exception ...Log it
    /*@AfterThrowing(pointcut = "atExecution()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.error("An exception has been thrown in " + joinPoint.getSignature().getName() + " ()");
        log.error("Cause : " + exception.getMessage());
    }*/

    @Around("atExecution() && args(request,response,..)")
    public Object logAround(ProceedingJoinPoint joinPoint, HttpServletRequest request, HttpServletResponse response) throws Throwable {
        Object result = null;
        ApiLog logRecord = null;
        StringBuilder requestParams = new StringBuilder();

        long start = System.currentTimeMillis();

        if (joinPoint.getArgs().length > 2) {
            Map<String, String[]> map = request.getParameterMap();
            if (!map.isEmpty()) {
                requestParams.append("request params:");
                map.forEach((k,v) -> requestParams.append(k + "=" + v[0]));
            } else {
                requestParams.append("request body:");
                requestParams.append(utils.createJsonStr(joinPoint.getArgs()[2]));
            }
        }

        if (request != null && response != null) {

            logRecord = new ApiLog();
            logRecord.setSessionId(request.getRequestedSessionId());
            logRecord.setMethod(request.getMethod());
            logRecord.setApiUrl(request.getRequestURL().toString());
            logRecord.setRequestBody(requestParams.toString());
            logRecord.setUsername(request.getRemoteUser());
            logRecord.setHost(request.getRemoteAddr());

        }

        try {
            result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - start;

            if (logRecord != null) {
                logRecord.setStatusCode(String.valueOf(HttpStatus.resolve(response.getStatus())));
                logRecord.setResponseBody(utils.createJsonStr(request.getRequestedSessionId(), result));
                logRecord.setLevel(LogLevel.INFO);
                logRecord.setDuration(String.valueOf(elapsedTime));

                logInFile(logRecord);
                logInDB(logRecord);

            }

        }
        catch (NotFoundException nfExcept) {
            long elapsedTime = System.currentTimeMillis() - start;
            if (logRecord != null) {
                logRecord.setStatusCode(String.valueOf(HttpStatus.NOT_FOUND));
                logRecord.setResponseBody(utils.createJsonStr(new ApiError(HttpStatus.NOT_FOUND,nfExcept.getMessage())));
                logRecord.setLevel(LogLevel.WARN);
                logRecord.setDuration(String.valueOf(elapsedTime));

                logInFile(logRecord);
                logInDB(logRecord);

            } else {
                log.error("An exception has been thrown in " + joinPoint.getSignature().getName() + " ()");
                log.error("Cause : " + nfExcept.getMessage());
            }

            throw nfExcept;

        }
        catch (BadRequestException brExcept) {
            long elapsedTime = System.currentTimeMillis() - start;
            if (logRecord != null) {
                logRecord.setStatusCode(String.valueOf(HttpStatus.BAD_REQUEST));
                logRecord.setResponseBody(utils.createJsonStr(new ApiError(HttpStatus.BAD_REQUEST,brExcept.getMessage())));
                logRecord.setLevel(LogLevel.WARN);
                logRecord.setDuration(String.valueOf(elapsedTime));

                logInFile(logRecord);
                logInDB(logRecord);

            } else {
                log.error("An exception has been thrown in " + joinPoint.getSignature().getName() + " ()");
                log.error("Cause : " + brExcept.getMessage());
            }

            throw brExcept;

        }
        catch (ForbiddenException fbExcept) {
            long elapsedTime = System.currentTimeMillis() - start;
            if (logRecord != null) {
                logRecord.setStatusCode(String.valueOf(HttpStatus.FORBIDDEN));
                logRecord.setResponseBody(utils.createJsonStr(new ApiError(HttpStatus.FORBIDDEN,fbExcept.getMessage())));
                logRecord.setLevel(LogLevel.WARN);
                logRecord.setDuration(String.valueOf(elapsedTime));

                logInFile(logRecord);
                logInDB(logRecord);

            } else {
                log.error("An exception has been thrown in " + joinPoint.getSignature().getName() + " ()");
                log.error("Cause : " + fbExcept.getMessage());
            }

            throw fbExcept;

        }
        catch (Exception exception) {
            log.error("An exception has been thrown in " + joinPoint.getSignature().getName() + " ()");
            log.error("Cause : " + exception.getMessage());
            throw exception;

        }
        return result;
    }

    private void logInFile(ApiLog logRecord) {
        StringBuilder sb = new StringBuilder();

        sb.append(logRecord.getSessionId() + " | ");
        sb.append(logRecord.getMethod() + " | ");
        sb.append(logRecord.getApiUrl() + " | ");
        sb.append(logRecord.getRequestBody() + " | ");
        sb.append(logRecord.getStatusCode() + " | ");
        sb.append(logRecord.getDuration() + " ms | ");
        sb.append("response body:" + logRecord.getResponseBody() + " | ");
        sb.append("user:" + logRecord.getUsername() + " | ");
        sb.append("host:" + logRecord.getHost());

        switch (logRecord.getLevel()) {
            case LogLevel.DEBUG: log.debug(sb.toString()); break;
            case LogLevel.INFO:  log.info(sb.toString()); break;
            case LogLevel.WARN:  log.warn(sb.toString()); break;
            case LogLevel.ERROR: log.error(sb.toString()); break;
        }
    }

    private void logInDB(ApiLog logRecord) {
        dataService.getLogService().saveLog(logRecord);
    }

}