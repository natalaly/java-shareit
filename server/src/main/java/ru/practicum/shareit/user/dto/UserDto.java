package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

/**
 * Data Transfer Object representing a User.
 *
 * @see User
 * @see UserMapper
 */
@Data
@Builder
public class UserDto {

  private Long id;

  private String name;

  private String email;

}
