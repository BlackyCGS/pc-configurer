package by.pcconf.pcconfigurer.service;

import by.pcconf.pcconfigurer.entity.PcConfiguration;

public interface CompatibilityService {

  boolean isCompatible(PcConfiguration config);
}
