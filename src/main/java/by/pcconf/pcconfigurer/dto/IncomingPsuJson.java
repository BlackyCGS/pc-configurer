package by.pcconf.pcconfigurer.dto;

public record IncomingPsuJson(
        Integer id,
        Float price,
        PsuInner psu
) {
  public record PsuInner(
          String name,
          String producer,
          Integer watt,
          String size,
          String efficiencyRating
  ) {}
}
