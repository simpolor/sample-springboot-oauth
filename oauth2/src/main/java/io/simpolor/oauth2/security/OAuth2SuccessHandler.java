package io.simpolor.oauth2.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    ObjectMapper objectMapper= new ObjectMapper();

    // private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();

        System.out.println("oAuth2User : "+oAuth2User);
        /*Token token = tokenService.generateToken(userDto.getEmail(), "USER");
        log.info("{}", token);*/

        /*if (request.getURI().toString().contains("/oauth/token")) {
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

        return body;*/

    }

    /*private void writeTokenResponse(HttpServletResponse response, Token token)
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        response.addHeader("Auth", token.getToken());
        response.addHeader("Refresh", token.getRefreshToken());
        response.setContentType("application/json;charset=UTF-8");

        PrintWriter writer = response.getWriter();
        writer.println(objectMapper.writeValueAsString(token));
        writer.flush();
    }*/
}
