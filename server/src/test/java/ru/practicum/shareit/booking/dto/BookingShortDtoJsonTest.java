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

@JsonTest
class BookingShortDtoJsonTest {

  @Autowired
  private JacksonTester<BookingShortDto> json;

  @SneakyThrows
  @Test
  void testBookingShortDtoSerialization() {

    LocalDateTime start = LocalDateTime.now();
    LocalDateTime end = start.plusDays(1);
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    BookingShortDto bookingDto = new BookingShortDto(
        1L,
        start,
        end,
        2L
    );

    JsonContent<BookingShortDto> result = json.write(bookingDto);

    assertThat(result).extractingJsonPathNumberValue("$.id")
        .isEqualTo(bookingDto.getId().intValue());
    assertThat(result).extractingJsonPathStringValue("$.start")
        .isEqualTo(bookingDto.getStart().format(formatter));
    assertThat(result).extractingJsonPathStringValue("$.end")
        .isEqualTo(bookingDto.getEnd().format(formatter));
    assertThat(result).extractingJsonPathNumberValue("$.bookerId")
        .isEqualTo(bookingDto.getBookerId().intValue());
  }

  @SneakyThrows
  @Test
  void testBookingShortDtoSerializationWithNullFields() {
    LocalDateTime start = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    BookingShortDto bookingDto = new BookingShortDto(
        1L,
        null,
        null,
        2L
    );

    JsonContent<BookingShortDto> result = json.write(bookingDto);

    assertThat(result).extractingJsonPathNumberValue("$.id")
        .isEqualTo(bookingDto.getId().intValue());
    assertThat(result).extractingJsonPathNumberValue("$.bookerId")
        .isEqualTo(bookingDto.getBookerId().intValue());

    assertThat(result).doesNotHaveJsonPath("$.start");
    assertThat(result).doesNotHaveJsonPath("$.end");
  }
}