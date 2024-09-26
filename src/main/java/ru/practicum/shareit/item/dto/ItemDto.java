package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

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

  @Null(groups = Create.class, message = "Id should be null for the item to be saved.")
  private Long id;

  @NotBlank(groups = Create.class, message = "Name can not be blank.")
  @Pattern(regexp = "^(.*\\S.*)$",
      groups = {Create.class, Update.class},
      message = "Name must contain at least one non-whitespace character.")
  private String name;

  @NotBlank(groups = Create.class, message = "Description can not be blank.")
  @Pattern(regexp = "^(.*\\S.*)$",
      groups = {Create.class, Update.class},
      message = "Description must contain at least one non-whitespace character.")
  private String description;

  @NotNull(groups = Create.class, message = "Available should be defined.")
  private Boolean available;

  private BookingShortDto lastBooking;

  private BookingShortDto nextBooking;

  private List<CommentDto> comments;


}
