package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Data Transfer Object representing a request for item.
 */

@Data
@Accessors(chain = true)
public class ItemRequestDto {

  @Null(message = "Id should be null for the item to be saved.")
  private Long id;

  @NotBlank(message = "Description can not be blank.")
  @Pattern(regexp = "^(.*\\S.*)$",
      message = "Description must contain at least one non-whitespace character.")
  private String description;

}
