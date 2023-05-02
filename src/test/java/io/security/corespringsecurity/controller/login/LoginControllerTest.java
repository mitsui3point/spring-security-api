package io.security.corespringsecurity.controller.login;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.security.corespringsecurity.domain.Account;
import io.security.corespringsecurity.domain.AccountDto;
import io.security.corespringsecurity.repository.UserRepository;
import io.security.corespringsecurity.test.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import static java.util.Arrays.stream;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
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

    private AccountDto accountDto;

    @Autowired
    private UserRepository userRepository;
    private Account user;

    @BeforeEach
    void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

        user = getUser(passwordEncoder.encode(RAW_PASSWORD));
        accountDto = mapper.readValue(mapper.writeValueAsString(getUser(RAW_PASSWORD)), AccountDto.class);
    }

    @Test
    @DisplayName("/api/login 실패한다.")
    void apiLogin() throws Exception {
        //when
        mvc.perform(post("/api/login")
                        .header("X-Requested-With", "XMLHttpRequest")
                        .content(new ObjectMapper().writeValueAsString(accountDto))
                        .with(csrf())
                )
                .andDo(print())
                //then
                .andExpect(unauthenticated())
                .andExpect(content().string("\"Invalid (Username) or Password\""))
        ;
    }

    @Test
    @DisplayName("/api/login 성공한다.")
    void apiLoginSuccess() throws Exception {
        //given
        given(userRepository.findByUsername(any())).willReturn(user);
        //when
        mvc.perform(post("/api/login")
                        .header("X-Requested-With", "XMLHttpRequest")
                        .content(new ObjectMapper().writeValueAsString(accountDto))
                        .with(csrf())
                )
                .andDo(print())
                //then
                .andExpect(authenticated().withAuthenticationPrincipal(user))
                .andExpect(jsonPath("$['username']").value(user.getUsername()))
                .andExpect(jsonPath("$['password']").value(user.getPassword()))
                .andExpect(jsonPath("$['role']").value(user.getRole()))
                .andExpect(jsonPath("$['email']").value(user.getEmail()))
                .andExpect(jsonPath("$['age']").value(user.getAge()))
        ;
    }

    @Test
    @WithMockUser(username = "user", password = "1111", roles = "USER")
    @DisplayName("login 유저를 logout 시킨다.")
    void apiLogout() throws Exception {
        //given
        given(userRepository.findByUsername(any())).willReturn(user);
        //when
        mvc.perform(get("/api/logout")
                        .header("X-Requested-With", "XMLHttpRequest")
                        .content(new ObjectMapper().writeValueAsString(accountDto))
                )
                .andDo(print())
                //then
                .andExpect(status().isOk())
                .andExpect(unauthenticated())
        ;
    }

    @Test
    @WithMockUser(username = "user", password = "1111", roles = {"USER"})
    @DisplayName("/denied 의 페이지 동작을 확인한다.")
    void accessDeniedPageTest() throws Exception {
        //given
        String exceptionMessage = "invalid access user";
        //when
        mvc.perform(get("/api/denied")
                        .param("exception", exceptionMessage)
                )
                .andDo(print())
                //then
                .andExpect(status().isOk())
                .andExpect(view().name("user/login/denied"))
                .andExpect(model().attribute("username", "user"))
                .andExpect(model().attribute("exception", exceptionMessage))
                .andExpect(result -> result.getResponse().getContentAsString().contains(exceptionMessage))//화면 내에 exception 메세지 출력여부 확인
                .andExpect(result -> result.getResponse().getContentAsString().contains("user"))//화면 내에 username 메세지 출력여부 확인
        ;
    }

    private AuthenticationException[] getAuthenticationExceptions() {
        return new AuthenticationException[]{
                new InsufficientAuthenticationException("InsufficientAuthenticationException"),
                new UsernameNotFoundException("UsernameNotFoundException"),
                new BadCredentialsException("invalid password")
        };
    }
}
