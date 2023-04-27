package io.security.corespringsecurity.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.security.corespringsecurity.domain.Account;
import io.security.corespringsecurity.test.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import java.io.IOException;

import static io.security.corespringsecurity.constants.TestDataConstants.RAW_PASSWORD;
import static io.security.corespringsecurity.constants.TestDataConstants.getUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebMvcTest
@Import(TestConfig.class)
public class AjaxAuthenticationFailureHandlerTest {

    @Autowired
    AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;

    @Autowired
    PasswordEncoder passwordEncoder;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private AuthenticationException exception;
    private Account user;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        user = getUser(passwordEncoder.encode(RAW_PASSWORD));
    }

    @Test
    @DisplayName("ajaxAuthenticationFailureHandler 가 AuthenticationSuccessHandler 타입이다.")
    void instanceOf() {
        boolean actual = ajaxAuthenticationFailureHandler instanceof AuthenticationFailureHandler;
        assertThat(ajaxAuthenticationFailureHandler).isNotNull();
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("onAuthenticationFailure 메서드 호출시(username 불일치) 실패 응답값을 response 세팅한다.")
    void onAuthenticationFailureCauseUsername() throws ServletException, IOException {
        //given
        exception = new UsernameNotFoundException("UsernameNotFoundException");

        //when
        ajaxAuthenticationFailureHandler.onAuthenticationFailure(request, response, exception);

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.getContentAsString()).isEqualTo("\"Invalid (Username) or Password\"");
    }

    @Test
    @DisplayName("onAuthenticationFailure 메서드 호출시(password 불일치) 실패 응답값을 response 세팅한다.")
    void onAuthenticationFailureCausePassword() throws ServletException, IOException {
        //given
        exception = new BadCredentialsException("invalid password");

        //when
        ajaxAuthenticationFailureHandler.onAuthenticationFailure(request, response, exception);

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.getContentAsString()).isEqualTo("\"Invalid Username or (Password)\"");
    }
}
