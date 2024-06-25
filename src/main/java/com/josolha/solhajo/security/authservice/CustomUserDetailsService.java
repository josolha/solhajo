package com.josolha.solhajo.security.authservice;

import com.josolha.solhajo.domain.user.entity.User;
import com.josolha.solhajo.domain.user.repository.UserRepository;
import com.josolha.solhajo.security.authobject.CustomUserDetails;
import com.josolha.solhajo.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    LogUtil.customInfo("비밀번호 혹은 이메일이 틀립니다. : "+username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });
        return new CustomUserDetails(user, user.getId());
    }
}

//사이트 회원가입,