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
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authorization")
public class AuthorizationController {

    @Value("${security.oauth2.client.client-id}")
    private String oauthClientId;

    @Value("${security.oauth2.client.client-secret}")
    private String oauthSecret;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * # grant_type = 'authorization_code' 코드 발급
     * @param mav
     * @return
     */
    @RequestMapping({"/form"})
    public ModelAndView form(ModelAndView mav) {

        mav.setViewName("authorize_form");
        return mav;
    }

    @RequestMapping({"/code"})
    public ModelAndView code(ModelAndView mav) {

        mav.setViewName("authorize_code");
        return mav;
    }

    /**
     * 내부적으로 /oauth/token을 호출하여 인증처리를 위한 컨트롤러
     * @param code
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping("/callback")
    public OAuthToken callback(@RequestParam String code) throws JsonProcessingException {

        String credentials = oauthClientId+":"+oauthSecret;
        String encoderCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Basic "+encoderCredentials);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", "http://localhost:9090/authorization/callback");
        params.add("scope", "read");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:9090/oauth/token", request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            OAuthToken oAuthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
            return oAuthToken;
        }

        return new OAuthToken();
    }

    @GetMapping("/refresh")
    public OAuthToken refresh(@RequestParam String refreshToken) throws JsonProcessingException {

        String credentials = oauthClientId+":"+oauthSecret;
        String encoderCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Authorization", "Basic "+encoderCredentials);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("refresh_token", refreshToken);
        params.add("grant_type", "refresh_token");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:9090/oauth/token", request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            OAuthToken oAuthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
            return oAuthToken;
        }

        return new OAuthToken();
    }

    @PostMapping("/credentials")
    public String authorize(@RequestBody AuthorizeDto request) {

        if(!request.getClientId().equals(oauthClientId) || !request.getSecret().equals(oauthSecret)){
            return "";
        }

        String credentials = request.getClientId()+":"+request.getSecret();
        String encoderCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        return "Basic " + encoderCredentials;
    }

}
