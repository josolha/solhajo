package com.josolha.solhajo.domain.user.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class UserRequestDto {

    @Getter
    @Builder
    @ToString
    public static class Register{
        private String name;
        private String email;
        private String password;
    }

    @Getter
    @Builder
    @ToString
    public static class Login{
        private String email;
        private String password;
    }

    @Getter
    @ToString
    @NoArgsConstructor
    public static class Reissue{
        private String accessToken;
    }

    @Getter
    @ToString
    @NoArgsConstructor
    public static class Logout{
        private String accessToken;
    }
    @Getter
    @ToString
    @NoArgsConstructor
    public static class Authorize{
        private String accessToken;
    }


}
