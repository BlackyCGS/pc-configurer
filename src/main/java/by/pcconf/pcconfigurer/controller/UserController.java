package by.pcconf.pcconfigurer.controller;

import by.pcconf.pcconfigurer.dto.UserDto;
import by.pcconf.pcconfigurer.entity.UserPermission;
import by.pcconf.pcconfigurer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PutMapping("/{id}/role/{roleType}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserDto> updateUserRole(
          @PathVariable Integer id,
          @PathVariable String roleType
  ) {
    return ResponseEntity.ok(userService.updateUserRole(id,
            UserPermission.valueOf(roleType.toUpperCase())));
  }
}
