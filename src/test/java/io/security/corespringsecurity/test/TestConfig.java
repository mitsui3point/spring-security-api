package io.security.corespringsecurity.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.security.corespringsecurity.repository.UserRepository;
import io.security.corespringsecurity.security.configs.AjaxSecurityConfig;
import io.security.corespringsecurity.security.service.CustomUsersDetailsService;
import io.security.corespringsecurity.service.UserService;
import io.security.corespringsecurity.service.UserServiceImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;


@TestConfiguration
public class TestConfig {
    @MockBean
    UserRepository userRepository;

    @Bean
    public UserService userService() {
        return new UserServiceImpl(userRepository);
    }

    @Bean
    public AjaxSecurityConfig ajaxSecurityConfig() {
        return new AjaxSecurityConfig(customUsersDetailsService(), objectMapper());
    }

    @Bean
    public CustomUsersDetailsService customUsersDetailsService() {
        return new CustomUsersDetailsService(userRepository);
    }

    @Bean
    public AuthenticationProvider ajaxAuthenticationProvider() {
        return ajaxSecurityConfig().ajaxAuthenticationProvider();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
