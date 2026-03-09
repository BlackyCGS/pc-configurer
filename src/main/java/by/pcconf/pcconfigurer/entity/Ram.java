package by.pcconf.pcconfigurer.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
public class Ram {

  private Integer id;

  private Float price;

  private String producer;

  private String ramType;

  private int size;

  private String timings;
}
