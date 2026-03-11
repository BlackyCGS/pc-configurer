package by.pcconf.pcconfigurer.mapper;

import java.util.Map;

public interface RecordMapper {
 <T extends Record> Map<String, Object> toMap(T recordToMap);
}
