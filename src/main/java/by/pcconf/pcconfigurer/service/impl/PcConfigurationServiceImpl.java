package by.pcconf.pcconfigurer.service.impl;

import by.pcconf.pcconfigurer.entity.ComponentType;
import by.pcconf.pcconfigurer.entity.PcConfiguration;
import by.pcconf.pcconfigurer.exception.*;
import by.pcconf.pcconfigurer.repository.PcConfigurationRepository;
import by.pcconf.pcconfigurer.service.ExternalApiService;
import by.pcconf.pcconfigurer.service.PcConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PcConfigurationServiceImpl implements PcConfigurationService {
  private final ExternalApiService externalApiService;
  PcConfigurationRepository configurationRepository;

  @Autowired
  public PcConfigurationServiceImpl(ExternalApiService externalApiService,
                                    PcConfigurationRepository configurationRepository) {
    this.externalApiService = externalApiService;
    this.configurationRepository = configurationRepository;
  }


  /**
   *
   * @param id configuration id
   * @return configuration
   */
  @Override
  public PcConfiguration getConfiguration(Integer id) {
    return configurationRepository.findById(id).orElseThrow(() -> new NotFoundCustomException("No configuration found with id: " + id));
  }

  @Override
  public List<PcConfiguration> getConfigurationByUser(Integer userId) {
    List<PcConfiguration> configurations = configurationRepository.findByUserId(userId);
    if (configurations.isEmpty()) {
      throw new NotFoundCustomException("No configuration found for user with id: " + userId);
    }
    return configurations;
  }

  @Transactional
  @Override
  public void deleteConfiguration(Integer id) {
    configurationRepository.delete(configurationRepository.findById(id).orElseThrow(() -> new NoContentCustomException("No content")));
  }

  @Transactional
  @Override
  public PcConfiguration saveConfiguration(PcConfiguration config) {
    if (config.getGpuId() != null && externalApiService.getComponentType(config.getGpuId()) != ComponentType.GPU) {
      throw new BadRequestCustomException("Invalid component type");
    }
    if (config.getCpuId() != null && externalApiService.getComponentType(config.getCpuId()) != ComponentType.CPU) {
      throw new BadRequestCustomException("Invalid component type");
    }
    if (config.getPcCaseId() != null && externalApiService.getComponentType(config.getPcCaseId()) != ComponentType.PC_CASE) {
      throw new BadRequestCustomException("Invalid component type");
    }
    if (config.getPsuId() != null && externalApiService.getComponentType(config.getPsuId()) != ComponentType.PSU) {
      throw new BadRequestCustomException("Invalid component type");
    }
    if (config.getMotherboardId() != null && externalApiService.getComponentType(config.getMotherboardId()) != ComponentType.MOTHERBOARD) {
      throw new BadRequestCustomException("Invalid component type");
    }
    if (config.getRamId() != null && externalApiService.getComponentType(config.getRamId()) != ComponentType.RAM) {
      throw new BadRequestCustomException("Invalid component type");
    }
    return configurationRepository.save(config);
  }
}
