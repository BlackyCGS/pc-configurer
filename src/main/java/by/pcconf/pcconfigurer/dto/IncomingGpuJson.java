package by.pcconf.pcconfigurer.dto;

public record IncomingGpuJson(
        Integer id,
        Float price,
        GpuInner gpu
) {
  public record GpuInner(
          String name,
          String producer,
          Integer boostClock,
          Integer vram,
          Integer tdp,
          Integer hdmi,
          Integer displayPort,
          Integer dvi,
          Integer vga
  ) {}
}
