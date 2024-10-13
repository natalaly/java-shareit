package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

/**
 * Utility class for mapping between {@link Comment} entities and {@link CommentDto}. This class
 * provides static methods to convert between different representations of item data:
 */
@UtilityClass
@Slf4j
public class CommentMapper {

  public CommentDto mapToCommentDto(final Comment comment) {
    log.debug("Mapping Comment {} to CommentDto.", comment);
    Objects.requireNonNull(comment, "Comment cannot be null.");
    return CommentDto.builder()
        .id(comment.getId())
        .text(comment.getText())
        .authorName(comment.getAuthor().getName())
        .created(comment.getCreated())
        .build();
  }

  public List<CommentDto> mapToCommentDto(final Iterable<Comment> comments) {
    if (comments == null) {
      return Collections.emptyList();
    }
    final List<CommentDto> dtos = new ArrayList<>();
    comments.forEach(i -> dtos.add(mapToCommentDto(i)));
    return dtos;
  }

  public Comment mapToComment(final CommentDto commentDto, final Item item,
                              final User user, final LocalDateTime created) {
    Objects.requireNonNull(commentDto, "CommentDto cannot be null.");
    return Comment.builder()
        .text(commentDto.getText())
        .item(item)
        .author(user)
        .author(user)
        .created(created)
        .build();
  }

}
