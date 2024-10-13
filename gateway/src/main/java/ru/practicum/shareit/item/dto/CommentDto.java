package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object representing a Comment.
 */
@Data
@Builder
public class CommentDto {

  @Null(message = "Id should be null for the comment to be added.")
  private Long id;

  @NotBlank(message = "Text should not be blank.")
  @Pattern(regexp = "^(.*\\S.*)$", message = "Text must contain at least one non-whitespace character.")
  @Size(min = 1, max = 2000, message = "Text must be less than or equal to 2000.")
  private String text;

}
