package by.pcconf.pcconfigurer.entity;

import jakarta.persistence.*;
import lombok.Data;


@Data
public class Gpu {

  private Integer id;

  private Float price;

  private String producer;

  private int boostClock = 1000;

  private int vram = 1000;

  private int tdp = 100;

  private int hdmi = 0;

  private int displayPort = 0;

  private int dvi = 0;

  private int vga = 0;

}
