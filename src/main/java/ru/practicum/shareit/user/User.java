package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

/**
 * Represents a user in the ShareIt app.
 */
@Data
@Builder
public class User {

  private Long id;
  private String name;
  private String email;

}
