package by.pcconf.pcconfigurer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PcCaseFilter(
        Float minPrice,
        Float maxPrice,
        String motherboard,
        String powerSupply,
        Integer pageNumber,
        Integer pageSize
) {
}
