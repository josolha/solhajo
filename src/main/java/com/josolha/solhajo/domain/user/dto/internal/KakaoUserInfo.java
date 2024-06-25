package com.josolha.solhajo.domain.user.dto.internal;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class KakaoUserInfo {
    private String email;
    private String name;
}
