package io.simpolor.oauth2.config;

import io.simpolor.oauth2.security.CustomOAuth2Provider;
import io.simpolor.oauth2.security.CustomOAuth2UserService;
import io.simpolor.oauth2.security.OAuth2SuccessHandler;
import io.simpolor.oauth2.security.SocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;
    // 페이스북: id, name, email
    // 구글: sub, name, email
    // 카카오: id, kakao_account { email } properties { nickname }
    // 네이버: id, email, name }

    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
                .antMatchers("/", "/index", "/welcome").permitAll()
                .antMatchers("/error", "/error/**", "/h2-console/**").permitAll()
                .antMatchers("/oauth2/**").permitAll()
                .antMatchers("/login/**").permitAll()

                    .antMatchers("/facebook").hasAuthority(/*SocialType.FACEBOOK.getRoleType()*/"ROLE_USER")
                    .antMatchers("/google").hasAuthority(/*SocialType.GOOGLE.getRoleType()*/"ROLE_USER")
                    .antMatchers("/kakao").hasAuthority(/*SocialType.KAKAO.getRoleType()*/"ROLE_USER")
                    .antMatchers("/naver").hasAuthority(/*SocialType.NAVER.getRoleType()*/"ROLE_USER")
                    .anyRequest().authenticated()
                .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 토큰 방식으로 세션을 사용하지 않음
                .and()
                    .oauth2Login()
                    // .userInfoEndpoint()
                    .tokenEndpoint()
                    //.accessTokenResponseClient()
                    // .userService(customOAuth2UserService)  // 네이버 USER INFO의 응답을 처리하기 위한 설정
                .and()
                    //.defaultSuccessUrl("/loginSuccess")
                    .successHandler(oAuth2SuccessHandler)
                    .failureUrl("/loginFailure")
                .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"));
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(
            OAuth2ClientProperties oAuth2ClientProperties) {

        List<ClientRegistration> registrations = oAuth2ClientProperties
                .getRegistration().keySet().stream()
                .map(client -> getRegistration(oAuth2ClientProperties, client))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new InMemoryClientRegistrationRepository(registrations);
    }

    private ClientRegistration getRegistration(OAuth2ClientProperties clientProperties, String client) {

        if("google".equals(client)) {
            OAuth2ClientProperties.Registration registration = clientProperties.getRegistration().get("google");
            return CommonOAuth2Provider.GOOGLE.getBuilder(client)
                    .clientId(registration.getClientId())
                    .clientSecret(registration.getClientSecret())
                    .scope("email", "profile")
                    .build();
        }

        if("facebook".equals(client)) {
            OAuth2ClientProperties.Registration registration = clientProperties.getRegistration().get("facebook");
            return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
                    .clientId(registration.getClientId())
                    .clientSecret(registration.getClientSecret())
                    .userInfoUri("https://graph.facebook.com/me?fields=id,name,email,link")
                    .scope("email")
                    .build();
        }

        if("naver".equals(client)) {
            OAuth2ClientProperties.Registration registration = clientProperties.getRegistration().get("naver");
            return CustomOAuth2Provider.NAVER.getBuilder(client)
                    .clientId(registration.getClientId())
                    .clientSecret(registration.getClientSecret())
                    .scope("profile")
                    .build();
        }

        if("kakao".equals(client)) {
            OAuth2ClientProperties.Registration registration = clientProperties.getRegistration().get("kakao");
            return CustomOAuth2Provider.KAKAO.getBuilder(client)
                    .clientId(registration.getClientId())
                    .clientSecret(registration.getClientSecret())
                    .scope("profile_nickname", "account_email")
                    .build();
        }

        if("line".equals(client)) {
            OAuth2ClientProperties.Registration registration = clientProperties.getRegistration().get("line");
            return CustomOAuth2Provider.LINE.getBuilder(client)
                    .clientId(registration.getClientId())
                    .clientSecret(registration.getClientSecret())
                    .scope("profile")
                    .build();
        }

        return null;
    }
}
