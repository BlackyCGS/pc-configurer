package by.pcconf.pcconfigurer.mapper;

import by.pcconf.pcconfigurer.dto.IncomingPcCaseJson;
import by.pcconf.pcconfigurer.entity.PcCase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PcCaseMapper {
  @Mapping(target = "producer", source = "pcCase.producer")
  @Mapping(target = "motherboard", source = "pcCase.motherboard")
  @Mapping(target = "powerSupply", source = "pcCase.powerSupply")
  PcCase toEntity(IncomingPcCaseJson incomingPcCaseJson);
}
