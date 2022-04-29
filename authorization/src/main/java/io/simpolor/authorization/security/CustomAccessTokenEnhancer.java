package io.simpolor.authorization.security;

import io.simpolor.authorization.model.UserDto;
import io.simpolor.authorization.repository.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/***
 * 인증 후 토큰 발급시 응답 값을 가공 처리하는 클래스
 * ( 클래스에서 작업이 마친 결과과 Jwt 토큰에 추가정보가 포함됨 )
 */
@Slf4j
@Component
public class CustomAccessTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {

        Authentication userAuthentication = oAuth2Authentication.getUserAuthentication();
        if (userAuthentication != null) {
            Object principal = oAuth2Authentication.getUserAuthentication().getPrincipal();
            if (principal instanceof User) {

                Map<String, Object> additionalInfo = new HashMap<>();
                additionalInfo.put("addFiled", "test");

                User user = (User) principal;
                additionalInfo.put("user", UserDto.of(user));
                ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(additionalInfo);
            }
        }

        return oAuth2AccessToken;
    }

}
