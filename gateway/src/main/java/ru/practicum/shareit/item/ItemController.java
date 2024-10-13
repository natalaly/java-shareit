package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utils.HeaderConstants;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

/**
 * ItemController handles incoming HTTP requests related to items. It forwards requests to the
 * {@link ItemClient} for further processing. This controller performs validation on input data and
 * manages item operations like adding, updating, retrieving items, searching, and adding comments.
 */
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

  private static final String USER_ID_HEADER = HeaderConstants.USER_ID_HEADER;
  private final ItemClient itemClient;

  @PostMapping
  public ResponseEntity<Object> addItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                        @Validated(Create.class) @RequestBody ItemDto itemDto) {
    log.info("Received request POST /items for user with ID {} to add item {}", userId, itemDto);
    return itemClient.addItem(userId, itemDto);
  }

  @PatchMapping("/{itemId}")
  public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                           @Validated(Update.class) @RequestBody ItemDto itemDto,
                                           @PathVariable("itemId") @NotNull @Positive Long itemId) {
    log.info("Received request PATCH /items/{} for user,ID {} to update with data {}", itemId,
        userId, itemDto);
    return itemClient.updateItem(userId, itemId, itemDto);
  }

  @GetMapping
  public ResponseEntity<Object> getUserItems(@RequestHeader(USER_ID_HEADER) Long ownerId) {
    log.info("Received request GET /items from user with ID {}.", ownerId);
    return itemClient.getUserItems(ownerId);
  }

  @GetMapping("/{itemId}")
  public ResponseEntity<Object> getItemById(@RequestHeader(USER_ID_HEADER) Long userId,
                                            @PathVariable("itemId") @NotNull @Positive Long itemId) {
    log.info("Received request from user {} GET /items/{}.", userId, itemId);
    return itemClient.getItemById(userId, itemId);
  }

  @GetMapping("/search")
  public ResponseEntity<Object> searchItem(
      @RequestHeader(USER_ID_HEADER) Long userId,
      @RequestParam(name = "text") @NotNull String text) {
    log.info("Received request GET /items/search?text={} from user with ID {}.", text, userId);
    return itemClient.search(userId, text);
  }

  @PostMapping("/{itemId}/comment")
  public ResponseEntity<Object> addCommentToItem(
      @RequestHeader(USER_ID_HEADER) Long userId,
      @PathVariable("itemId") @NotNull @Positive Long itemId,
      @Validated @RequestBody CommentDto commentDto) {
    log.info("Received request POST /items/{}/comment for user ID {} to add comment {}",
        itemId, userId, commentDto);
    return itemClient.addComment(userId, itemId, commentDto);
  }

}
