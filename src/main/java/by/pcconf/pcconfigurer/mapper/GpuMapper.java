package by.pcconf.pcconfigurer.mapper;

import by.pcconf.pcconfigurer.dto.IncomingGpuJson;
import by.pcconf.pcconfigurer.entity.Gpu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GpuMapper {
  @Mapping(target = "producer", source = "gpu.producer")
  @Mapping(target = "boostClock", source = "gpu.boostClock")
  @Mapping(target = "vram", source = "gpu.vram")
  @Mapping(target = "tdp", source = "gpu.tdp")
  @Mapping(target = "hdmi", source = "gpu.hdmi")
  @Mapping(target = "displayPort", source = "gpu.displayPort")
  @Mapping(target = "dvi", source = "gpu.dvi")
  @Mapping(target = "vga", source = "gpu.vga")
  Gpu toEntity(IncomingGpuJson incomingGpuJson);
}
