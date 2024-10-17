package ru.practicum.shareit.request.dto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.utils.RequestBuilder;

@JsonTest
class ItemRequestDtoJsonTest {

  @Autowired
  private JacksonTester<ItemRequestDto> json;

  @SneakyThrows
  @Test
  void testItemRequestDtoSerialization() {
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    LocalDateTime now = LocalDateTime.now();

    ItemRequestDto requestDto = RequestBuilder.buildRequestDto(now);

    JsonContent<ItemRequestDto> result = json.write(requestDto);

    assertThat(result).extractingJsonPathNumberValue("$.id")
        .isEqualTo(requestDto.getId().intValue());
    assertThat(result).extractingJsonPathStringValue("$.description")
        .isEqualTo(requestDto.getDescription());
    assertThat(result).extractingJsonPathNumberValue("$.requestorId")
        .isEqualTo(requestDto.getRequestorId().intValue());
    assertThat(result).extractingJsonPathStringValue("$.created")
        .isEqualTo(requestDto.getCreated().format(formatter));
    assertThat(result).extractingJsonPathArrayValue("$.items").isEmpty();
  }

}