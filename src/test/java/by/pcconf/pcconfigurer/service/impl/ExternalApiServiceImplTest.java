package by.pcconf.pcconfigurer.service.impl;

import by.pcconf.pcconfigurer.dto.*;
import by.pcconf.pcconfigurer.entity.*;
import by.pcconf.pcconfigurer.exception.BadRequestCustomException;
import by.pcconf.pcconfigurer.exception.NotFoundCustomException;
import by.pcconf.pcconfigurer.mapper.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExternalApiServiceImplSimpleTest {

  private ExternalApiServiceImpl service;

  private CpuMapper cpuMapper = mock(CpuMapper.class);
  private GpuMapper gpuMapper = mock(GpuMapper.class);
  private MotherboardMapper motherboardMapper = mock(MotherboardMapper.class);
  private PcCaseMapper pcCaseMapper = mock(PcCaseMapper.class);
  private PsuMapper psuMapper = mock(PsuMapper.class);
  private RamMapper ramMapper = mock(RamMapper.class);

  @BeforeEach
  void setUp() {
    RestClient.Builder builder = mock(RestClient.Builder.class);
    RestClient restClient = mock(RestClient.class);

    when(builder.build()).thenReturn(restClient);

    service = spy(new ExternalApiServiceImpl(
            builder,
            cpuMapper,
            gpuMapper,
            motherboardMapper,
            pcCaseMapper,
            psuMapper,
            ramMapper
    ));
  }


  @Test
  void getCpu_success() {
    IncomingCpuJson dto = new IncomingCpuJson(1, 100f,
            new IncomingCpuJson.CpuInner("Ryzen", "AMD", 6, 12, 65, "AM4"));
    Cpu cpu = new Cpu();

    doReturn(ComponentType.CPU).when(service).getComponentType(1);
    doReturn(dto).when(service).getSingleRecordById(1, IncomingCpuJson.class);
    when(cpuMapper.toEntity(dto)).thenReturn(cpu);

    Cpu result = service.getCpu(1);

    assertNotNull(result);
    verify(cpuMapper).toEntity(dto);
  }

  @Test
  void getCpu_wrongType() {
    doReturn(ComponentType.GPU).when(service).getComponentType(1);

    assertThrows(BadRequestCustomException.class,
            () -> service.getCpu(1));
  }

  @Test
  void getCpu_notFound() {
    doReturn(ComponentType.CPU).when(service).getComponentType(1);
    doReturn(null).when(service).getSingleRecordById(1, IncomingCpuJson.class);

    assertThrows(NotFoundCustomException.class,
            () -> service.getCpu(1));
  }


  @Test
  void getGpu_success() {
    IncomingGpuJson dto = new IncomingGpuJson(1, 200f,
            new IncomingGpuJson.GpuInner("RTX", "NVIDIA", 1700, 8, 200,1,1,0,0));
    Gpu gpu = new Gpu();

    doReturn(ComponentType.GPU).when(service).getComponentType(1);
    doReturn(dto).when(service).getSingleRecordById(1, IncomingGpuJson.class);
    when(gpuMapper.toEntity(dto)).thenReturn(gpu);

    assertNotNull(service.getGpu(1));
  }


  @Test
  void getRam_success() {
    IncomingRamJson dto = new IncomingRamJson(1, 50f,
            new IncomingRamJson.RamInner("Vengeance", "Corsair", "DDR4", 16, "CL16"));
    Ram ram = new Ram();

    doReturn(ComponentType.RAM).when(service).getComponentType(1);
    doReturn(dto).when(service).getSingleRecordById(1, IncomingRamJson.class);
    when(ramMapper.toEntity(dto)).thenReturn(ram);

    assertNotNull(service.getRam(1));
  }

  @Test
  void isTypeEquals_true() {
    doReturn(ComponentType.CPU).when(service).getComponentType(1);

    assertTrue(service.isTypeEquals(1, "cpu"));
  }
}