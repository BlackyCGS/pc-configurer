package by.pcconf.pcconfigurer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MotherboardFilter(
        Float minPrice,
        Float maxPrice,
        String socket,
        String chipset,
        String formFactor,
        String memoryType,
        Integer pageNumber,
        Integer pageSize
) {
}
