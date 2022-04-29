package io.simpolor.authorization.security.advice;

import io.simpolor.authorization.repository.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashMap;
import java.util.Map;


/***
 * 모든 인증 작업을
 * auth response 응답 리턴하기 전 실행 (auth 플로우 최종 단계)
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@ControllerAdvice
public class OAuthResponseAdvice implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        if (request.getURI().toString().contains("/oauth/token")) {
            if (body instanceof DefaultOAuth2AccessToken) {

                DefaultOAuth2AccessToken oAuth2AccessToken = (DefaultOAuth2AccessToken) body;

                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("accessToken", oAuth2AccessToken.getValue());
                responseMap.put("tokenType", oAuth2AccessToken.getTokenType());
                responseMap.put("refreshToken", oAuth2AccessToken.getRefreshToken());
                responseMap.put("expiresIn", oAuth2AccessToken.getExpiresIn());
                responseMap.put("scope", oAuth2AccessToken.getScope());

                if (((DefaultOAuth2AccessToken) body).getAdditionalInformation() != null &&
                        ((DefaultOAuth2AccessToken) body).getAdditionalInformation().get("user") != null &&
                        ((DefaultOAuth2AccessToken) body).getAdditionalInformation().get("user") instanceof User) {

                    User user = (User) ((DefaultOAuth2AccessToken) body).getAdditionalInformation().get("user");
                    System.out.println("user : "+user);
                }

                return responseMap;
            }
        }

        return body;
    }
}