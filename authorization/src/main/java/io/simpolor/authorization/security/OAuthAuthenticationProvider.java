package io.simpolor.authorization.security;

import io.simpolor.authorization.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuthAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoding passwordEncoding;
    private final UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();
        System.out.println("authentication.username = " + username);
        System.out.println("authentication.password = " + password);

        // 테스트 유저 호출(만약 DB에 연동해서 불러온다면 대체해도 된다)
        UserDetails user = userService.loadUserByUsername(username);

        if(passwordEncoding.matches(passwordEncoding.encode(password), user.getPassword())) {
            throw new BadCredentialsException(username);
        }

        return new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}
