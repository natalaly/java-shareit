package ru.practicum.shareit.booking.dto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@JsonTest
class BookingResponseDtoJsonTest {

  @Autowired
  private JacksonTester<BookingResponseDto> json;

  @SneakyThrows
  @Test
  void testBookingResponseDtoSerialization() {
    LocalDateTime start = LocalDateTime.now();
    LocalDateTime end = start.plusDays(1);
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    BookingResponseDto bookingDto = new BookingResponseDto(
        1L,
        start,
        end,
        ItemDto.builder().id(1L).build(),
        UserDto.builder().id(1L).build(),
        BookingStatus.WAITING
    );

    JsonContent<BookingResponseDto> result = json.write(bookingDto);

    assertThat(result).extractingJsonPathNumberValue("$.id")
        .isEqualTo(bookingDto.getId().intValue());
    assertThat(result).extractingJsonPathStringValue("$.start")
        .isEqualTo(bookingDto.getStart().format(formatter));
    assertThat(result).extractingJsonPathStringValue("$.end")
        .isEqualTo(bookingDto.getEnd().format(formatter));
    assertThat(result).extractingJsonPathNumberValue("$.item.id")
        .isEqualTo(bookingDto.getItem().getId().intValue());
    assertThat(result).extractingJsonPathNumberValue("$.booker.id")
        .isEqualTo(bookingDto.getBooker().getId().intValue());
    assertThat(result).extractingJsonPathStringValue("$.status")
        .isEqualTo(bookingDto.getStatus().name());
  }

}