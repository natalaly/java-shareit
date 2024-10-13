package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

/**
 * Data Transfer Object representing a User.
 */
@Data
@Builder
public class UserDto {

  private Long id;

  @NotBlank(groups = Create.class, message = "Name can not be blank.")
  private String name;

  @NotBlank(groups = Create.class, message = "Email can not be blank.")
  @Email(groups = {Create.class, Update.class}, message = "Email should be correct format.")
  private String email;

}
