package by.pcconf.pcconfigurer.controller;

import by.pcconf.pcconfigurer.entity.PcConfiguration;
import by.pcconf.pcconfigurer.exception.BadRequestCustomException;
import by.pcconf.pcconfigurer.exception.ForbiddenCustomException;
import by.pcconf.pcconfigurer.exception.UnauthorizedCustomException;
import by.pcconf.pcconfigurer.service.ExternalApiService;
import by.pcconf.pcconfigurer.service.JwtService;
import by.pcconf.pcconfigurer.service.PcConfigurationService;
import by.pcconf.pcconfigurer.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/configuration")
public class PcConfigurationController {
  private final PcConfigurationService pcConfigurationService;
  private final ExternalApiService externalApiService;
  private final JwtService jwtService;
  private final UserService userService;
  @Value("${EXTERNAL_API_ADDRESS:localhost}")
  private String externalApiAddress;
  @Value("${REDIRECT_PORT:80}")
  private Integer redirectPort;

  @Autowired
  public PcConfigurationController(PcConfigurationService pcConfigurationService,
                                   ExternalApiService externalApiService,
                                   JwtService jwtService,
                                   UserService userService) {
    this.pcConfigurationService = pcConfigurationService;
    this.jwtService = jwtService;
    this.userService = userService;
    this.externalApiService = externalApiService;
  }

  @GetMapping("/list")
  public List<PcConfiguration> getConfigurationsForUser(HttpServletRequest request) {
    String token = jwtService.extractTokenFromCookies(request.getCookies());
    String name = jwtService.extractUsername(token);
    return pcConfigurationService.getConfigurationByUser(userService.getIdByUsername(name));
  }

  @GetMapping("/id")
  public PcConfiguration getConfigurationById(@RequestParam("id") Integer id,
                                              HttpServletRequest request) {
    String token = jwtService.extractTokenFromCookies(request.getCookies());
    String name = jwtService.extractUsername(token);
    PcConfiguration config = pcConfigurationService.getConfiguration(id);
    if (!config.getUser().getId().equals(userService.getIdByUsername(name))) {
      throw new ForbiddenCustomException("User does not have permission to access this configuration");
    }
    return config;
  }

  @PutMapping()
  public PcConfiguration saveConfiguration(@RequestBody PcConfiguration configuration,
                                                HttpServletRequest request) {
    String token = jwtService.extractTokenFromCookies(request.getCookies());
    String name = jwtService.extractUsername(token);
    if (name == null) {
      throw new UnauthorizedCustomException("Unauthorized");
    }
    configuration.setUser(userService.getUserByName(name));
    return pcConfigurationService.saveConfiguration(configuration);
  }

  @DeleteMapping()
  public ResponseEntity<Void> deleteConfiguration(@RequestBody PcConfiguration configuration,
                                            HttpServletRequest request) {
    String name = extractNameFromToken(request);
    PcConfiguration repoConfig =
            pcConfigurationService.getConfiguration(configuration.getId());
    if (!userService.getIdByUsername(name).equals(repoConfig.getUser().getId())) {
      throw new ForbiddenCustomException("Forbidden");
    }
    pcConfigurationService.deleteConfiguration(configuration.getId());
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PostMapping("/toOrder")
  public ResponseEntity<Void> fromConfigToOrder(@RequestBody PcConfiguration configuration,
                                             HttpServletRequest request, UriComponentsBuilder uriComponentsBuilder) {
    String name = extractNameFromToken(request);
    PcConfiguration repoConfig =
            pcConfigurationService.getConfiguration(configuration.getId());
    if (!userService.getIdByUsername(name).equals(repoConfig.getUser().getId())) {
      throw new ForbiddenCustomException("Forbidden");
    }
    externalApiService.sendConfigToExternalApi(configuration,
            userService.getEmailByUsername(name));
    HttpHeaders headers = new HttpHeaders();
    String location = externalApiAddress +
            ":" +
            redirectPort +
            "/" +
            "cart";
    headers.setLocation(URI.create(location));
    return new ResponseEntity<>(headers, HttpStatus.FOUND);
  }

  private String extractNameFromToken(HttpServletRequest request) {
    String token = jwtService.extractTokenFromCookies(request.getCookies());
    return jwtService.extractUsername(token);
  }
}


