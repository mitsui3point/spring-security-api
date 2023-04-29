package io.security.corespringsecurity.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.security.corespringsecurity.domain.AccountDto;
import io.security.corespringsecurity.security.configs.AjaxSecurityConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * {@link AbstractAuthenticationProcessingFilter} 상속
 *
 * 필터 작동 조건
 * {@link AntPathRequestMatcher}("/api/login") 로 요청정보와 매칭하고 요청 방식이 Ajax 이면 필터 작동
 *
 * {@link AjaxAuthenticationToken} 생성하여 {@link AuthenticationManager} 에게 전달하여 인증처리
 *
 * Filter 추가 {@link AjaxSecurityConfig}
 * http.addFilterBefore(AjaxAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
 */
@Slf4j
public class AjaxLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    public AjaxLoginProcessingFilter() {
        //java: call to super must be first statement in constructor; super 호출은 항상 첫줄이어야 한다.
        super(new AntPathRequestMatcher("/api/login"));//사용자가 해당 url 을 호출했을때 AjaxLoginProcessingFilter 가 작동하도록
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        loggingSessionCsrfTokenInfo(request.getSession());
        if (!isAjax(request)) {
            throw new IllegalStateException("Authentication is not supported");
        }

        ObjectMapper mapper = new ObjectMapper();
        AccountDto account = null;
        try {
            account = mapper.readValue(
                    request.getReader(),
                    AccountDto.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Username or Password is empty");
        }
        if (isEmptyAccount(account)) {
            throw new IllegalArgumentException("Username or Password is empty");
        }

        AjaxAuthenticationToken authenticationToken = new AjaxAuthenticationToken(
                account.getUsername(),
                account.getPassword());

        return getAuthenticationManager().authenticate(authenticationToken);
    }

    private void loggingSessionCsrfTokenInfo(HttpSession session) {
        if (session == null) {
            return;
        }
        if (session.getAttributeNames().hasMoreElements()) {
            String attrName = session.getAttributeNames().nextElement();
            log.info("/api/login attemptAuthentication");
            log.info("request.getSession().getId():{}", session.getId());
            try {
                DefaultCsrfToken csrfToken = (DefaultCsrfToken) session.getAttribute(attrName);
                log.info("request.getSession().getAttribute({}):\n{}", attrName, csrfToken.getToken());
            } catch (ClassCastException e) {
                log.error("loggingSessionCsrfTokenInfo:");
            } finally {
                return;
            }
        }
    }

    private boolean isEmptyAccount(AccountDto accountDto) {
        return StringUtils.isEmpty(accountDto.getUsername()) || StringUtils.isEmpty(accountDto.getPassword());
    }

    private boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }
}
