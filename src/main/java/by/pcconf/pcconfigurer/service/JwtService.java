package by.pcconf.pcconfigurer.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.function.Function;

public interface JwtService {

  String extractUsername(String token);

  <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

  List<String> generateTokenPair(UserDetails userDetails);

  boolean validateRefreshToken(String token);

  boolean isRefreshTokenValid(String refreshToken);

  List<String> updateTokenPair(String refreshToken);

  boolean isTokenValid(String token, UserDetails userDetails);

  boolean isTokenExpired(String token);

  void revokeUser(int id);

  String extractTokenFromCookies(Cookie[] cookies);
}
