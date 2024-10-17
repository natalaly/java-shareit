package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;

/**
 * Data Transfer Object representing a request for item.
 *
 * @see ItemRequest
 * @see ItemRequestMapper
 */

@Data
@Accessors(chain = true)
public class ItemRequestDto {

  private Long id;

  private String description;

  private Long requestorId;

  private LocalDateTime created;

  private List<ItemDto> items = new ArrayList<>();

}
