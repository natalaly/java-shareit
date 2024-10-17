package ru.practicum.shareit.utils;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;

@UtilityClass
public class CommentBuilder {

  public CommentDto buildCommentDto() {
    return CommentDto.builder()
        .id(1L)
        .text("Comment text")
        .build();
  }

}
