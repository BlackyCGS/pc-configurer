package by.pcconf.pcconfigurer.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
public class Psu {

  private Integer id;

  private Float price;

  private String producer;

  private int watt;

  private String size;

  private String efficiencyRating;
}
