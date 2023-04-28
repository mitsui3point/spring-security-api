package io.security.corespringsecurity.security.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class AjaxLoginAuthenticationEntryPointTest {

    AjaxLoginAuthenticationEntryPoint ajaxLoginAuthenticationEntryPoint;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private AuthenticationException authenticationException;

    @BeforeEach
    void setUp() {
        ajaxLoginAuthenticationEntryPoint = new AjaxLoginAuthenticationEntryPoint();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        authenticationException = new AuthenticationException("Auth Exception") {};
    }

    @Test
    @DisplayName("AjaxLoginAuthenticationEntryPoint 가 AuthenticationEntryPoint 타입이다.")
    void instanceOf() {
        boolean actual = ajaxLoginAuthenticationEntryPoint instanceof AuthenticationEntryPoint;
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("인가가 필요한 자원에 미인증 사용자가 접근하면 Unauthorized http 응답코드를 출력한다.")
    void commence() throws ServletException, IOException {
        //when
        ajaxLoginAuthenticationEntryPoint.commence(request, response, authenticationException);
        //then
        assertThat(response.getErrorMessage()).isEqualTo("UnAuthorized");
    }
}