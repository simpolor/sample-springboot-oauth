package io.simpolor.client.remote.message;

import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttribute {

    private Map<String, Object> attribute; // OAuth 반환하는 유저 정보 Map
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

}