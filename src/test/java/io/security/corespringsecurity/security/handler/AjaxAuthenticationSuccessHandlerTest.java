package io.security.corespringsecurity.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.security.corespringsecurity.domain.Account;
import io.security.corespringsecurity.test.TestConfig;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.Assertions;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static io.security.corespringsecurity.constants.TestDataConstants.RAW_PASSWORD;
import static io.security.corespringsecurity.constants.TestDataConstants.getUser;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@WebMvcTest
@Import(TestConfig.class)
@ExtendWith(MockitoExtension.class)
public class AjaxAuthenticationSuccessHandlerTest {

    @Autowired
    @InjectMocks
    AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler;

    @Autowired
    PasswordEncoder passwordEncoder;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    @Mock
    private Authentication authentication;
    private Account user;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        user = getUser(passwordEncoder.encode(RAW_PASSWORD));
    }

    @Test
    @DisplayName("ajaxAuthenticationSuccessHandler 가 AuthenticationSuccessHandler 타입이다.")
    void instanceOf() {
        boolean actual = ajaxAuthenticationSuccessHandler instanceof AuthenticationSuccessHandler;
        assertThat(ajaxAuthenticationSuccessHandler).isNotNull();
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("onAuthenticationSuccess 메서드 호출시 성공 응답값을 response 세팅한다.")
    void onAuthenticationSuccess() throws ServletException, IOException {
        //given
        given(authentication.getPrincipal()).willReturn(user);

        //when
        ajaxAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(new ObjectMapper().readValue(
                response.getContentAsString(),
                Account.class))
                .isEqualTo(user);
    }
}
