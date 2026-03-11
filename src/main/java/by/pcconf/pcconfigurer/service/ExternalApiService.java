package by.pcconf.pcconfigurer.service;

import by.pcconf.pcconfigurer.dto.*;
import by.pcconf.pcconfigurer.entity.*;

import java.util.List;

public interface ExternalApiService {
  ComponentType getComponentType(Integer id);
  boolean isTypeEquals(Integer itemId, String type);
  void sendConfigToExternalApi(PcConfiguration config, String email);
  <T extends Record> Long getRecordsAmount(T filter, String type);
  List<Cpu> getCpus(CpuFilter filter);
  Cpu getCpu(Integer id);
  List<Gpu> getGpus(GpuFilter filter);
  Gpu getGpu(Integer id);
  List<Motherboard> getMotherboards(MotherboardFilter filter);
  Motherboard getMotherboard(Integer id);
  List<PcCase> getPcCases(PcCaseFilter filter);
  PcCase getPcCase(Integer id);
  List<Psu> getPsus(PsuFilter filter);
  Psu getPsu(Integer id);
  List<Ram> getRams(RamFilter filter);
  Ram getRam(Integer id);
}
