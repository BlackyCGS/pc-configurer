package by.pcconf.pcconfigurer.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
public class Motherboard {

  private Integer id;

  private Float price;

  private String producer;

  private String socket;

  private String chipset;

  private String formFactor;

  private String memoryType;

  private int sata;

  private int ramSlots;

  private int vga;

  private int dvi;

  private int displayPort;

  private int hdmi;
}
