package by.pcconf.pcconfigurer.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {

  private Integer id;

  private String username;

  private String email;

  private String permission;
}
