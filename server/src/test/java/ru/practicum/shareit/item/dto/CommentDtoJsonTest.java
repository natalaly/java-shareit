package ru.practicum.shareit.item.dto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

@JsonTest
class CommentDtoJsonTest {

  @Autowired
  private JacksonTester<CommentDto> json;

  @SneakyThrows
  @Test
  void testCommentDtoSerialization() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    LocalDateTime now = LocalDateTime.now();
    CommentDto commentDto = CommentDto.builder()
        .id(1L)
        .text("Text")
        .created(now.minusHours(23))
        .authorName("Name")
        .build();

    JsonContent<CommentDto> result = json.write(commentDto);

    assertThat(result).extractingJsonPathNumberValue("$.id")
        .isEqualTo(commentDto.getId().intValue());
    assertThat(result).extractingJsonPathStringValue("$.text")
        .isEqualTo(commentDto.getText());
    assertThat(result).extractingJsonPathStringValue("$.authorName")
        .isEqualTo(commentDto.getAuthorName());
    assertThat(result).extractingJsonPathStringValue("$.created")
        .isEqualTo(commentDto.getCreated().format(formatter));
  }

}