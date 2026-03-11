package by.pcconf.pcconfigurer.dto;

public record IncomingRamJson(
        Integer id,
        Float price,
        RamInner ram
) {
  public record RamInner(
          String name,
          String producer,
          String ramType,
          Integer size,
          String timings
  ) {}
}
