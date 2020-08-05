package ru.wawulya.CBTicket.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationEntryPoint authEntryPoint;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }

   /* @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf()
                .disable();
        // All requests send to the Web Server request must be authenticated
        http
                .authorizeRequests()
                .antMatchers("/").permitAll()
                //.antMatchers("/login").permitAll()

                //.antMatchers("/api/*").authenticated()

                //.hasRole("USER")
                .anyRequest().authenticated();

                //.anyRequest().permitAll()

        *//*.and()
                .formLogin()
                .usernameParameter("user")
                .passwordParameter("passwd")
                .loginPage("/login")
                .defaultSuccessUrl("/settings")*//*
        //.and()
         //       .httpBasic();

        // Use AuthenticationEntryPoint to authenticate user/password
        http.httpBasic().authenticationEntryPoint(authEntryPoint);
        http.headers().frameOptions().sameOrigin();
        //http.httpBasic();
    }*/

    @Configuration
    @Order(1)
    public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Autowired
        private AuthenticationEntryPoint authEntryPoint;

        protected void configure(HttpSecurity http) throws Exception {
            http.cors()
                    .and()
                    .csrf()
                    .disable();

            http
                    .antMatcher("/api/**")
                    .authorizeRequests()
                    .anyRequest()
                    .hasRole("USER")
            .and()
                    .httpBasic()
                    .authenticationEntryPoint(authEntryPoint);

        }
    }

    @Configuration
    @Order(2)
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .cors()
                    .and()
                    .csrf()
                    .disable();

            http
                    .authorizeRequests()
                    .antMatchers("/", "/css/**", "/js/**").permitAll()
                    .anyRequest().authenticated()
            .and()
                    .formLogin()
                    .loginPage("/login")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/home")
                    .permitAll()
            .and()
                    .logout()
                    .permitAll()
                    .logoutSuccessUrl("/login");

            http
                    .headers()
                    .frameOptions()
                    .sameOrigin();
        }


    }


    /*@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        String password = "admin";

        String encrytedPassword = this.passwordEncoder().encode(password);

        InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> //
                mngConfig = auth.inMemoryAuthentication();

        UserDetails u1 = User.withUsername("admin").password(encrytedPassword).roles("USER").build();
        mngConfig.withUser(u1);

    }*/

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Access-Control-Allow-Headers","Access-Control-Allow-Origin","Access-Control-Request-Method", "Access-Control-Request-Headers","Origin","Cache-Control", "Content-Type", "Authorization"));
        configuration.setAllowedMethods(Arrays.asList("DELETE", "GET", "POST", "PATCH", "PUT"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
