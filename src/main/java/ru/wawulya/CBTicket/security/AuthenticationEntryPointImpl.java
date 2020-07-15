package ru.wawulya.CBTicket.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import ru.wawulya.CBTicket.model.RequestResult;
import ru.wawulya.CBTicket.utility.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
@Slf4j
@Component
public class AuthenticationEntryPointImpl extends BasicAuthenticationEntryPoint {

    @Autowired
    private Utils utils;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
            throws IOException {

        response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = response.getWriter();
        writer.println(utils.createJsonStr(UUID.randomUUID(), new RequestResult("401",authEx.getMessage())));
    }

    @Override
    public void afterPropertiesSet() {
        // RealmName appears in the login window (Firefox).
        setRealmName("CBTicket");
        super.afterPropertiesSet();
    }

}
