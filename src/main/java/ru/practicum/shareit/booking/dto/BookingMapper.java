package ru.practicum.shareit.booking.dto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;

/**
 * Utility class for mapping between {@link Booking} entities and various DTO representations. This
 * class provides static methods to convert between different representations of booking data:
 * <ul>
 *   <li>{@link #mapToBooking(BookingDto)}: Maps a {@link BookingDto} to a {@link Booking} entity.</li>
 *   <li>{@link #mapToResponseDto(Booking)}: Maps a {@link Booking} entity to a {@link BookingResponseDto}.</li>
 *   <li>{@link #mapToResponseDto(List)}: Converts a {@link List} of {@link Booking} entities to a list of {@link BookingResponseDto}.</li>
 *   <li>{@link #mapToShortDto(Booking)}: Maps a {@link Booking} entity to a {@link BookingShortDto} for concise responses.</li>
 * </ul>
 */
@UtilityClass
public class BookingMapper {

  public Booking mapToBooking(final BookingDto bookingDto) {
    Objects.requireNonNull(bookingDto);
    return Booking.builder()
        .id(bookingDto.getId())
        .start(bookingDto.getStart())
        .end(bookingDto.getEnd())
        .build();
  }

  public BookingResponseDto mapToResponseDto(final Booking booking) {
    Objects.requireNonNull(booking);
    return BookingResponseDto.builder()
        .id(booking.getId())
        .start(booking.getStart())
        .end(booking.getEnd())
        .status(booking.getStatus())
        .booker(UserMapper.mapToUserDto(booking.getBooker()))
        .item(ItemMapper.mapToItemDto(booking.getItem()))
        .build();
  }

  public List<BookingResponseDto> mapToResponseDto(final List<Booking> bookings) {
    if (bookings == null) {
      return Collections.emptyList();
    }
    return bookings.stream()
        .map(BookingMapper::mapToResponseDto)
        .toList();
  }

  public BookingShortDto mapToShortDto(final Booking booking) {
    if (booking == null) {
      return null;
    }
    return BookingShortDto.builder()
        .id(booking.getId())
        .start(booking.getStart())
        .end(booking.getEnd())
        .bookerId(booking.getBooker().getId())
        .build();
  }
}
