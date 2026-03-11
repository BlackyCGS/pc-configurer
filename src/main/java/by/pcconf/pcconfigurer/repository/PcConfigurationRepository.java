package by.pcconf.pcconfigurer.repository;

import by.pcconf.pcconfigurer.entity.PcConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PcConfigurationRepository extends JpaRepository<PcConfiguration, Integer> {
  List<PcConfiguration> findByUserId(int userId);
}
