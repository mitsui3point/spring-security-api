package io.security.corespringsecurity.security.common;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 사용자가 자원에 접근할 경우 => 사용자가 다시 인증을 받도록 처리
 * 	AccessDeniedException 발생시킴 =>
 * 	ExceptionTranslationFilter 에서
 * 		2. 인증받은사용자가 AccessDeniedException 발생시킨 경우
 * 			=> accessDeniedHandler.handle(); 메세지 혹은 식별할수 있는 정보로 접근거부 되었다고 사용자에게 알리는 메서드
 */
public class AjaxAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access is denied");
    }
}
