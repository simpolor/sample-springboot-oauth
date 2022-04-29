package io.simpolor.authorization.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.simpolor.authorization.model.AuthorizeDto;
import io.simpolor.authorization.model.OAuthToken;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2")
public class OAuth2Controller {

    @Value("${security.oauth2.client.client-id}")
    private String oauthClientId;

    @Value("${security.oauth2.client.client-secret}")
    private String oauthSecret;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @PostMapping("/authorize")
    public String authorize(@RequestBody AuthorizeDto request) {

        if(!request.getClientId().equals(oauthClientId)){
            return "Fail";
        }
        if(!request.getSecret().equals(oauthSecret)){
            return "Fail";
        }

        String credentials = request.getClientId()+":"+request.getSecret();
        String encoderCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        return "Basic " + encoderCredentials;
    }

}
