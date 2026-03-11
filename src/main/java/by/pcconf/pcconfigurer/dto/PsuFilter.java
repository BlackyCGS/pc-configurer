package by.pcconf.pcconfigurer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PsuFilter(
        Float minPrice,
        Float maxPrice,
        Integer minWatt,
        Integer maxWatt,
        String size,
        String efficiencyRating,
        Integer pageNumber,
        Integer pageSize
) {
}
