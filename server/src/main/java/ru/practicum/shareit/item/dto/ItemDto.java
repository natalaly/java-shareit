package ru.practicum.shareit.item.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.model.Item;

/**
 * Data Transfer Object representing an Item.
 *
 * @see Item
 * @see ItemMapper
 */
@Data
@Builder
@Accessors(chain = true)
public class ItemDto {

  private Long id;

  private String name;


  private String description;

  private Boolean available;

  private BookingShortDto lastBooking;

  private BookingShortDto nextBooking;

  private List<CommentDto> comments;

  private Long requestId;

}
