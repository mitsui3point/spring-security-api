package io.security.corespringsecurity.security.configs;

import io.security.corespringsecurity.test.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static org.assertj.core.api.Assertions.*;

@WebMvcTest
@Import(TestConfig.class)
public class AjaxSecurityConfigTest {

    @Autowired
    AjaxSecurityConfig ajaxSecurityConfig;

    @Test
    void instanceOf() {
        assertThat(ajaxSecurityConfig).isInstanceOf(WebSecurityConfigurerAdapter.class);
    }
}
