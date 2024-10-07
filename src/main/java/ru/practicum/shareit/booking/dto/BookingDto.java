package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.EndAfterStart;

/**
 * Data Transfer Object representing a Booking.
 *
 * @see Booking
 * @see BookingMapper
 */
@Data
@Builder
@EndAfterStart(groups = Create.class)
public class BookingDto {

  @Null(groups = Create.class, message = "Id should be null for the booking to be created.")
  private Long id;

  @NotNull(groups = Create.class, message = "Invalid date. Start date can not be null.")
  @FutureOrPresent(groups = Create.class, message = "Invalid date.Start date must not be in the past.")
  private LocalDateTime start;

  @NotNull(groups = Create.class, message = "Invalid date. End date can not be null.")
  @Future(groups = Create.class, message = "Invalid date. End date must be in the future.")
  private LocalDateTime end;

  @NotNull(groups = Create.class, message = "Item ID can not be null.")
  @Positive(groups = Create.class, message = "Item ID can not be negative number.")
  private Long itemId;

  private Long bookerId;

  private BookingStatus status;

}
