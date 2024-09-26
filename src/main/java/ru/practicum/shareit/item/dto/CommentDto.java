package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

/**
 * Data Transfer Object representing a Comment.
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
