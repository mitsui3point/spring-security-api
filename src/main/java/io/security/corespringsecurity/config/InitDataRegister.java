package io.security.corespringsecurity.config;

import io.security.corespringsecurity.domain.Account;
import io.security.corespringsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitDataRegister {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @PostConstruct
    public void init() {
        userRepository.save(getUser(passwordEncoder.encode("1111")));
        userRepository.save(getManager(passwordEncoder.encode("1111")));
        userRepository.save(getAdmin(passwordEncoder.encode("1111")));
    }

    private Account getUser(String password) {
        return Account.builder()
                .username("user")
                .password(password)
                .age("11")
                .role("ROLE_USER")
                .email("aa@aa.com")
                .build();
    }

    private Account getManager(String password) {
        return Account.builder()
                .username("manager")
                .password(password)
                .age("11")
                .role("ROLE_MANAGER")
                .email("aa@aa.com")
                .build();
    }

    private Account getAdmin(String password) {
        return Account.builder()
                .username("admin")
                .password(password)
                .age("11")
                .role("ROLE_ADMIN")
                .email("aa@aa.com")
                .build();
    }
}
