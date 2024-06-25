package com.josolha.solhajo.security.jwt.entryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.josolha.solhajo.util.ApiResponse;
import com.josolha.solhajo.util.LogUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ApiResponse apiResponse;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        LogUtil.customInfo("AuthException : "+ authException);

        ResponseEntity<?> apiResponseEntity = apiResponse.error("Unauthorized");
        response.setStatus(apiResponseEntity.getStatusCode().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String jsonResponse = objectMapper.writeValueAsString(apiResponseEntity.getBody());
        response.getWriter().write(jsonResponse);
    }
}
