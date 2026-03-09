package by.pcconf.pcconfigurer.service;

import by.pcconf.pcconfigurer.entity.PcConfiguration;

import java.util.List;

public interface PcConfigurationService {

  PcConfiguration getConfiguration(Integer id);
  List<PcConfiguration> getConfigurationByUser(Integer userId);
  void deleteConfiguration(Integer id);
  PcConfiguration saveConfiguration(PcConfiguration config);


}
