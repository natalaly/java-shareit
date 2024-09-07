package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

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

  @NotBlank(groups = Create.class)
  private String name;

  @NotBlank(groups = Create.class)
  @Email(message = "Email should be correct format.", groups = {Create.class, Update.class})
  private String email;

}
