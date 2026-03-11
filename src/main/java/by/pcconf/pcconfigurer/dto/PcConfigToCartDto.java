package by.pcconf.pcconfigurer.dto;

import by.pcconf.pcconfigurer.entity.PcConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PcConfigToCartDto {
  String email;
  PcConfiguration pcConfiguration;
}
