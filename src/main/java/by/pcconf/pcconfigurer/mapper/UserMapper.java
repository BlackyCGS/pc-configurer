package by.pcconf.pcconfigurer.mapper;


import by.pcconf.pcconfigurer.dto.UserDto;
import by.pcconf.pcconfigurer.dto.UserRequest;
import by.pcconf.pcconfigurer.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  User toEntity(UserRequest request);

  UserDto toDto(User user);
}
