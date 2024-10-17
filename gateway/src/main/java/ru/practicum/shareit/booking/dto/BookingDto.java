package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.EndAfterStart;

/**
 * Data Transfer Object representing a Booking.
 */
@Data
@Builder
@EndAfterStart(message = "Invalid date. End date should be after start date.")
public class BookingDto {

  @NotNull(message = "Invalid date. Start date can not be null.")
  @FutureOrPresent(message = "Invalid date.Start date must not be in the past.")
  private LocalDateTime start;

  @NotNull(message = "Invalid date. End date can not be null.")
  @Future(message = "Invalid date. End date must be in the future.")
  private LocalDateTime end;

  @NotNull(message = "Item ID can not be null.")
  @Positive(message = "Item ID can not be negative number.")
  private Long itemId;

}
