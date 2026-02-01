package api.basic.com.projects.service;

import api.basic.com.projects.dto.UserDto;
import api.basic.com.projects.dto.request.LoginRequestDto;
import api.basic.com.projects.dto.request.RegisterRequestDto;
import api.basic.com.projects.dto.response.TokenResponseDto;
import api.basic.com.projects.exception.FieldException;
import api.basic.com.projects.utils.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtUtil jwtUtil;

    public TokenResponseDto register (RegisterRequestDto registerRequestDto) {
        confirmdPassworMatch(registerRequestDto.getPassword(), registerRequestDto.getConfirmPassword());
        if(userService.existsByUsername(registerRequestDto.getUsername())){
            throw new FieldException(
                    "El nombre de usuario ya existe",
                    "username",
                    HttpStatus.BAD_REQUEST
            );
        }
        UserDto user = modelMapper.map(registerRequestDto, UserDto.class);
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        UserDto userSave = userService.save(user);
        return TokenResponseDto.builder()
                .token(jwtUtil.generateToken(userSave.getUsername()))
                .build();
    }

    public TokenResponseDto login (LoginRequestDto loginRequestDto) {
        UserDto user =  userService.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new FieldException(
                        "Credenciales incorrectas",
                        "username",
                        HttpStatus.BAD_REQUEST)
                );

        if(!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new FieldException(
                    "Credenciales incorrectas",
                    "password",
                    HttpStatus.BAD_REQUEST
            );
        }

        return TokenResponseDto.builder()
                .token(jwtUtil.generateToken(user.getUsername()))
                .build();
    }

    private void confirmdPassworMatch (String password, String confirmPassword) {
        if(!password.equals(confirmPassword)) {
            throw new FieldException(
                    "Las contraseñas no coinciden",
                    "confirmPassword",
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
