package com.josolha.solhajo.config;

import com.josolha.solhajo.domain.user.repository.UserRepository;
import com.josolha.solhajo.security.jwt.JwtAuthenticationFilter;
import com.josolha.solhajo.security.jwt.JwtTokenProvider;
import com.josolha.solhajo.security.jwt.entryPoint.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Log4j2
public class CustomSecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;
    private final UserRepository userRepository;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    //private final CustomOAuthUserService customOAuthUserService;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(
                        PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers(
                                        "/api/login",
                                        "/api/signup",
                                        "/api/reissue",
                                        "/api/logout",
                                        "/api/kakao-login-url",
                                        "/api/auth/kakao",
                                        "/api/kakao-callback",
                                        "/ws/**",
                                        "/api/user/authorize"
//                                        "/api/chat"
                                ).permitAll()
                                .anyRequest().authenticated())
                                //.anyRequest().permitAll()) //모두열기

//
//                .oauth2Login(oauth2 -> oauth2
//                        .redirectionEndpoint(redirection -> redirection
//                                .baseUri("/api/kakao-callback")) // 리디렉션 URI 설정
//                        .userInfoEndpoint(userInfo -> userInfo
//                                .userService(new CustomOAuthUserService(userRepository)))) // 여기에서 CustomOAuthUserService를 등록

                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint))

                .addFilterBefore(new JwtAuthenticationFilter(redisTemplate,jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);



        return http.build();
    }
}
