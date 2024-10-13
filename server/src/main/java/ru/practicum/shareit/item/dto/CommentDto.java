package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

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

  private Long id;

  private String text;

  private String authorName;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
  private LocalDateTime created;

}
