package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * Data Transfer Object representing a Booking.
 *
 * @see Booking
 * @see BookingMapper
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingResponseDto {

  private Long id;

  private LocalDateTime start;

  private LocalDateTime end;

  private ItemDto item;

  private UserDto booker;

  private BookingStatus status;

}
