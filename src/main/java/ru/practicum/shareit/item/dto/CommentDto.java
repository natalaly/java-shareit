package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.validation.Create;

/**
 * Data Transfer Object representing a Comment.
 *
 * @see Comment
 * @see Item
 * @see ItemMapper
 */
@Data
@Builder
public class CommentDto {

  @Null(groups = Create.class, message = "Id should be null for the comment to be added.")
  private Long id;

  @NotBlank(groups = Create.class, message = "Text should not be blank.")
  @Pattern(regexp = "^(.*\\S.*)$",
      groups = {Create.class},
      message = "Text must contain at least one non-whitespace character.")
  @Max(2000)
  private String text;

  private String authorName;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
  private LocalDateTime created;

}
