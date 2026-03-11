package by.pcconf.pcconfigurer.service;

import by.pcconf.pcconfigurer.dto.UserDto;
import by.pcconf.pcconfigurer.dto.UserRequest;
import by.pcconf.pcconfigurer.entity.User;
import by.pcconf.pcconfigurer.entity.UserPermission;
import jakarta.servlet.http.Cookie;

public interface UserService {

  UserDto createUser(UserRequest user);

  UserDto updateUser(UserRequest user);

  void deleteUser(UserRequest user);

  Integer getIdByUsername(String username);

  UserDto updateUserRole(Integer id, UserPermission permission);

  String getEmailByUsername(String username);

  User getUserByName(String name);
}
