package io.simpolor.client.dto.model;

import io.simpolor.client.dto.enums.Role;
import io.simpolor.client.repository.entity.User;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthAttributes {

    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

    /* of()
     * OAuth2User에서 반환하는 사용자 정보는 Map이기 때문에 값 하나하나 변환
     */
    public static OAuthAttributes of(String registrationId,
                                     String userNameAttributeName,
                                     Map<String, Object> attributes) {

        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }


    /* toEntity()
     * User 엔티티 생성
     * OAuthAttributes에서 엔티티 생성 시점 = 처음 가입 시
     * OAuthAttributes 클래스 생성이 끝났으면 같은 패키지에 SessionUser 클래스 생성
     */
    public User toEntity(){
        return User.builder()
                .name(this.name)
                .email(this.email)
                .picture(this.picture)
                .role(Role.GUEST)
                .build();
    }
}
