package io.simpolor.client.config;

import io.simpolor.client.security.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests() // URL에 따른 권한 체크
                .antMatchers("/", "/index", "/welcome").permitAll()
                .antMatchers("/error", "/error/**", "/test/**").permitAll()
                .antMatchers("/student/list").permitAll()
                .antMatchers("/student/detail").authenticated()
                .antMatchers("/student/register", "/student/modify/**", "/student/delete/**").hasRole("ADMIN")
                .anyRequest().authenticated()

            .and()// 로그인 설정
                .formLogin()

            .and() // 로그아웃 설정
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/student/list")

            .and() // 에러 페이지 설정
                .exceptionHandling()
                .accessDeniedPage("/access-denied-page")

            .and()
                .oauth2Login()
                .userInfoEndpoint()
                .userService(customOAuth2UserService);

    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin@gmail.com")
                .password(passwordEncoder().encode("1234")).roles("ADMIN", "USER");
        auth.inMemoryAuthentication()
                .withUser("user@gmail.com")
                .password("{noop}1234").roles("USER");
    }
}
