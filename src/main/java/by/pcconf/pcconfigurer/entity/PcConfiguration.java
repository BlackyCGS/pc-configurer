package by.pcconf.pcconfigurer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pc_configuration")
@Data
@NoArgsConstructor
public class PcConfiguration {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;
  Integer cpuId;
  Integer gpuId;
  Integer motherboardId;
  Integer psuId;
  Integer pcCaseId;
  Integer ramId;
  @Min(value = 0, message = "Ram amount needs to be greater than 0")
  Integer ramAmount;

  @JsonIgnore
  @SuppressWarnings("java:S7027")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

}
