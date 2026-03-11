package by.pcconf.pcconfigurer.dto;

import by.pcconf.pcconfigurer.exception.InternalServerErrorCustomException;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CpuFilter(
        Float minPrice,
        Float maxPrice,
        Integer minCores,
        Integer maxCores,
        Integer minThreads,
        Integer maxThreads,
        Integer minTdp,
        Integer maxTdp,
        String socket,
        Integer pageNumber,
        Integer pageSize
) {

}
