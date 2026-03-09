package by.pcconf.pcconfigurer.dto;


import com.fasterxml.jackson.annotation.JsonCreator;

public record IncomingCpuJson(
        Integer id,
        Float price,
        CpuInner cpu
) {
  public record CpuInner(
          String name,
          String producer,
          Integer cores,
          Integer threads,
          Integer tdp,
          String socket
  ) {}
}
