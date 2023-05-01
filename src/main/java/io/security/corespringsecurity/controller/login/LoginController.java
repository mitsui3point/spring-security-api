package io.security.corespringsecurity.controller.login;

import io.security.corespringsecurity.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "exception", required = false) String exception,
                        Model model) {
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);

        return "user/login/login";
    }

    @PostMapping("/api/login")
    @ResponseBody
    public String apiLogin(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "exception", required = false) String exception,
                           Model model) {
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);

        return "user/login/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();//인증객체
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);//session.invalidate();, 인증객체 비움
        }
        return "logout ok";
    }

    @GetMapping("/api/logout")
    @ResponseBody
    public String apiLogout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();//인증객체
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);//session.invalidate();, 인증객체 비움
        }
        return "logout ok";
    }

    @GetMapping(value = {"/denied", "/api/denied"})
    public String accessDenied(@RequestParam(value = "exception", required = false) String exception,
                               Model model) {
        //현재 사용자 이름
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        Account account = new ModelMapper().map(authentication.getPrincipal(), Account.class);

        //사용자가 자원을 체크하지 못하는 message 출력
        model.addAttribute("username", account.getUsername());
        model.addAttribute("exception", exception);

        return "user/login/denied";
    }
}
