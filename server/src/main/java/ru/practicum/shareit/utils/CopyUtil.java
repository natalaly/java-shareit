package ru.practicum.shareit.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CopyUtil {

  private final ObjectMapper objectMapper = new ObjectMapper();

  public <T> T copy(T entity, Class<T> entityClass) {
    try {
      return objectMapper
          .readValue(objectMapper.writeValueAsString(entity), entityClass);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
}
