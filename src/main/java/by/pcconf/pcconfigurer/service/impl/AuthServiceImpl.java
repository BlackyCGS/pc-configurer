package by.pcconf.pcconfigurer.service.impl;

import by.pcconf.pcconfigurer.dto.LoginRequest;
import by.pcconf.pcconfigurer.dto.UserDto;
import by.pcconf.pcconfigurer.dto.UserRequest;
import by.pcconf.pcconfigurer.entity.User;
import by.pcconf.pcconfigurer.mapper.UserMapper;
import by.pcconf.pcconfigurer.repository.UserRepository;
import by.pcconf.pcconfigurer.service.AuthService;
import by.pcconf.pcconfigurer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final UserMapper userMapper;
  private final UserService userService;

  @Autowired
  public AuthServiceImpl(UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         AuthenticationManager authenticationManager,
                         UserMapper userMapper,
                         UserService userService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.userMapper = userMapper;
    this.userService = userService;
  }

  @Override
  public UserDto signUp(UserRequest userRequest) {
    return userService.createUser(userRequest);
  }

  @Override
  public User authenticate(LoginRequest input) {
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    input.getEmail(), input.getPassword()
            )
    );
    return userRepository.findByEmail(input.getEmail()).orElseThrow();
  }
}
