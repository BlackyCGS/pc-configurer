package by.pcconf.pcconfigurer.dto;

public record IncomingPcCaseJson(
        Integer id,
        String name,
        Float price,
        PcCaseInner pcCase
) {
  public record PcCaseInner(
          String producer,
          String motherboard,
          String powerSupply
  ) {}
}
