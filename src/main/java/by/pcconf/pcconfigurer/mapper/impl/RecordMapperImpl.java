package by.pcconf.pcconfigurer.mapper.impl;

import by.pcconf.pcconfigurer.exception.InternalServerErrorCustomException;
import by.pcconf.pcconfigurer.mapper.RecordMapper;
import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class RecordMapperImpl implements RecordMapper {
  @Override
  public <T extends Record> Map<String, Object> toMap(T recordToMap) {
      Map<String, Object> map = new HashMap<>();
      for (RecordComponent component : recordToMap.getClass().getRecordComponents()) {
        try {
          Object value = component.getAccessor().invoke(recordToMap);
          if (value != null) {
            map.put(component.getName(), value);
          }
        } catch (IllegalAccessException | InvocationTargetException ex) {
          throw new InternalServerErrorCustomException("Internal server error caught: " + ex.getMessage());
        }
      }
      return map;
  }
}
