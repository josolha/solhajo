package com.josolha.solhajo.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class UserResponseDto {
    @Getter
    @AllArgsConstructor
    @Builder
    public static class TokenInfo{
        private String email;
        private String name;
        private String accessToken;
    }
    @Getter
    @ToString
    @Builder
    public static class Authorize{
        private String userName;
    }

    @Getter
    @ToString
    @Builder
    public static class reissueToken{
        private String accessToken;
    }
}
