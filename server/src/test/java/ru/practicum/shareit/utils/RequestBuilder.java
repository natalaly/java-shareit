package ru.practicum.shareit.utils;

import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class RequestBuilder {

  public ItemRequestDto buildRequestDto(LocalDateTime created) {
    return buildRequestDto(1L, "Description", 2L, created);
  }

  public ItemRequest buildItemRequest(String description, User requester, LocalDateTime created) {
    return new ItemRequest()
        .setDescription(description)
        .setRequestor(requester)
        .setCreated(created);
  }

  public ItemRequest buildItemRequest(User requestor) {
    return new ItemRequest()
        .setRequestor(requestor)
        .setCreated(LocalDateTime.now());
  }

  public ItemRequestDto buildRequestDto(
      Long id, String description, Long requesterId, LocalDateTime created) {
    return new ItemRequestDto()
        .setId(id)
        .setDescription(description)
        .setRequestorId(requesterId)
        .setCreated(created);
  }

}
