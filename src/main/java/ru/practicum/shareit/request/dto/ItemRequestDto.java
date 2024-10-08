package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

/**
 * Data Transfer Object representing a request for item.
 *
 * @see ItemRequest
 * @see ItemRequestMapper
 */

@Data
@Accessors(chain = true)
public class ItemRequestDto {

  @Null(groups = Create.class, message = "Id should be null for the item to be saved.")
  private Long id;

  @NotBlank(groups = Create.class, message = "Description can not be blank.")
  @Pattern(regexp = "^(.*\\S.*)$",
      groups = {Create.class, Update.class},
      message = "Description must contain at least one non-whitespace character.")
  private String description;

  private Long requestorId;

  private LocalDateTime created;

  private List<ItemDto> items = new ArrayList<>();

}
