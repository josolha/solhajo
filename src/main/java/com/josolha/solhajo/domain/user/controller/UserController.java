package com.josolha.solhajo.domain.user.controller;

import com.josolha.solhajo.domain.user.dto.request.UserRequestDto;
import com.josolha.solhajo.domain.user.service.KakaoLoginService;
import com.josolha.solhajo.domain.user.service.UserService;
import com.josolha.solhajo.util.ApiResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final ApiResponse response;

    private final KakaoLoginService kakaoLoginService;

    @PostMapping("/api/signup")
    public ResponseEntity<?> userRegister(@RequestBody UserRequestDto.Register register) {
        return userService.register(register);
    }

    @PostMapping("/api/login")
    public ResponseEntity<?> userLogin(@RequestBody UserRequestDto.Login login,Error error) {
        return userService.login(login);
    }

    @PostMapping("/api/reissue")
    public ResponseEntity<?> reissue(@RequestBody UserRequestDto.Reissue reissue) {
        return userService.reissue(reissue);
    }

    @PostMapping("/api/logout")
    public ResponseEntity<?> logout(@RequestBody UserRequestDto.Logout logout){
        return userService.logout(logout);
    }

    @GetMapping("/api/kakao-login-url")
    public ResponseEntity<?> kakaoLoginUrl(){
        return response.success(kakaoLoginService.getKakaoLoginUrl());
    }

    @GetMapping("/api/kakao-callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam String code) throws IOException {
        //System.out.println("code = " + code);
        return response.success(kakaoLoginService.handleKakaoLogin(code));
    }
    @PostMapping("/api/user/authorize")
    public ResponseEntity<?> authorizeCheck(@RequestBody UserRequestDto.Authorize authorize){
        return userService.authorizeCheck(authorize);
    }

}
