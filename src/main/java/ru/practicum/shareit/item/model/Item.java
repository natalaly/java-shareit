package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * Represents an item in the application.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

  private Long id;
  private String name;
  private String description;
  private boolean available;
  private User owner;
  private ItemRequest request;

}
