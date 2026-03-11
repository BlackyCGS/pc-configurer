package by.pcconf.pcconfigurer.controller;

import by.pcconf.pcconfigurer.dto.*;
import by.pcconf.pcconfigurer.entity.*;
import by.pcconf.pcconfigurer.service.ExternalApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/components")
public class ComponentsController {
  private final ExternalApiService externalApiService;

  @Autowired
  public ComponentsController(ExternalApiService externalApiService) {
    this.externalApiService = externalApiService;
  }

  @GetMapping("/cpu")
  public PagedResponse<Cpu> getCpus(
          @RequestParam(required = false) Float minPrice,
          @RequestParam(required = false) Float maxPrice,
          @RequestParam(required = false) Integer minCores,
          @RequestParam(required = false) Integer maxCores,
          @RequestParam(required = false) Integer minThreads,
          @RequestParam(required = false) Integer maxThreads,
          @RequestParam(required = false) Integer minTdp,
          @RequestParam(required = false) Integer maxTdp,
          @RequestParam(required = false) String socket,
          @RequestParam(required = false, defaultValue = "0") int pageNumber,
          @RequestParam(required = false, defaultValue = "20") int pageSize
  ) {
    CpuFilter filter = new CpuFilter(
            minPrice, maxPrice, minCores, maxCores, minThreads,
            maxThreads, minTdp, maxTdp, socket, pageNumber, pageSize
    );
    List<Cpu> cpus = externalApiService.getCpus(filter);
    Long totalItems = externalApiService.getRecordsAmount(filter, "ram");
    Double totalPages = Math.ceil((double) totalItems /pageSize);
    return new PagedResponse<>(
            cpus, totalItems, pageNumber, pageSize, totalPages
    );
  }

  @GetMapping("/cpu/{id}")
  public Cpu getCpuById(@PathVariable Integer id) {
    return externalApiService.getCpu(id);
  }

  @GetMapping("/gpu")
  public PagedResponse<Gpu> getGpus(
          @RequestParam(required = false) Float minPrice,
          @RequestParam(required = false) Float maxPrice,
          @RequestParam(required = false) Integer minBoostClock,
          @RequestParam(required = false) Integer maxBoostClock,
          @RequestParam(required = false) Integer minVram,
          @RequestParam(required = false) Integer maxVram,
          @RequestParam(required = false) Integer minTdp,
          @RequestParam(required = false) Integer maxTdp,
          @RequestParam(required = false,
                  defaultValue = "0") int pageNumber,
          @RequestParam(required = false,
                  defaultValue = "20") int pageSize
  ) {
    GpuFilter filter = new GpuFilter(
            minPrice, maxPrice, minBoostClock, maxBoostClock,
            minVram, maxVram, minTdp, maxTdp, pageNumber, pageSize
    );
    List<Gpu> gpus = externalApiService.getGpus(filter);
    Long totalItems = externalApiService.getRecordsAmount(filter, "ram");
    Double totalPages = Math.ceil((double) totalItems /pageSize);
    return new PagedResponse<>(
            gpus, totalItems, pageNumber, pageSize, totalPages
    );
  }

  @GetMapping("/gpu/{id}")
  public Gpu getGpuById(@PathVariable Integer id) {
    return externalApiService.getGpu(id);
  }

  @GetMapping("/motherboard")
  public PagedResponse<Motherboard> getMotherboards(
          @RequestParam(required = false) Float minPrice,
          @RequestParam(required = false) Float maxPrice,
          @RequestParam(required = false) String socket,
          @RequestParam(required = false) String chipset,
          @RequestParam(required = false) String formFactor,
          @RequestParam(required = false) String memoryType,
          @RequestParam(required = false, defaultValue = "0") int pageNumber,
          @RequestParam(required = false, defaultValue = "20") int pageSize
  ) {
    MotherboardFilter filter = new MotherboardFilter(
            minPrice, maxPrice, socket, chipset,
            formFactor, memoryType, pageNumber, pageSize
    );
    List<Motherboard> motherboards = externalApiService.getMotherboards(filter);
    Long totalItems = externalApiService.getRecordsAmount(filter, "ram");
    Double totalPages = Math.ceil((double) totalItems /pageSize);
    return new PagedResponse<>(
            motherboards, totalItems, pageNumber, pageSize, totalPages
    );
  }

  @GetMapping("/motherboard/{id}")
  public Motherboard getMotherboardById(@PathVariable Integer id) {
    return externalApiService.getMotherboard(id);
  }

  @GetMapping("/pc_case")
  public PagedResponse<PcCase> getPcCases(
          @RequestParam(required = false) Float minPrice,
          @RequestParam(required = false) Float maxPrice,
          @RequestParam(required = false) String motherboard,
          @RequestParam(required = false) String powerSupply,
          @RequestParam(required = false, defaultValue = "0") int pageNumber,
          @RequestParam(required = false, defaultValue = "20") int pageSize
  ) {
    PcCaseFilter filter = new PcCaseFilter(
            minPrice, maxPrice, motherboard, powerSupply,
            pageNumber, pageSize
    );
    List<PcCase> pcCases = externalApiService.getPcCases(filter);
    Long totalItems = externalApiService.getRecordsAmount(filter, "ram");
    Double totalPages = Math.ceil((double) totalItems /pageSize);
    return new PagedResponse<>(
            pcCases, totalItems, pageNumber, pageSize, totalPages
    );
  }

  @GetMapping("/pc_case/{id}")
  public PcCase getPcCaseById(@PathVariable Integer id) {
    return externalApiService.getPcCase(id);
  }

  @GetMapping("/psu")
  public PagedResponse<Psu> getPsus(
          @RequestParam(required = false) Float minPrice,
          @RequestParam(required = false) Float maxPrice,
          @RequestParam(required = false) Integer minWatt,
          @RequestParam(required = false) Integer maxWatt,
          @RequestParam(required = false) String size,
          @RequestParam(required = false) String efficiencyRating,
          @RequestParam(required = false, defaultValue = "0") int pageNumber,
          @RequestParam(required = false, defaultValue = "20") int pageSize
  ) {
    PsuFilter filter = new PsuFilter(
            minPrice, maxPrice, minWatt, maxWatt, size,
            efficiencyRating, pageNumber, pageSize
    );
    List<Psu> psuList = externalApiService.getPsus(filter);
    Long totalItems = externalApiService.getRecordsAmount(filter, "ram");
    Double totalPages = Math.ceil((double) totalItems /pageSize);
    return new PagedResponse<>(
            psuList, totalItems, pageNumber, pageSize, totalPages
    );
  }

  @GetMapping("/psu/{id}")
  public Psu getPsuById(@PathVariable Integer id) {
    return externalApiService.getPsu(id);
  }

  @GetMapping("/ram")
  public PagedResponse<Ram> getRams(
          @RequestParam(required = false) Float minPrice,
          @RequestParam(required = false) Float maxPrice,
          @RequestParam(required = false) Integer minSize,
          @RequestParam(required = false) Integer maxSize,
          @RequestParam(required = false) String ramType,
          @RequestParam(required = false) String timings,
          @RequestParam(required = false, defaultValue = "0") int pageNumber,
          @RequestParam(required = false, defaultValue = "20") int pageSize
  ) {
    RamFilter filter = new RamFilter(
            minPrice, maxPrice, minSize, maxSize,
            ramType, timings, pageNumber, pageSize
    );
    List<Ram> rams = externalApiService.getRams(filter);
    Long totalItems = externalApiService.getRecordsAmount(filter, "ram");
    Double totalPages = Math.ceil((double) totalItems /pageSize);
    return new PagedResponse<>(
            rams, totalItems, pageNumber, pageSize, totalPages
    );
  }

  @GetMapping("/ram/{id}")
  public Ram getRamById(@PathVariable Integer id) {
    return externalApiService.getRam(id);
  }
}
