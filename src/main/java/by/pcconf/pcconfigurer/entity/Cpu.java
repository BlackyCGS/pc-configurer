package by.pcconf.pcconfigurer.entity;

import jakarta.persistence.*;
import lombok.Data;


@Data
public class Cpu {


  private Integer id;

  private Float price;

  private String name;

  private String producer;

  private int cores;

  private int threads;

  private int tdp;

  private String socket;
}
