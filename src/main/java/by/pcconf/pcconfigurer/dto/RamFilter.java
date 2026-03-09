package by.pcconf.pcconfigurer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RamFilter(
        Float minPrice,
        Float maxPrice,
        Integer minSize,
        Integer maxSize,
        String ramType,
        String timings,
        Integer pageNumber,
        Integer pageSize
) {
}
