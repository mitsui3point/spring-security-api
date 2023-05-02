package io.security.corespringsecurity.controller.user;

import io.security.corespringsecurity.test.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MessageController.class)
@Import(TestConfig.class)
class MessageControllerTest {

    private static final String MESSAGES_URL = "/messages";

    @Autowired
    WebApplicationContext context;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
    @Test
    @WithMockUser(username = "manager", password = "1111", roles={"MANAGER"})
    @DisplayName("/api/messages 호출을 성공한다.")
    void apiMessages() throws Exception {
        //when
        mvc.perform(post("/api" + MESSAGES_URL)
                        .header("X-Requested-With", "XMLHttpRequest")
                        .with(csrf())
                )
                .andDo(print())
                //then
                .andExpect(authenticated().withAuthenticationName("manager"))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"))
        ;
    }

    @Test
    @WithAnonymousUser
    @DisplayName("/api/messages 를 인증되지 않은 유저가 호출하면 실패한다.(AjaxLoginAuthenticationEntryPoint; UnAuthorized)")
    void apiMessagesAnonymousUserFail() throws Exception {
        //when
        mvc.perform(post("/api" + MESSAGES_URL)
                        .header("X-Requested-With", "XMLHttpRequest")
                        .with(csrf())
                )
                .andDo(print())
                //then
                .andExpect(unauthenticated())
                .andExpect(status().isUnauthorized())
                .andExpect(result -> result.getResponse().getErrorMessage().equals("UnAuthorized"))
        ;
    }

    @Test
    @WithMockUser(username = "user", password = "1111", roles={"USER"})
    @DisplayName("/api/messages 호출을 ROLE_USER 가 호출시 실패한다.(AjaxAccessDeniedHandler; Access is denied)")
    void apiMessagesUserFail() throws Exception {
        //when
        mvc.perform(post("/api" + MESSAGES_URL)
                        .header("X-Requested-With", "XMLHttpRequest")
                )
                .andDo(print())
                //then
                .andExpect(authenticated())
                .andExpect(status().isForbidden())
                .andExpect(result -> result.getResponse().getErrorMessage().equals("Access is denied"))
        ;
    }
}