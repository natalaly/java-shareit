package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

/**
 * Data Transfer Object representing a Booking.
 *
 * @see Booking
 * @see BookingMapper
 */
@Data
@Builder
public class BookingDto {

  private Long id;

  private LocalDateTime start;

  private LocalDateTime end;

  private Long itemId;

  private Long bookerId;

  private BookingStatus status;

}
