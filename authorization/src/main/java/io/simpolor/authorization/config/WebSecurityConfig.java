package io.simpolor.authorization.config;

import io.simpolor.authorization.security.PasswordEncoding;
import io.simpolor.authorization.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    // 로그인시 password에 대한 인코딩을 안하도록 빈 설정
    /*@Bean
    public PasswordEncoder noOpPasswordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }*/

    private final PasswordEncoding passwordEncoding;
    private final UserService userService;

    /**
     * AuthorizationServer에서 grant_type : password 방식을 사용하기 위하여 빈 선언
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf()
                .ignoringAntMatchers("/h2-console/**")
            .and()
                .headers()
                    .frameOptions().disable()
            /*.csrf()
                .disable()
                .headers().frameOptions().disable()*/

            // URL에 따른 권한 체크
            .and()
                .authorizeRequests()
                .antMatchers("/", "/index", "/welcome").permitAll()
                .antMatchers("/error", "/error/**", "/h2-console/**").permitAll()
                // .antMatchers("/user/**").permitAll()
                .antMatchers("/oauth/authorize", "/oauth/token", "/oauth/check_token", "/oauth/**").permitAll()
                .antMatchers("/authorization/form", "/authorization/code", "/authorization/callback", "/authorization/**").permitAll()
                .anyRequest().authenticated()
                .and().formLogin()
            .and().httpBasic();
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userService)
            .passwordEncoder(passwordEncoding);

        // 아래는 프로바이더를 사용할 경우
        // auth.authenticationProvider(oAuthAuthenticationProvider);
    }

}
