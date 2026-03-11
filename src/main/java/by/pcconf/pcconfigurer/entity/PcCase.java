package by.pcconf.pcconfigurer.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
public class PcCase {

  private Integer id;

  private Float price;

  private String producer;

  private String motherboard;

  private String powerSupply;
}
