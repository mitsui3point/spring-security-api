package io.security.corespringsecurity.security.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static io.security.corespringsecurity.constants.RoleConstant.*;
import static io.security.corespringsecurity.constants.UrlConstant.*;

@Configuration
@EnableWebSecurity
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //현재 이 시스템에 요청으로 접근하려면 모든 요청에 대해 인증을 요구함
        http
                .authorizeRequests()

                .antMatchers(ROOT_URL, USERS_URL, "user/login/**", "/login", "/denied").permitAll()
                .antMatchers(MYPAGE_URL).hasRole(USER_ROLE)
                .antMatchers(MESSAGES_URL).hasRole(MANAGER_ROLE)
                .antMatchers(CONFIG_URL).hasRole(ADMIN_ROLE)
                .anyRequest()
                .authenticated();
    }
}
