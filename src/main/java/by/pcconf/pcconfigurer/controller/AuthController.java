package by.pcconf.pcconfigurer.controller;

import by.pcconf.pcconfigurer.dto.LoginRequest;
import by.pcconf.pcconfigurer.dto.UserDto;
import by.pcconf.pcconfigurer.dto.UserRequest;
import by.pcconf.pcconfigurer.entity.User;
import by.pcconf.pcconfigurer.service.AuthService;
import by.pcconf.pcconfigurer.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final JwtService jwtService;

  @Value("${security.jwt.expiration-time}")
  private long jwtExpiration;

  @Value("${security.jwt.refresh.expiration-time}")
  private long refreshExpirationTime;

  @Autowired
  public AuthController(AuthService authService, JwtService jwtService) {
    this.authService = authService;
    this.jwtService = jwtService;
  }

  @PostMapping("/signup")
  public ResponseEntity<UserDto> register(@RequestBody UserRequest registerUserDto) {
    UserDto registeredUser = authService.signUp(registerUserDto);

    return ResponseEntity.ok(registeredUser);
  }

  @PostMapping("/login")
  public ResponseEntity<String> authenticate(@RequestBody LoginRequest loginUserDto) {
    User authenticatedUser = authService.authenticate(loginUserDto);

    List<String> jwtTokens = jwtService.generateTokenPair(authenticatedUser);

    return prepareAndReturnCookies(jwtTokens);
  }

  private ResponseEntity<String> prepareAndReturnCookies(List<String> jwtTokens) {
    ResponseCookie jwtAccess = ResponseCookie.from("jwt", jwtTokens.get(0))
            .httpOnly(true)
            //.secure(true)
            .sameSite("Strict")
            .path("/")
            .maxAge(jwtExpiration / 1000)
            .build();

    ResponseCookie jwtRefresh = ResponseCookie.from("jwtr", jwtTokens.get(1))
            .httpOnly(true)
            //.secure(true)
            .sameSite("Strict")
            .path("/")
            .maxAge(refreshExpirationTime / 1000)
            .build();
    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, jwtAccess.toString())
            .header(HttpHeaders.SET_COOKIE, jwtRefresh.toString())
            .body(jwtTokens.toString());
  }
}
