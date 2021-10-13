package io.simpolor.client.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public final class SecurityContext {

    private static final String ANONYMOUS_USER  = "anonymousUser";

    public static UserDetails getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated()){
            // ignoring에 포함되지 않는 경우 무조건 인증을 통과하며,
            if (!isAnonymousUser(authentication.getName())){
                return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            }
        }
        return null;
    }

    public static boolean isAuthenticated(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null && !isAnonymousUser(authentication.getName()) ){
            return authentication.isAuthenticated();
        }
        return false;
    }

    private static boolean isAnonymousUser(String name){
        return ANONYMOUS_USER.equals(name);
    }
}
