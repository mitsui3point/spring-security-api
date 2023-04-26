package io.security.corespringsecurity.security.filter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.security.corespringsecurity.domain.AccountDto;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.ServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static io.security.corespringsecurity.constants.TestDataConstants.RAW_PASSWORD;
import static io.security.corespringsecurity.constants.TestDataConstants.getUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(TestConfig.class)
@ExtendWith(MockitoExtension.class)
public class AjaxLoginProcessingFilterTest {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MockMvc mvc;

    @InjectMocks
    AjaxLoginProcessingFilter ajaxLoginProcessingFilter;

    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    MockHttpServletRequest request;

    @Mock
    MockHttpServletResponse response;
    private BufferedReader bufferedReader;
    private AccountDto user;

    @BeforeEach
    void setUp() throws Exception {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        request.setRequestURI("/api/login");
        request.setMethod(POST.name());

        user = mapper.readValue(mapper.writeValueAsString(getUser(passwordEncoder.encode(RAW_PASSWORD))), AccountDto.class);
        bufferedReader = new BufferedReader(new StringReader(mapper.writeValueAsString(user)));
    }

    @Test
    @DisplayName("AjaxAuthenticationProcessingFilter 가 AbstractAuthenticationProcessingFilter 타입이다.")
    void instanceOf() {
        boolean actual = ajaxLoginProcessingFilter instanceof AbstractAuthenticationProcessingFilter;
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("인증필터 메서드에서 요청이 ajax 가 아닐경우 예외를 발생시킨다.")
    void attemptAuthenticationWithAjax() {
        //then
        assertThatThrownBy(() ->
                //when
                ajaxLoginProcessingFilter.attemptAuthentication(request, response))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("인증필터 메서드에서 요청을 받을 때 user 정보가 불일치한다.")
    void attemptAuthenticationNoUserinfo() throws IOException {
        //given
        given(request.getHeader("X-Requested-With")).willReturn("XMLHttpRequest");
        given(request.getReader()).willReturn(null);

        //then
        assertThatThrownBy(() ->
                //when
                ajaxLoginProcessingFilter.attemptAuthentication(request, response))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("인증필터 메서드에서 요청이 필터를 정상통과할때, 인증정보가 담긴 AjaxAuthenticationToken 을 리턴한다.")
    void attemptAuthenticationWithUserinfo() throws IOException, ServletException {
        //given
        given(request.getHeader("X-Requested-With")).willReturn("XMLHttpRequest");
        given(request.getReader()).willReturn(bufferedReader);

        //when
        assertDoesNotThrow(() -> ajaxLoginProcessingFilter.attemptAuthentication(request, response));
    }
}
