package com.josolha.solhajo.domain.user.service;

import com.josolha.solhajo.domain.user.dto.internal.LoginType;
import com.josolha.solhajo.domain.user.dto.request.UserRequestDto;
import com.josolha.solhajo.domain.user.dto.response.UserResponseDto;
import com.josolha.solhajo.domain.user.dto.response.UserResponseDto.reissueToken;
import com.josolha.solhajo.domain.user.entity.Authority;
import com.josolha.solhajo.domain.user.entity.User;
import com.josolha.solhajo.domain.user.repository.UserRepository;
import com.josolha.solhajo.security.authobject.CustomUserDetails;
import com.josolha.solhajo.security.jwt.JwtTokenProvider;
import com.josolha.solhajo.util.ApiResponse;
import com.josolha.solhajo.util.LogUtil;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final RedisTemplate redisTemplate;

    private final JwtTokenProvider jwtTokenProvider;
    private final ApiResponse response;

    public ResponseEntity<?> register(UserRequestDto.Register register) {
        if (userRepository.findByEmail(register.getEmail()).isPresent()) {
            return response.fail("이미 사용중인 이메일 입니다.");
        }
        userRepository.save(User.builder()
                .name(register.getName())
                .password(passwordEncoder.encode(register.getPassword()))
                .email(register.getEmail())
                .loginType(LoginType.LOCAL)
                .roles(Collections.singletonList(Authority.ROLE_USER.name()))
                .build());
        return response.success();
    }
    public ResponseEntity<?> login(UserRequestDto.Login login) throws UsernameNotFoundException {

        //1. Login ID/PW 를 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword());
        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        LogUtil.customInfo("authentication = "+authentication);
        //3.다운캐스팅으로 접근
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        //4.access 토근 발급
        String accessToken = jwtTokenProvider.generateAccessToken(authentication, userDetails.getUserId());
        LogUtil.customInfo("accessToken = " + accessToken);
        //5.refresh 토근 발급
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails.getUserId());
        LogUtil.customInfo("refreshToken = " + refreshToken);
        //6.redis 저장
        redisTemplate.opsForValue()
                .set("RT:" + userDetails.getUserId(), refreshToken, jwtTokenProvider.getRefreshTokenExpiredTime(),
                        TimeUnit.MILLISECONDS);
        reissueToken result = reissueToken.builder().accessToken(accessToken).build();
        return response.success("성공적으로 로그인 완료했습니다.", result);
    }

    public ResponseEntity<?> reissue(UserRequestDto.Reissue reissue) {

        Authentication authentication = jwtTokenProvider.getAuthentication(reissue.getAccessToken());
        String refreshToken = (String) redisTemplate.opsForValue().get("RT:" + authentication.getName());
        if (ObjectUtils.isEmpty(refreshToken)) {
            return ResponseEntity.ok("값이 유효하지 않습니다");
        }
        String accessToken = jwtTokenProvider.generateAccessToken(authentication,
                Long.valueOf(authentication.getName()));
        System.out.println("reissue,authentication = " + authentication.getName());
        System.out.println("reissue,accessToken = " + accessToken);

        reissueToken result = reissueToken.builder().accessToken(accessToken).build();
        return response.success("성공적으로 토큰이 재발급 되었습니다", result);
    }

    public ResponseEntity<?> logout(UserRequestDto.Logout logout) {

        if (!jwtTokenProvider.validateToken(logout.getAccessToken())) {
            return response.error("Unauthorized");
        }
        Authentication authentication = jwtTokenProvider.getAuthentication(logout.getAccessToken());
        if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
            redisTemplate.delete("RT:" + authentication.getName());
        }
        Long expiration = jwtTokenProvider.getExpiration(logout.getAccessToken());
        redisTemplate.opsForValue()
                .set(logout.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);
        return response.success();
    }

    public ResponseEntity<?> authorizeCheck(UserRequestDto.Authorize authorize) {
        if (!jwtTokenProvider.validateToken(authorize.getAccessToken())) {
            return response.error("Unauthorized");
        }
        Optional<User> userOptional = userRepository.findById(jwtTokenProvider.getUserIdFromToken(authorize.getAccessToken()));
        if (!userOptional.isPresent()) {
            return response.fail("해당 유저 없음");
        }
        UserResponseDto.Authorize result = UserResponseDto.Authorize.builder().userName(userOptional.get().getName())
                .build();
        return response.success("성공적으로 유저 이름 확인됌",result);
    }
}

