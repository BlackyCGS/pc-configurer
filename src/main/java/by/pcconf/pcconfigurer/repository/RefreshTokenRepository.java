package by.pcconf.pcconfigurer.repository;

import java.util.Date;
import java.util.Optional;

import by.pcconf.pcconfigurer.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

  boolean existsByToken(String refreshToken);

  RefreshToken findByToken(String refreshToken);

  void deleteByUserId(int id);

  void deleteByToken(String refreshToken);

  void deleteAllByUserId(int id);

  void deleteAllByExpirationTimeBefore(Date expirationTime);
}
