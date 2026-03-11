package by.pcconf.pcconfigurer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GpuFilter(
        Float minPrice,
        Float maxPrice,
        Integer minBoostClock,
        Integer maxBoostClock,
        Integer minVram,
        Integer maxVram,
        Integer minTdp,
        Integer maxTdp,
        Integer pageNumber,
        Integer pageSize
) {
}
