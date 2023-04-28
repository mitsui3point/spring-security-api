package io.security.corespringsecurity.security.common;

import io.security.corespringsecurity.test.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;


import javax.servlet.ServletException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest
@Import(TestConfig.class)
public class AjaxAccessDeniedHandlerTest {

    @Autowired
    AjaxAccessDeniedHandler ajaxAccessDeniedHandler;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private AccessDeniedException accessDeniedException;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        accessDeniedException = new AccessDeniedException("Access Denied Exception");

    }

    @Test
    @DisplayName("AjaxAccessDeniedHandler 가 AccessDeniedHandler 타입이다.")
    void instanceOf() {
        boolean actual = ajaxAccessDeniedHandler instanceof AccessDeniedHandler;
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("인가가 필요한 자원에 인증되었으나 인가되지 않은 사용자가 접근하면 forbidden http 응답코드를 출력한다.")
    void handle() throws ServletException, IOException {
        //when
        ajaxAccessDeniedHandler.handle(request, response, accessDeniedException);

        //then
        assertThat(response.getErrorMessage()).isEqualTo("Access is denied");
    }
}