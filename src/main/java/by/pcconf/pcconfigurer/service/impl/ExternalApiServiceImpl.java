package by.pcconf.pcconfigurer.service.impl;

import by.pcconf.pcconfigurer.dto.*;
import by.pcconf.pcconfigurer.entity.*;
import by.pcconf.pcconfigurer.exception.BadRequestCustomException;
import by.pcconf.pcconfigurer.exception.ExternalApiUnauthorizedException;
import by.pcconf.pcconfigurer.exception.NotFoundCustomException;
import by.pcconf.pcconfigurer.mapper.*;
import by.pcconf.pcconfigurer.mapper.impl.RecordMapperImpl;
import by.pcconf.pcconfigurer.service.ExternalApiService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ExternalApiServiceImpl implements ExternalApiService {
  private final RestClient restClient;
  private final CpuMapper cpuMapper;
  private final GpuMapper gpuMapper;
  private final MotherboardMapper motherboardMapper;
  private final PcCaseMapper pcCaseMapper;
  private final PsuMapper psuMapper;
  private final RamMapper ramMapper;
  private final RecordMapper recordMapper;
  @Value("${EXTERNAL_API_ADDRESS:localhost}")
  private String externalApiAddress;
  @Value("${EXTERNAL_API_PORT:8080}")
  private Integer externalApiPort;
  private static final String PRODUCTS_URI = "/api/products/category/";
  private static final String SINGLE_PRODUCT_URI = "/api/products/";

  @Autowired
  public ExternalApiServiceImpl(RestClient.Builder builder,
                                CpuMapper cpuMapper,
                                GpuMapper gpuMapper,
                                MotherboardMapper motherboardMapper,
                                PcCaseMapper pcCaseMapper,
                                PsuMapper psuMapper,
                                RamMapper ramMapper
                                ) {
    this.restClient = builder.build();
    this.cpuMapper = cpuMapper;
    this.gpuMapper = gpuMapper;
    this.motherboardMapper = motherboardMapper;
    this.pcCaseMapper = pcCaseMapper;
    this.psuMapper = psuMapper;
    this.ramMapper = ramMapper;
    this.recordMapper = new RecordMapperImpl();
  }

  public <T extends Record, R extends Record> List<R> getRecords(T filter,
                                                                 String type,
                                                                 Class<R> responseType) {
    String uri = PRODUCTS_URI + type.trim();
    return restClient
            .get()
            .uri(uriBuilder -> {
              UriBuilder builder = uriBuilder
                      .scheme("http")
                      .host(externalApiAddress)
                      .port(externalApiPort)
                      .path(uri);
              recordMapper.toMap(filter).forEach(builder::queryParam);
              return builder.build();
            })
            .retrieve()
            .body(ParameterizedTypeReference.forType(
                    ResolvableType.forClassWithGenerics(List.class, responseType).getType()));
  }

  public <T extends Record> T getSingleRecordById(@NonNull Integer id, Class<T> responseType) {
    return restClient
            .get()
            .uri( uriBuilder -> {
              UriBuilder builder = uriBuilder
                      .scheme("http")
                      .host(externalApiAddress)
                      .port(externalApiPort)
                      .path(SINGLE_PRODUCT_URI + id);
              return builder.build();
            })
            .retrieve()
            .body(responseType);
  }

  @Override
  public <T extends Record> Long getRecordsAmount(T filter, String type) {
    String uri = PRODUCTS_URI + type.trim() + "/amount";
    return restClient
            .get()
            .uri(uriBuilder -> {
              UriBuilder builder = uriBuilder
                      .scheme("http")
                      .host(externalApiAddress)
                      .port(externalApiPort)
                      .path(uri);
              recordMapper.toMap(filter).forEach(builder::queryParam);
              return builder.build();
            })
            .retrieve()
            .body(Long.class);
  }

  @Override
  public ComponentType getComponentType(@NonNull Integer id) {
    String uri = SINGLE_PRODUCT_URI + id + "/type";
    String type = restClient
            .get()
            .uri(uriBuilder -> {
              UriBuilder builder = uriBuilder
                      .scheme("http")
                      .host(externalApiAddress)
                      .port(externalApiPort)
                      .path(uri);
              return builder.build();}
            )
            .retrieve()
            .body(String.class);
    if (type == null) {
      throw new NotFoundCustomException("Product type not found");
    }
    if (Arrays.asList(ComponentType.values()).contains(ComponentType.valueOf(type.toUpperCase()))) {
      return ComponentType.valueOf(type.toUpperCase());
    } else {
      throw new BadRequestCustomException("Illegal product type");
    }
  }

  @Override
  public boolean isTypeEquals(@NonNull Integer itemId,@NonNull String type) {
    ComponentType componentType = getComponentType(itemId);
    return ComponentType.valueOf(type.trim().toUpperCase()).equals(componentType);
  }

  @Override
  public void sendConfigToExternalApi(PcConfiguration config, String email) {
    PcConfigToCartDto dto = new PcConfigToCartDto(email, config);
    try {
      restClient.post()
              .uri(uriBuilder -> {
                UriBuilder builder = uriBuilder
                        .scheme("http")
                        .host(externalApiAddress)
                        .port(externalApiPort)
                        .path("/api/orders/fromConfig");
                return builder.build();
              })
              .contentType(MediaType.APPLICATION_JSON)
              .body(dto)
              .retrieve()
              .toBodilessEntity();
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode().is4xxClientError() &&
              (e.getStatusCode().value() == 401 || e.getStatusCode().value() == 403)) {
        throw new ExternalApiUnauthorizedException(
                "Unauthorized access to external api"
        );
      }
      throw e;
    }
  }

  @Override
  public List<Cpu> getCpus(CpuFilter filter) {
    List<IncomingCpuJson> response = getRecords(filter, "cpu", IncomingCpuJson.class);
    List<Cpu> cpus = new ArrayList<>();
    if (response == null || response.isEmpty()) {
      throw new NotFoundCustomException("Cpus not found");
    }
    for (IncomingCpuJson incomingCpuJson : response) {
      cpus.add(cpuMapper.toEntity(incomingCpuJson));
    }
    return cpus;
  }

  @Override
  public Cpu getCpu(@NonNull Integer id) {
    if (getComponentType(id) != ComponentType.CPU) {
      throw new BadRequestCustomException("Cpu is not a cpu");
    }
    IncomingCpuJson response = getSingleRecordById(id, IncomingCpuJson.class);
    if (response == null) {
      throw new NotFoundCustomException("Cpu not found");
    }
    return cpuMapper.toEntity(response);
  }

  @Override
  public List<Gpu> getGpus(GpuFilter filter) {
    List<IncomingGpuJson> response = getRecords(filter, "gpu", IncomingGpuJson.class);
    List<Gpu> gpus = new ArrayList<>();
    if (response == null || response.isEmpty()) {
      throw new NotFoundCustomException("Gpus not found");
    }
    for (IncomingGpuJson incomingGpuJson : response) {
      gpus.add(gpuMapper.toEntity(incomingGpuJson));
    }
    return gpus;
  }

  @Override
  public Gpu getGpu(@NonNull Integer id) {
    if (getComponentType(id) != ComponentType.GPU) {
      throw new BadRequestCustomException("Gpu is not a gpu");
    }
    IncomingGpuJson response = getSingleRecordById(id, IncomingGpuJson.class);
    if (response == null) {
      throw new NotFoundCustomException("Gpu not found");
    }
    return gpuMapper.toEntity(response);
  }

  @Override
  public List<Motherboard> getMotherboards(MotherboardFilter filter) {
    List<IncomingMotherboardJson> response = getRecords(filter, "motherboard", IncomingMotherboardJson.class);
    List<Motherboard> motherboards = new ArrayList<>();
    if (response == null || response.isEmpty()) {
      throw new NotFoundCustomException("Motherboards not found");
    }
    for (IncomingMotherboardJson incomingMotherboardJson : response) {
      motherboards.add(motherboardMapper.toEntity(incomingMotherboardJson));
    }
    return motherboards;
  }

  @Override
  public Motherboard getMotherboard(@NonNull Integer id) {
    if (getComponentType(id) != ComponentType.MOTHERBOARD) {
      throw new BadRequestCustomException("Motherboard is not a motherboard");
    }
    IncomingMotherboardJson response = getSingleRecordById(id, IncomingMotherboardJson.class);
    if (response == null) {
      throw new NotFoundCustomException("Motherboard not found");
    }
    return motherboardMapper.toEntity(response);
  }

  @Override
  public List<PcCase> getPcCases(PcCaseFilter filter) {
    List<IncomingPcCaseJson> response = getRecords(filter, "pcCase", IncomingPcCaseJson.class);
    List<PcCase> pcCases = new ArrayList<>();
    if (response == null || response.isEmpty()) {
      throw new NotFoundCustomException("PcCases not found");
    }
    for (IncomingPcCaseJson incomingPcCaseJson : response) {
      pcCases.add(pcCaseMapper.toEntity(incomingPcCaseJson));
    }
    return pcCases;
  }

  @Override
  public PcCase getPcCase(@NonNull Integer id) {
    if (getComponentType(id) != ComponentType.PC_CASE) {
      throw new BadRequestCustomException("Pc case is not a pc case");
    }
    IncomingPcCaseJson response = getSingleRecordById(id, IncomingPcCaseJson.class);
    if (response == null) {
      throw new NotFoundCustomException("PcCase not found");
    }
    return pcCaseMapper.toEntity(response);
  }

  @Override
  public List<Psu> getPsus(PsuFilter filter) {
    List<IncomingPsuJson> response = getRecords(filter, "psu", IncomingPsuJson.class);
    List<Psu> psus = new ArrayList<>();
    if (response == null || response.isEmpty()) {
      throw new NotFoundCustomException("Psus not found");
    }
    for (IncomingPsuJson incomingPsuJson : response) {
      psus.add(psuMapper.toEntity(incomingPsuJson));
    }
    return psus;
  }

  @Override
  public Psu getPsu(@NonNull Integer id) {
    if (getComponentType(id) != ComponentType.PSU) {
      throw new BadRequestCustomException("Psu is not a psu");
    }
    IncomingPsuJson response = getSingleRecordById(id, IncomingPsuJson.class);
    if (response == null) {
      throw new NotFoundCustomException("Psu not found");
    }
    return psuMapper.toEntity(response);
  }

  @Override
  public List<Ram> getRams(RamFilter filter) {
    List<IncomingRamJson> response = getRecords(filter, "ram", IncomingRamJson.class);
    List<Ram> rams = new ArrayList<>();
    if (response == null || response.isEmpty()) {
      throw new NotFoundCustomException("Rams not found");
    }
    for (IncomingRamJson incomingRamJson : response) {
      rams.add(ramMapper.toEntity(incomingRamJson));
    }
    return rams;
  }

  @Override
  public Ram getRam(@NonNull Integer id) {
    if (getComponentType(id) != ComponentType.RAM) {
      throw new BadRequestCustomException("Ram is not a ram");
    }
    IncomingRamJson response = getSingleRecordById(id, IncomingRamJson.class);
    if (response == null) {
      throw new NotFoundCustomException("Ram not found");
    }
    return ramMapper.toEntity(response);
  }


}
