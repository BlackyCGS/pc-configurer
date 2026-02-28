package by.pcconf.pcconfigurer.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class UserRequest {
  private String username;

  private String email;

  private String password;

}
