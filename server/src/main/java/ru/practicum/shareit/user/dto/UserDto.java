package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.user.model.User;

/**
 * Data Transfer Object representing a User.
 *
 * @see User
 * @see UserMapper
 */
@Data
@Builder
@Accessors(chain = true)
public class UserDto {

  private Long id;

  private String name;

  private String email;

}
