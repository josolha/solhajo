package com.josolha.solhajo.security.jwt;

import com.josolha.solhajo.util.LogUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer ";
    //private static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L;  // 30분
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L;  // 10초

    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;    // 7일


    private final Key key;

    public JwtTokenProvider(@Value("${jwt.secretKey}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Authentication authentication, Long userId) {

        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        long now = (new Date()).getTime();
        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(Long.toString(userId))
                .setIssuer("josolha")
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Long userId) {
        long now = (new Date()).getTime();
        Date RefreshTokenExpiresIn = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);
        return Jwts.builder()
                .setExpiration(RefreshTokenExpiresIn)
                .setIssuer("josolha")
                .setSubject(Long.toString(userId))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //토큰 -> Authentication
    public Authentication getAuthentication(String accessToken) {

        // "Bearer: " 접두사 제거
        if (accessToken.startsWith(BEARER_TYPE)) {
            accessToken = accessToken.substring((BEARER_TYPE).length());
        }
        LogUtil.customInfo("accessToken = " + accessToken);
        Claims claim = parseClaims(accessToken);

        if (claim.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }
        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claim.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        Long userId = Long.parseLong(claim.getSubject());

        //로그인 후 접근시 id 값과 권한만 있음.
        UserDetails principal = new User(claim.getSubject(), "", authorities);
        LogUtil.customInfo("principal ="+ principal);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public Long getUserIdFromToken(String accessToken){
        if (accessToken.startsWith(BEARER_TYPE)) {
            accessToken = accessToken.substring((BEARER_TYPE).length());
        }
        LogUtil.customInfo("accessToken = " + accessToken);
        Claims claim = parseClaims(accessToken);

        if (claim.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }
        return Long.parseLong(claim.getSubject());
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {

        // "Bearer: " 접두사 제거
        if (token.startsWith(BEARER_TYPE)) {
            token = token.substring((BEARER_TYPE).length());
        }
        LogUtil.customInfo("token = " + token);
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            LogUtil.customInfo("Invalid JWT Token :"+ e);
        } catch (ExpiredJwtException e) {
            LogUtil.customInfo("401 Expired JWT Token :"+ e);
        } catch (UnsupportedJwtException e) {
            LogUtil.customInfo("Unsupported JWT Token : "+e);
        } catch (IllegalArgumentException e) {
            LogUtil.customInfo("JWT claims string is empty. : "+ e);
        }
        return false;
    }

    public Long getRefreshTokenExpiredTime() {
        return REFRESH_TOKEN_EXPIRE_TIME;
    }

    public Long getExpiration(String accessToken) {

        if (accessToken.startsWith(BEARER_TYPE)) {
            accessToken = accessToken.substring((BEARER_TYPE).length());
        }
        // accessToken 남은 유효시간
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody()
                .getExpiration();
        // 현재 시간
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

}
