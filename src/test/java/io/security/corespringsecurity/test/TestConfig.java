package io.security.corespringsecurity.test;

import io.security.corespringsecurity.repository.UserRepository;
import io.security.corespringsecurity.service.UserService;
import io.security.corespringsecurity.service.UserServiceImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;


@TestConfiguration
public class TestConfig {
    @MockBean
    UserRepository userRepository;

    @Bean
    public UserService userService() {
        return new UserServiceImpl(userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
