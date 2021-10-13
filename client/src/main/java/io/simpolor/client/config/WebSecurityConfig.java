package io.simpolor.client.config;

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
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // Cross site request forgery (사이트간 요청 위조 [사용 권장] )
                /*.csrf().disable()*/

                // URL에 따른 권한 체크
                .authorizeRequests()
                    .antMatchers("/", "/index", "/welcome").permitAll()
                    .antMatchers("/error", "/error/**", "/test/**").permitAll()
                    .antMatchers("/student/list").permitAll()
                    .antMatchers("/student/detail").authenticated()
                    .antMatchers("/student/register", "/student/modify/**", "/student/delete/**").hasRole("ADMIN")
                    // .antMatchers("/admin/login").permitAll()
                    // .antMatchers("/admin/security").authenticated()
                    //.antMatchers("/admin/**").hasAnyAuthority("ADMIN")
                    //.antMatchers("/admin/**").hasRole("ADMIN") // Role은 앞에 "ROlE_" 권한이 붙음
                    .anyRequest().authenticated()

                /**
                 access(String) : 주어진 SpEL 표현식의 평가 결과가 true이면 접근을 허용
                 anonymous() : 익명의 사용자의 접근을 허용
                 authenticated() : 인증된 사용자의 접근을 허용
                 denyAll() : 무조건 접근을 허용하지 않음
                 fullyAuthenticated() : 사용자가 완전히 인증되면 접근을 허용(기억되지 않음)
                 hasAnyAuthority(String...) : 사용자가 주어진 권한 중 어떤 것이라도 있다면 접근을 허용
                 hasAnyRole(String...) : 사용자가 주어진 역할 중 어떤 것이라도 있다면 접근을 허용
                 hasAuthority(String) : 사용자가 주어진 권한이 있다면 접근을 허용
                 hasIpAddress(String) : 주어진 IP로부터 요청이 왔다면 접근을 허용
                 hasRole(String) : 사용자가 주어진 역할이 있다면 접근을 허용
                 not() : 다른 접근 방식의 효과를 무효화
                 permitAll() : 무조건 접근을 허용
                 rememberMe() : 기억하기를 통해 인증된 사용자의 접근을 허용
                 */

                //.antMatchers("/test/**").access("hasRole('ROLE_SPITTER') and hasIpAddress('192.168.1.2')")
                /**
                 authentication : 사용자의 인증 객체
                 denyAll : 항상 거짓으로 평가함
                 hasAnyRole(역할 목록) : 사용자가 역할 목록 중 하나라도 역할이 있는 경우 참
                 hasRole(역할) : 사용자가 주어진 역할이 있는 경우 참
                 hasIpAddress(IP 주소) : 주어진 IP 주소로부터 요청이 오는 경우 참
                 isAnonymous() : 사용자가 익명인 경우 참
                 isAuthenticated() : 사용자가 인증된 경우 참
                 isFullyAuthenticated() : 사용자가 완전히 인증된 경우 참 (기억하기(remember-me)로는 인증되지 않음)
                 isRememberMe() : 사용자가 기억하기(remember-me)로 인증된 경우 참
                 permitAll : 항상 참으로 평가함
                 principal : 사용자의 주체 객체
                 */

                // 로그인 설정
                .and()
                    .formLogin()
                    // .loginPage("/login") // 로그인 페이지 ( 컨트롤러를 매핑하지 않으면 기본 제공 로그인 페이지 호출 )
                    //.loginProcessingUrl("/user/login") // 로그인 프로세스 처리할 URL
                    //.failureUrl("/user/login?error=true") // 로그인 실패시 이동할 URL
                    //.defaultSuccessUrl("/user/home")
                    //.usernameParameter("id")";
                    //.passwordParameter("pw")";
                    //.and()

                // 에러 페이지 설정
                .and()
                    .exceptionHandling()
                    .accessDeniedPage("/access-denied-page")

                // 비밀번호 자동저장 설정
                // .and()
                //    .rememberMe()
                //    .key("remember-me")
                //    .rememberMeParameter("remember-me")
                //    .rememberMeCookieName("remember-me")
                //    .tokenValiditySeconds(86400) // 1일 = 86400초

                // 로그아웃 설정
                .and()
                    .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/student/list");
    }

    /**
     * 회원 정보 설정
     * - inMemoryAuthentication : 메모리 방식
     * - withUser : 식별자
     * - password : 비밀번호
     * - roles : 권한
     * @param auth
     * @throws Exception
     */
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("admin@gmail.com")
                .password(passwordEncoder().encode("1234")).roles("ADMIN", "USER");
        auth.inMemoryAuthentication().withUser("user@gmail.com")
                .password("{noop}1234").roles("USER");
        // {noop} : NoOpPasswordEncoder
    }
}
