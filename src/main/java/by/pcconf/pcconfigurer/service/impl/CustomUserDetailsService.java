package by.pcconf.pcconfigurer.service.impl;

import by.pcconf.pcconfigurer.entity.User;
import by.pcconf.pcconfigurer.exception.NotFoundCustomException;
import by.pcconf.pcconfigurer.repository.UserRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Autowired
  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @NullMarked
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user =
            userRepository.findByUsername(username).orElseThrow(() ->
                    new UsernameNotFoundException("User with username " + username + " not found"));
    return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            user.getAuthorities()
    );
  }
}
