package io.security.corespringsecurity.test;

import io.security.corespringsecurity.repository.UserRepository;
import io.security.corespringsecurity.security.configs.AjaxSecurityConfig;
import io.security.corespringsecurity.service.UserService;
import io.security.corespringsecurity.service.UserServiceImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;


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
        return new AjaxSecurityConfig();
    }

}
