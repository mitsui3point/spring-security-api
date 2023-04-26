package io.security.corespringsecurity.controller;

import io.security.corespringsecurity.test.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = HomeController.class)
@Import(TestConfig.class)
class HomeControllerTest {

    public static final String HOME_URL = "/";

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
    @WithAnonymousUser
    @DisplayName("/ 호출시 http status UNAUTHORIZED 를 리턴한다.")
    void homeAccessFail() throws Exception {
        //when
        mvc.perform(get(HOME_URL))
                .andDo(print())
                //then
                .andExpect(status().isUnauthorized());
//                .andExpect(status().isOk())
//                .andExpect(view().name("home"));
    }
}