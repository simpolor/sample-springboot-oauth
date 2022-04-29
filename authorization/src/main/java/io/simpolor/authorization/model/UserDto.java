package io.simpolor.authorization.model;

import io.simpolor.authorization.repository.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class UserDto {

    private Long id;
    private String email;
    private String name;

    public static UserDto of(User user){

        UserDto userDto = new UserDto();
        userDto.setId(user.getUserId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());

        return userDto;
    }

}
