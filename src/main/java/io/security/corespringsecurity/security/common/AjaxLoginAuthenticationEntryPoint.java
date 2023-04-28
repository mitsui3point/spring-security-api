package io.security.corespringsecurity.security.common;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 사용자가 자원에 접근할 경우 => 사용자가 다시 인증을 받도록 처리
 * 	AccessDeniedException 발생시킴 =>
 * 	ExceptionTranslationFilter 에서
 * 		1. 익명사용자가 AccessDeniedException 발생시킨 경우
 * 			sendStartAuthentication()
 * 			=> authenticationEntryPoint.commerce(); 재 로그인 요청을 할 수 있는 메서드
 */
public class AjaxLoginAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UnAuthorized");
    }
}
