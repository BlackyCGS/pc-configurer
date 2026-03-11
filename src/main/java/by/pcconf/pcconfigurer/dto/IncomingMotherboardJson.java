package by.pcconf.pcconfigurer.dto;

import by.pcconf.pcconfigurer.entity.Motherboard;

public record IncomingMotherboardJson(
        Integer id,
        Float price,
        MotherboardInner motherboard
) {
  public record MotherboardInner(
          String name,
          String producer,
          String socket,
          String chipset,
          String formFactor,
          String memoryType,
          Integer sata,
          Integer ramSlots,
          Integer vga,
          Integer dvi,
          Integer displayPort,
          Integer hdmi
  ) {}
}
