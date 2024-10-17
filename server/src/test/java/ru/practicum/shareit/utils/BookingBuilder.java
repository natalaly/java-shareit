package ru.practicum.shareit.utils;

import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingBuilder {

  public static BookingDto buildBookingDto(LocalDateTime timePoint) {
    return BookingDto.builder()
        .itemId(1L)
        .start(timePoint.plusDays(1))
        .end(timePoint.plusDays(2))
        .build();
  }

  public static BookingResponseDto buildBookingResponseDto(LocalDateTime timePoint) {
    return BookingResponseDto.builder()
        .id(1L)
        .item(ItemDto.builder().id(1L).build())
        .start(timePoint.plusDays(1))
        .end(timePoint.plusDays(2))
        .status(BookingStatus.WAITING)
        .build();
  }

  public static Booking buildBooking(
      Item item, User booker, LocalDateTime start, LocalDateTime end, BookingStatus status) {
    return Booking.builder()
        .item(item)
        .booker(booker)
        .start(start)
        .end(end)
        .status(status)
        .build();
  }

  public static Booking buildBooking(LocalDateTime timePoint, Item item, User user) {
    item.setId(1L);
    return buildBooking(item, user,
        timePoint.plusDays(1), timePoint.plusDays(2),BookingStatus.WAITING);
  }
}
