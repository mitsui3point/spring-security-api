package io.security.corespringsecurity.security.provider;

import io.security.corespringsecurity.security.filter.AjaxAuthenticationToken;
import io.security.corespringsecurity.security.service.AccountContext;
import io.security.corespringsecurity.security.service.CustomUsersDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자가 입력한 정보로 추가적인 인증절차를 SecurityConfig 에 설정하는 Service
 */
@RequiredArgsConstructor
public class AjaxAuthenticationProvider implements AuthenticationProvider {
    private final CustomUsersDetailsService customUsersDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        AccountContext accountContext = (AccountContext) customUsersDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, accountContext.getAccount().getPassword())) {
            throw new BadCredentialsException("invalid password");
        }

        return new AjaxAuthenticationToken(
                accountContext.getAccount(),
                null,
                accountContext.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        //AuthenticationProvider(Impl;ProviderManager#authenticate if(!provider.supports(TargetClass.class))
        return AjaxAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
