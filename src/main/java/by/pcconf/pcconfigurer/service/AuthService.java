package by.pcconf.pcconfigurer.service;

import by.pcconf.pcconfigurer.dto.LoginRequest;
import by.pcconf.pcconfigurer.dto.UserDto;
import by.pcconf.pcconfigurer.dto.UserRequest;
import by.pcconf.pcconfigurer.entity.User;

public interface AuthService {
  UserDto signUp(UserRequest userRequest);

  User authenticate(LoginRequest input);

}
