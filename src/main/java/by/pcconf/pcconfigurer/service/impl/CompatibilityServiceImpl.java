package by.pcconf.pcconfigurer.service.impl;

import by.pcconf.pcconfigurer.entity.*;
import by.pcconf.pcconfigurer.service.CompatibilityService;
import by.pcconf.pcconfigurer.service.ExternalApiService;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public class CompatibilityServiceImpl implements CompatibilityService {

  private final ExternalApiService externalApiService;

  CompatibilityServiceImpl(ExternalApiService externalApiService) {
    this.externalApiService = externalApiService;
  }


  @Override
  public boolean isCompatible(PcConfiguration config) {
    if (config.getGpuId() != null && config.getCpuId() != null && config.getPsuId() != null) {
      boolean tdpCheck = isTdpEnough(config.getGpuId(), config.getCpuId(), config.getPsuId());
      if (!tdpCheck) {
        return false;
      }
    }
    if (config.getCpuId() != null && config.getMotherboardId() != null) {
      boolean socketCheck = isSocketCompatible(config.getCpuId(), config.getMotherboardId());
      if (!socketCheck) {
        return false;
      }
    }
    if (config.getRamId() != null && config.getRamAmount() != null && config.getMotherboardId() != null) {
      boolean ramCheck = isRamCompatible(config.getRamId(), config.getRamAmount(), config.getMotherboardId());
      if (!ramCheck) {
        return false;
      }
    }
    if (config.getMotherboardId() != null && config.getPsuId() != null && config.getPcCaseId() != null) {
      return isCaseCompatible(config.getMotherboardId(), config.getPsuId(), config.getPcCaseId());
    }
    return true;
  }

  private boolean isTdpEnough(@NonNull Integer gpuId,
                              @NonNull Integer cpuId,
                              @NonNull Integer psuId) {
    Gpu gpu = externalApiService.getGpu(gpuId);
    Cpu cpu = externalApiService.getCpu(cpuId);
    Psu psu = externalApiService.getPsu(psuId);
    int realTdp = gpu.getTdp() + cpu.getTdp();
    return realTdp <= psu.getWatt();
  }

  private boolean isSocketCompatible(@NonNull Integer cpuId,
                                     @NonNull Integer motherboardId) {
    Cpu cpu = externalApiService.getCpu(cpuId);
    Motherboard motherboard = externalApiService.getMotherboard(motherboardId);
    return cpu.getSocket().equals(motherboard.getSocket());
  }

  public boolean isRamCompatible(@NonNull Integer ramId,
                                 @NonNull Integer ramAmount,
                                 @NonNull Integer motherboardId) {
    Ram ram = externalApiService.getRam(ramId);
    Motherboard motherboard = externalApiService.getMotherboard(motherboardId);
    String ramType = ram.getRamType().substring(0, 3);
    return ramType.equals(motherboard.getMemoryType()) && ramAmount.equals(motherboard.getRamSlots());
  }

  public boolean isCaseCompatible(@NonNull Integer motherboardId,
                                  @NonNull Integer psuId,
                                  @NonNull Integer pcCaseId) {
    Motherboard motherboard = externalApiService.getMotherboard(motherboardId);
    Psu psu = externalApiService.getPsu(psuId);
    PcCase pcCase = externalApiService.getPcCase(pcCaseId);
    return pcCase.getMotherboard().equals(motherboard.getFormFactor()) && pcCase.getPowerSupply().equals(psu.getSize());
  }
}
