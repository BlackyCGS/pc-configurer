package by.pcconf.pcconfigurer.mapper;

import by.pcconf.pcconfigurer.dto.IncomingPsuJson;
import by.pcconf.pcconfigurer.entity.Psu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PsuMapper {

  @Mapping(target = "producer", source = "psu.producer")
  @Mapping(target = "watt", source = "psu.watt")
  @Mapping(target = "size", source = "psu.size")
  @Mapping(target = "efficiencyRating", source = "psu.efficiencyRating")
  Psu toEntity(IncomingPsuJson incomingPsuJson);
}
