package api.basic.com.projects.controller;

import api.basic.com.projects.dto.request.LoginRequestDto;
import api.basic.com.projects.dto.request.RegisterRequestDto;
import api.basic.com.projects.dto.response.ResponseDto;
import api.basic.com.projects.dto.response.TokenResponseDto;
import api.basic.com.projects.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto registerRequestDto) {
        TokenResponseDto tokenResponseDto = authService.register(registerRequestDto);
        return ResponseDto.response(tokenResponseDto, HttpStatus.CREATED.value());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        TokenResponseDto tokenResponseDto = authService.login(loginRequestDto);
        return ResponseDto.response(tokenResponseDto, HttpStatus.OK.value());
    }
}