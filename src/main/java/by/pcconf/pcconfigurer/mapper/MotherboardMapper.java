package by.pcconf.pcconfigurer.mapper;

import by.pcconf.pcconfigurer.dto.IncomingMotherboardJson;
import by.pcconf.pcconfigurer.entity.Motherboard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MotherboardMapper {
  @Mapping(target = "producer", source = "motherboard.producer")
  @Mapping(target = "socket", source = "motherboard.socket")
  @Mapping(target = "chipset", source = "motherboard.chipset")
  @Mapping(target = "formFactor", source = "motherboard.formFactor")
  @Mapping(target = "memoryType", source = "motherboard.memoryType")
  @Mapping(target = "sata", source = "motherboard.sata")
  @Mapping(target = "ramSlots", source = "motherboard.ramSlots")
  @Mapping(target = "vga", source = "motherboard.vga")
  @Mapping(target = "dvi", source = "motherboard.dvi")
  @Mapping(target = "displayPort", source = "motherboard.displayPort")
  @Mapping(target = "hdmi", source = "motherboard.hdmi")
  Motherboard toEntity(IncomingMotherboardJson incomingMotherboardJson);
}
