package api.basic.com.projects.service;

import api.basic.com.projects.dto.UserDto;
import api.basic.com.projects.entity.User;
import api.basic.com.projects.exception.FieldException;
import api.basic.com.projects.repository.IUserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private IUserRepository iUserRepository;

    @Autowired
    private ModelMapper modelMapper;

    public UserDto save(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(userDto.getPassword());
        User saveUser = iUserRepository.save(user);
        return modelMapper.map(saveUser, UserDto.class);
    }

    public Optional<UserDto> findByUsername(String username) {
        return iUserRepository.findByUsername(username).map(
                user -> {
                    return modelMapper.map(user, UserDto.class);
                }
        );
    }

    public boolean existsByUsername(String username) {
        return iUserRepository.existsByUsername(username);
    }

    public User getAuthenticatedUser(UserDetails userDetails) {
        return iUserRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new FieldException(
                    "Credenciales incorrectas",
                    "user",
                    HttpStatus.BAD_REQUEST)
                );
    }
}
