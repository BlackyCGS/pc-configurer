package by.pcconf.pcconfigurer.mapper;

import by.pcconf.pcconfigurer.dto.IncomingCpuJson;
import by.pcconf.pcconfigurer.entity.Cpu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CpuMapper {
  @Mapping(target = "producer", source = "cpu.producer")
  @Mapping(target = "name", source = "cpu.name")
  @Mapping(target = "cores", source = "cpu.cores")
  @Mapping(target = "threads", source = "cpu.threads")
  @Mapping(target = "tdp", source = "cpu.tdp")
  @Mapping(target = "socket", source = "cpu.socket")
  Cpu toEntity(IncomingCpuJson incomingCpuJson);
}
