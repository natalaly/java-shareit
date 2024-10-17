package ru.practicum.shareit.item.dto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingShortDto;

@JsonTest
class ItemDtoJsonTest {

  @Autowired
  private JacksonTester<ItemDto> json;

  @SneakyThrows
  @Test
  void testItemDtoSerialization() {

    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    LocalDateTime now = LocalDateTime.now();
    BookingShortDto lastBooking = BookingShortDto.builder()
        .id(1L)
        .start(now.minusDays(2))
        .end(now.minusDays(1))
        .bookerId(1L)
        .build();
    List<CommentDto> comments = List.of(
        CommentDto.builder()
            .id(1L)
            .text("Text")
            .created(now.minusHours(23))
            .authorName("Name")
            .build()
    );
    ItemDto itemDto = ItemDto.builder()
        .id(1L)
        .name("item")
        .description("description")
        .available(true)
        .lastBooking(lastBooking)
        .comments(comments)
        .requestId(1L)
        .build();

    JsonContent<ItemDto> result = json.write(itemDto);

    assertThat(result).extractingJsonPathNumberValue("$.id")
        .isEqualTo(itemDto.getId().intValue());
    assertThat(result).extractingJsonPathStringValue("$.name")
        .isEqualTo(itemDto.getName());
    assertThat(result).extractingJsonPathStringValue("$.description")
        .isEqualTo(itemDto.getDescription());
    assertThat(result).extractingJsonPathBooleanValue("$.available")
        .isTrue();
    assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id")
        .isEqualTo(itemDto.getLastBooking().getId().intValue());
    assertThat(result).extractingJsonPathStringValue("$.lastBooking.start")
        .isEqualTo(itemDto.getLastBooking().getStart().format(formatter));
    assertThat(result).extractingJsonPathStringValue("$.lastBooking.end")
        .isEqualTo(itemDto.getLastBooking().getEnd().format(formatter));
    assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId")
        .isEqualTo(itemDto.getLastBooking().getBookerId().intValue());
    assertThat(result).hasEmptyJsonPathValue("$.nextBooking");
    assertThat(result).extractingJsonPathNumberValue("$.requestId")
        .isEqualTo(itemDto.getRequestId().intValue());
    assertThat(result).extractingJsonPathArrayValue("$.comments")
        .hasSize(1)
        .element(0)
        .hasFieldOrPropertyWithValue("id", itemDto.getComments().getFirst().getId().intValue())
        .hasFieldOrPropertyWithValue("text", itemDto.getComments().getFirst().getText())
        .hasFieldOrPropertyWithValue("authorName", itemDto.getComments().getFirst().getAuthorName())
        .hasFieldOrPropertyWithValue("created",
            itemDto.getComments().getFirst().getCreated()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")));
  }

}