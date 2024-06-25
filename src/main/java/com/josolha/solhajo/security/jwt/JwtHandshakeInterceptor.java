package com.josolha.solhajo.security.jwt;

import com.josolha.solhajo.util.LogUtil;
import java.net.URI;
import java.util.Map;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public JwtHandshakeInterceptor(JwtTokenProvider jwtTokenProvider, RedisTemplate<String, String> redisTemplate) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String token = extractToken(request);
        LogUtil.customInfo("token :" + token);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String isLogout = redisTemplate.opsForValue().get(token);
            if (ObjectUtils.isEmpty(isLogout)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return true;
            }
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }

    private String extractToken(ServerHttpRequest request) {
        // 쿼리 파라미터에서 access_token 값을 추출
        URI tokens = request.getURI();
        LogUtil.customInfo("tokens : "+tokens);

        return null;
    }
}
