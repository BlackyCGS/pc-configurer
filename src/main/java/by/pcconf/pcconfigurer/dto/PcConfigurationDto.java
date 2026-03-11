package by.pcconf.pcconfigurer.dto;

import jakarta.validation.constraints.Min;

public class PcConfigurationDto {
  Integer id;
  Integer cpuId;
  Integer gpuId;
  Integer motherboardId;
  Integer psuId;
  Integer pcCaseId;
  Integer ramId;
  @Min(value = 0, message = "Ram amount needs to be greater than 0")
  Integer ramAmount;
  Integer userId;
}
