package by.pcconf.pcconfigurer.service.impl;

import by.pcconf.pcconfigurer.dto.UserDto;
import by.pcconf.pcconfigurer.dto.UserRequest;
import by.pcconf.pcconfigurer.entity.User;
import by.pcconf.pcconfigurer.entity.UserPermission;
import by.pcconf.pcconfigurer.exception.BadRequestCustomException;
import by.pcconf.pcconfigurer.exception.NotFoundCustomException;
import by.pcconf.pcconfigurer.mapper.UserMapper;
import by.pcconf.pcconfigurer.repository.UserRepository;
import by.pcconf.pcconfigurer.service.UserService;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private static final String NOT_FOUND_MESSAGE = "User not found";

  @Autowired
  public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
                         PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public UserDto createUser(UserRequest user) {
    User userEntity = userMapper.toEntity(user);
    if (userRepository.existsByEmail(userEntity.getEmail())) {
      throw new BadRequestCustomException("Email address already in use");
    }
    if (userRepository.existsByUsername(userEntity.getUsername())) {
      throw new BadRequestCustomException("Username already in use");
    }
    userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
    return userMapper.toDto(userRepository.save(userEntity));
  }

  @Override
  public UserDto updateUser(UserRequest user) {
    User userEntity = userMapper.toEntity(user);
    if (userRepository.existsById(userEntity.getId())) {
      return userMapper.toDto(userRepository.save(userEntity));
    } else {
      throw new NotFoundCustomException(NOT_FOUND_MESSAGE);
    }
  }

  @Override
  @Transactional
  public void deleteUser(UserRequest user) {
    userRepository.delete(userRepository.findByEmail(user.getEmail()).orElseThrow(
            () -> new NotFoundCustomException(NOT_FOUND_MESSAGE)
    ));
  }

  @Override
  public Integer getIdByUsername(String username) {
    User user = userRepository.findByUsername(username).orElseThrow(
            () -> new NotFoundCustomException(NOT_FOUND_MESSAGE)
    );
    return user.getId();
  }

  @Override
  public UserDto updateUserRole(Integer id, UserPermission permission) {
    User user = userRepository.findById(id).orElseThrow(
            () -> new NotFoundCustomException(NOT_FOUND_MESSAGE)
    );
    user.setPermission(permission);
    return userMapper.toDto(userRepository.save(user));
  }

  @Override
  public String getEmailByUsername(String username) {
    User user = userRepository.findByUsername(username).orElseThrow(
            () -> new NotFoundCustomException(NOT_FOUND_MESSAGE)
    );
    return user.getEmail();
  }

  @Override
  public User getUserByName(String name) {
    return userRepository.findByUsername(name).orElseThrow(
            () -> new NotFoundCustomException(NOT_FOUND_MESSAGE)
    );
  }


}
