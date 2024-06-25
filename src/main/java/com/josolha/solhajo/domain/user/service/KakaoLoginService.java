package com.josolha.solhajo.domain.user.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.josolha.solhajo.domain.user.dto.internal.KakaoUserInfo;
import com.josolha.solhajo.domain.user.dto.internal.LoginType;
import com.josolha.solhajo.domain.user.entity.Authority;
import com.josolha.solhajo.domain.user.entity.User;
import com.josolha.solhajo.domain.user.repository.UserRepository;
import com.josolha.solhajo.security.jwt.JwtTokenProvider;
import com.josolha.solhajo.util.ApiResponse;
import com.josolha.solhajo.util.LogUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;


@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoLoginService {

    private final UserRepository userRepository;

    private final ApiResponse response;

    private final JwtTokenProvider jwtTokenProvider;

    private final RedisTemplate redisTemplate;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.client-secret}")
    private String clientSecret;


    @Transactional
    public ResponseEntity<?> handleKakaoLogin(String code) throws IOException {

        String accessToken = getAccessTokenFromKakao(code);
        KakaoUserInfo kakaoUserInfo = getUserEmailFromKakao(accessToken);

        Optional<User> existingUser = userRepository.findByEmail(kakaoUserInfo.getEmail());

        // 직접 사이트에 가입한 회원인 경우
        if (existingUser.isPresent() && existingUser.get().getLoginType() != LoginType.KAKAO) {
            return response.fail("직접 사이트에 가입한 회원입니다.");
        }

        // 카카오로 이미 가입된 경우, 1.로그인 처리
        if (existingUser.isPresent() && existingUser.get().getLoginType() == LoginType.KAKAO) {
            // 로그인 성공 처리 로직, 예를 들면 토큰 재발급
            String makeAccess = jwtTokenProvider.generateAccessToken(authenticationUserByKakao(kakaoUserInfo),
                    existingUser.get().getId());
            String refreshToken = jwtTokenProvider.generateRefreshToken(existingUser.get().getId());
            redisTemplate.opsForValue()
                    .set("RT:" + existingUser.get().getId(), refreshToken,
                            jwtTokenProvider.getRefreshTokenExpiredTime(),
                            TimeUnit.MILLISECONDS);
            return response.success("성공적으로 로그인 완료했습니다.", makeAccess);
        }

        //카카오로 처음 로그인시, 1.회원가입, 2.로그인 처리
        User user = registerUserByKakao(kakaoUserInfo);
        String makeAccess = jwtTokenProvider.generateAccessToken(authenticationUserByKakao(kakaoUserInfo),
                user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        redisTemplate.opsForValue()
                .set("RT:" + user.getId(), refreshToken, jwtTokenProvider.getRefreshTokenExpiredTime(),
                        TimeUnit.MILLISECONDS);
        return response.success("성공적으로 로그인 완료했습니다.", makeAccess);
    }

    private Authentication authenticationUserByKakao(KakaoUserInfo kakaoUserInfo) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(kakaoUserInfo.getEmail(), null, authorities);
        LogUtil.customInfo("authenticationToken = " + authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        return authenticationToken;
    }

    private User registerUserByKakao(KakaoUserInfo kakaoUserInfo) {
        return userRepository.save(User.builder()
                .name(kakaoUserInfo.getName())
                .loginType(LoginType.KAKAO)
                .email(kakaoUserInfo.getEmail())
                .roles(Collections.singletonList(Authority.ROLE_USER.name()))
                .build());
    }
    public String getKakaoLoginUrl() {
        URI uri = UriComponentsBuilder.fromUriString("https://kauth.kakao.com/oauth/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .build().toUri();

        LogUtil.customInfo("RETURN URI : "+uri);
        return uri.toString();
    }

    public String getAccessTokenFromKakao(String code) throws IOException {
        String reqURL = "https://kauth.kakao.com/oauth/token";
        String parameters = "grant_type=authorization_code" +
                "&client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&code=" + code +
                "&client_secret=" + clientSecret;

        URL url = new URL(reqURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.getOutputStream().write(parameters.getBytes(StandardCharsets.UTF_8));
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        br.close();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonMap = objectMapper.readValue(result.toString(),
                new TypeReference<Map<String, Object>>() {
                });
        String accessToken = (String) jsonMap.get("access_token");
        String refreshToken = (String) jsonMap.get("refresh_token");
        LogUtil.customInfo("Access Token : " + accessToken);
        LogUtil.customInfo("Refresh Token : " + refreshToken);
        return accessToken;
    }

    public KakaoUserInfo getUserEmailFromKakao(String accessToken) throws IOException {
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        URL url = new URL(reqURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        br.close();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonMap = objectMapper.readValue(result.toString(),
                new TypeReference<Map<String, Object>>() {
                });
        // 사용자 정보에서 이메일을 파싱
        Map<String, Object> kakaoAccount = (Map<String, Object>) jsonMap.get("kakao_account");
        String email = (String) kakaoAccount.get("email");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String name = (String) profile.get("nickname");
        LogUtil.customInfo("User Email : " + email);
        LogUtil.customInfo("User name : " + name);
        return KakaoUserInfo.builder()
                .email(email)
                .name(name)
                .build();
    }
}
