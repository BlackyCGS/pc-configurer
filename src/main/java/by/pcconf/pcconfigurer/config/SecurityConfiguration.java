package by.pcconf.pcconfigurer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;

@Configuration
public class SecurityConfiguration {

  private static final String ADMIN = "ADMIN";

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    CookieClearingLogoutHandler cookies = new CookieClearingLogoutHandler("jwt",
            "jwtr");
    http
            .logout(logout -> {
              logout.logoutUrl("/api/auth/logout");
              logout.addLogoutHandler(cookies);
              logout.logoutSuccessUrl("/api/auth/logout/success");
            })
            .securityMatcher("/api/**")
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth").permitAll()
                    )
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authz -> authz
                    .anyRequest().permitAll()
            )
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }
}
