package by.pcconf.pcconfigurer.service.impl;

import by.pcconf.pcconfigurer.entity.*;
import by.pcconf.pcconfigurer.service.ExternalApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompatibilityServiceImplTest {

  @Mock
  private ExternalApiService externalApiService;

  @InjectMocks
  private CompatibilityServiceImpl compatibilityService;

  private PcConfiguration config;

  private Cpu cpu;
  private Gpu gpu;
  private Motherboard motherboard;
  private Ram ram;
  private Psu psu;
  private PcCase pcCase;

  @BeforeEach
  void setUp() {
    config = new PcConfiguration();

    cpu = new Cpu();
    cpu.setId(1);
    cpu.setSocket("AM4");
    cpu.setTdp(65);

    gpu = new Gpu();
    gpu.setId(10);
    gpu.setTdp(250);

    motherboard = new Motherboard();
    motherboard.setId(100);
    motherboard.setSocket("AM4");
    motherboard.setMemoryType("DDR4");
    motherboard.setRamSlots(4);
    motherboard.setFormFactor("ATX");

    ram = new Ram();
    ram.setId(200);
    ram.setRamType("DDR4 3200");

    psu = new Psu();
    psu.setId(300);
    psu.setWatt(650);
    psu.setSize("ATX");

    pcCase = new PcCase();
    pcCase.setId(400);
    pcCase.setMotherboard("ATX");
    pcCase.setPowerSupply("ATX");
  }


  @Test
  void isCompatible_allChecksPass_shouldReturnTrue() {
    config.setCpuId(1);
    config.setGpuId(10);
    config.setPsuId(300);
    config.setMotherboardId(100);
    config.setRamId(200);
    config.setRamAmount(4);
    config.setPcCaseId(400);

    when(externalApiService.getCpu(1)).thenReturn(cpu);
    when(externalApiService.getGpu(10)).thenReturn(gpu);
    when(externalApiService.getPsu(300)).thenReturn(psu);
    when(externalApiService.getMotherboard(100)).thenReturn(motherboard);
    when(externalApiService.getRam(200)).thenReturn(ram);
    when(externalApiService.getPcCase(400)).thenReturn(pcCase);

    assertTrue(compatibilityService.isCompatible(config));
  }

  @Test
  void isCompatible_emptyConfig_shouldReturnTrue() {
    assertTrue(compatibilityService.isCompatible(new PcConfiguration()));
  }

  @Test
  void isCompatible_tdpNotEnough_shouldReturnFalse() {
    config.setCpuId(1);
    config.setGpuId(10);
    config.setPsuId(301);

    when(externalApiService.getGpu(10)).thenReturn(gpu);
    when(externalApiService.getCpu(1)).thenReturn(cpu);

    Psu weakPsu = new Psu();
    weakPsu.setId(301);
    weakPsu.setWatt(300);
    when(externalApiService.getPsu(301)).thenReturn(weakPsu);

    assertFalse(compatibilityService.isCompatible(config));
  }

  @Test
  void isCompatible_socketMismatch_shouldReturnFalse() {
    config.setCpuId(2);
    config.setMotherboardId(100);

    Cpu wrongCpu = new Cpu();
    wrongCpu.setId(2);
    wrongCpu.setSocket("LGA1700");

    when(externalApiService.getCpu(2)).thenReturn(wrongCpu);
    when(externalApiService.getMotherboard(100)).thenReturn(motherboard);

    assertFalse(compatibilityService.isCompatible(config));
  }

  @Test
  void isCompatible_ramTypeMismatch_shouldReturnFalse() {
    config.setRamId(201);
    config.setRamAmount(4);
    config.setMotherboardId(100);

    Ram wrongRam = new Ram();
    wrongRam.setId(201);
    wrongRam.setRamType("DDR5 6000");

    when(externalApiService.getRam(201)).thenReturn(wrongRam);
    when(externalApiService.getMotherboard(100)).thenReturn(motherboard);

    assertFalse(compatibilityService.isCompatible(config));
  }

  @Test
  void isCompatible_ramAmountNotEqualToSlots_shouldReturnFalse() {
    config.setRamId(200);
    config.setRamAmount(2);          // не равно 4
    config.setMotherboardId(100);

    when(externalApiService.getRam(200)).thenReturn(ram);
    when(externalApiService.getMotherboard(100)).thenReturn(motherboard);

    assertFalse(compatibilityService.isCompatible(config));
  }

  @Test
  void isCompatible_caseMotherboardMismatch_shouldReturnFalse() {
    config.setMotherboardId(100);
    config.setPsuId(300);
    config.setPcCaseId(401);

    PcCase wrongCase = new PcCase();
    wrongCase.setId(401);
    wrongCase.setMotherboard("mATX");
    wrongCase.setPowerSupply("ATX");

    when(externalApiService.getMotherboard(100)).thenReturn(motherboard);
    when(externalApiService.getPsu(300)).thenReturn(psu);
    when(externalApiService.getPcCase(401)).thenReturn(wrongCase);

    assertFalse(compatibilityService.isCompatible(config));
  }

  @Test
  void isCompatible_casePsuSizeMismatch_shouldReturnFalse() {
    config.setMotherboardId(100);
    config.setPsuId(300);
    config.setPcCaseId(402);

    PcCase wrongCase = new PcCase();
    wrongCase.setId(402);
    wrongCase.setMotherboard("ATX");
    wrongCase.setPowerSupply("SFX");

    when(externalApiService.getMotherboard(100)).thenReturn(motherboard);
    when(externalApiService.getPsu(300)).thenReturn(psu);
    when(externalApiService.getPcCase(402)).thenReturn(wrongCase);

    assertFalse(compatibilityService.isCompatible(config));
  }

  @Test
  void isCompatible_missingSomeComponents_shouldReturnTrue() {
    config.setCpuId(1);
    config.setMotherboardId(100);

    when(externalApiService.getCpu(1)).thenReturn(cpu);
    when(externalApiService.getMotherboard(100)).thenReturn(motherboard);

    assertTrue(compatibilityService.isCompatible(config));
  }

  @Test
  void isCompatible_onlyTdpComponents_shouldCheckOnlyTdp() {
    config.setCpuId(1);
    config.setGpuId(10);
    config.setPsuId(300);

    when(externalApiService.getCpu(1)).thenReturn(cpu);
    when(externalApiService.getGpu(10)).thenReturn(gpu);
    when(externalApiService.getPsu(300)).thenReturn(psu);

    assertTrue(compatibilityService.isCompatible(config));
  }

  @Test
  void isCompatible_onlyCpuAndMotherboard_shouldCheckOnlySocket() {
    config.setCpuId(1);
    config.setMotherboardId(100);

    when(externalApiService.getCpu(1)).thenReturn(cpu);
    when(externalApiService.getMotherboard(100)).thenReturn(motherboard);

    assertTrue(compatibilityService.isCompatible(config));
  }

  @Test
  void isCompatible_onlyRamAndMotherboard_shouldCheckOnlyRam() {
    config.setRamId(200);
    config.setRamAmount(4);                    // должно совпадать!
    config.setMotherboardId(100);

    when(externalApiService.getRam(200)).thenReturn(ram);
    when(externalApiService.getMotherboard(100)).thenReturn(motherboard);

    assertTrue(compatibilityService.isCompatible(config));
  }

  @Test
  void isCompatible_onlyCaseComponents_shouldCheckOnlyCase() {
    config.setMotherboardId(100);
    config.setPsuId(300);
    config.setPcCaseId(400);

    when(externalApiService.getMotherboard(100)).thenReturn(motherboard);
    when(externalApiService.getPsu(300)).thenReturn(psu);
    when(externalApiService.getPcCase(400)).thenReturn(pcCase);

    assertTrue(compatibilityService.isCompatible(config));
  }

  @Test
  void isCompatible_ramAmountIsNull_shouldSkipRamCheck() {
    config.setRamId(200);
    config.setRamAmount(null);
    config.setMotherboardId(100);

    assertTrue(compatibilityService.isCompatible(config));
    verify(externalApiService, never()).getRam(any());
  }


  @Test
  void isRamCompatible_correctTypeAndSlots_shouldReturnTrue() {
    config.setRamId(200);
    config.setRamAmount(4);
    config.setMotherboardId(100);

    when(externalApiService.getRam(200)).thenReturn(ram);
    when(externalApiService.getMotherboard(100)).thenReturn(motherboard);

    assertTrue(compatibilityService.isCompatible(config));
  }

  @Test
  void isRamCompatible_shortRamType_shouldThrowException() {
    Ram shortRam = new Ram();
    shortRam.setId(205);
    shortRam.setRamType("DR");

    config.setRamId(205);
    config.setRamAmount(4);
    config.setMotherboardId(100);

    when(externalApiService.getRam(205)).thenReturn(shortRam);
    when(externalApiService.getMotherboard(100)).thenReturn(motherboard);

    assertThrows(StringIndexOutOfBoundsException.class,
            () -> compatibilityService.isCompatible(config));
  }
}