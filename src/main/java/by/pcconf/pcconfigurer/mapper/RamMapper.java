package by.pcconf.pcconfigurer.mapper;

import by.pcconf.pcconfigurer.dto.IncomingRamJson;
import by.pcconf.pcconfigurer.entity.Ram;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RamMapper {

  @Mapping(target = "producer", source = "ram.producer")
  @Mapping(target = "ramType", source = "ram.ramType")
  @Mapping(target = "size", source = "ram.size")
  @Mapping(target = "timings", source = "ram.timings")
  Ram toEntity(IncomingRamJson incomingRamJson);
}
