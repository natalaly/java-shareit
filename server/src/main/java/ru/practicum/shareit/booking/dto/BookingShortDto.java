package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemDto;

/**
 * Data Transfer Object representing a Booking.
 *
 * @see Booking
 * @see BookingMapper
 * @see ItemDto
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingShortDto {

  private Long id;

  private LocalDateTime start;

  private LocalDateTime end;

  private Long bookerId;


}
