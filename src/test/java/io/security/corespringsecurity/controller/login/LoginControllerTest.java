package io.security.corespringsecurity.controller.login;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.security.corespringsecurity.domain.AccountDto;
import io.security.corespringsecurity.test.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static io.security.corespringsecurity.constants.TestDataConstants.RAW_PASSWORD;
import static io.security.corespringsecurity.constants.TestDataConstants.getUser;
import static io.security.corespringsecurity.constants.UrlConstant.LOGIN_URL;
import static io.security.corespringsecurity.constants.UrlConstant.LOGOUT_URL;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@Import(TestConfig.class)
public class LoginControllerTest {

    @Autowired
    WebApplicationContext context;

    @Autowired
    PasswordEncoder passwordEncoder;

    MockMvc mvc;

    private AccountDto user;

    @BeforeEach
    void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

        user = mapper.readValue(mapper.writeValueAsString(getUser(passwordEncoder.encode(RAW_PASSWORD))), AccountDto.class);
    }

    @Test
    @WithAnonymousUser
    @DisplayName("직접 커스터마이징한 로그인 페이지를 노출한다.")
    void customLoginPageTest() throws Exception {
        //when
        mvc.perform(get(LOGIN_URL))
                .andDo(print())
                //then
                .andExpect(view().name("user/login/login"));
    }

    @Test
    @WithMockUser(username = "user", password = "1111", roles = "USER")
    @DisplayName("로그아웃하면 로그인페이지로 redirect 한다.")
    void logoutTest() throws Exception {
        //when
        mvc.perform(get(LOGOUT_URL))
                .andDo(print())
                //then
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_URL))
                .andExpect(unauthenticated())//인증되지 않은 상태
        ;
    }

    @Test
    @WithAnonymousUser
    @DisplayName("/api/login 시도한다.")
    void apiLogin() throws Exception {
        //when
        mvc.perform(post("/api/login")
                        .header("X-Requested-With", "XMLHttpRequest")
                        .content(new ObjectMapper().writeValueAsString(user))
                )
                .andDo(print())
                //then
                .andExpect(unauthenticated())
//                .andExpect(authenticated())
        ;
    }
}
