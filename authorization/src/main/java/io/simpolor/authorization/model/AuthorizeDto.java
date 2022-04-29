package io.simpolor.authorization.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuthorizeDto {

    private String clientId;
    private String secret;
}
