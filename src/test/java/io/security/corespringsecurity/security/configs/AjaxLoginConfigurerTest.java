package io.security.corespringsecurity.security.configs;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class AjaxLoginConfigurerTest {
    AjaxLoginConfigurer ajaxLoginConfigurer;

    @BeforeEach
    void setUp() {
        ajaxLoginConfigurer = new AjaxLoginConfigurer();
    }

    @Test
    @DisplayName("AjaxLoginConfigurer 의 타입이 AbstractHttpConfigurer(AbstractAuthenticationFilterConfigurer) 이다. ")
    void instanceOf() {
//        boolean actual = ajaxLoginConfigurer instanceof AbstractAuthenticationFilterConfigurer;
        boolean actual = ajaxLoginConfigurer instanceof AbstractHttpConfigurer;
        Assertions.assertThat(actual).isTrue();
    }
}
