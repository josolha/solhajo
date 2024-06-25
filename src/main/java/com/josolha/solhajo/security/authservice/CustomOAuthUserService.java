package com.josolha.solhajo.security.authservice;

import com.josolha.solhajo.domain.user.entity.User;
import com.josolha.solhajo.domain.user.repository.UserRepository;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;


//사용 안함.
//@Service
@Slf4j
public class CustomOAuthUserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    public CustomOAuthUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        try {
            System.out.println("oAuth2User = " + oAuth2User);
            return processOAuth2User(oAuth2User);
        } catch (Exception ex) {
            OAuth2Error error = new OAuth2Error("oauth2_error", ex.getMessage(), null);
            throw new OAuth2AuthenticationException(error, ex.getMessage(), ex);
        }
    }

    private OAuth2User processOAuth2User(OAuth2User oAuth2User) {
        // 여기에서 사용자 정보를 추출
        String email = oAuth2User.getAttribute("email");
        // 데이터베이스에서 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        // 사용자 권한 설정
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        return new DefaultOAuth2User(Collections.singleton(authority), oAuth2User.getAttributes(), "email");
    }
}
