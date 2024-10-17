package ru.practicum.shareit.item;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utils.HeaderConstants;

/**
 * REST controller for managing items. Provides endpoints for adding, updating, retrieving, and
 * searching items.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

  private static final String USER_ID_HEADER = HeaderConstants.USER_ID_HEADER;
  private final ItemService itemService;

  @PostMapping
  public ResponseEntity<ItemDto> addNewItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                            @RequestBody ItemDto item) {
    log.info("Received request POST /items for user with ID {} to add item {}", userId, item);
    final ItemDto itemSaved = itemService.saveItem(userId, item);
    final URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(itemSaved.getId())
        .toUri();
    log.info("Item added successfully with ID {}", itemSaved.getId());
    return ResponseEntity.created(location).body(itemSaved);
  }

  @PatchMapping("/{itemId}")
  public ResponseEntity<ItemDto> updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                            @RequestBody ItemDto item,
                                            @PathVariable("itemId") Long itemId) {
    log.info("Received request PATCH /items/{} for user,ID {} to update with data {}", itemId,
        userId, item);
    final ItemDto itemUpdated = itemService.updateItem(userId, item, itemId);
    log.info("Item updated successfully: {}", itemUpdated);
    return ResponseEntity.ok(itemUpdated);
  }

  @GetMapping
  public ResponseEntity<List<ItemDto>> getAllItemFromUser(
      @RequestHeader(USER_ID_HEADER) Long userId) {
    log.info("Received request GET /items from user with ID {}.", userId);
    final List<ItemDto> items = itemService.getUserItems(userId);
    log.info("Returning {} items from user {} ", items.size(), userId);
    return ResponseEntity.ok(items);
  }

  @GetMapping("/{itemId}")
  public ResponseEntity<ItemDto> getItemById(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @PathVariable("itemId") Long itemId) {
    log.info("Received request from user {} GET /items/{}.", userId, itemId);
    final ItemDto item = itemService.getItemById(itemId, userId);
    log.info("Returning item {} ", item);
    return ResponseEntity.ok(item);
  }

  @GetMapping("/search")
  public ResponseEntity<List<ItemDto>> searchItemByPartialText(
      @RequestHeader(USER_ID_HEADER) Long userId,
      @RequestParam(name = "text") String text) {
    log.info("Received request GET /items/search?text={} for user with ID {}.", text, userId);
    final List<ItemDto> itemsFound = itemService.searchItemsByPartialText(text);
    log.info("Found {} items ", itemsFound.size());
    return ResponseEntity.ok(itemsFound);
  }

  @PostMapping("/{itemId}/comment")
  public ResponseEntity<CommentDto> addCommentToItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                                     @PathVariable("itemId") Long itemId,
                                                     @RequestBody CommentDto comment) {
    log.info("Received request POST /items/{}/comment for user ID {} to add comment {}",
        itemId, userId, comment);
    final CommentDto commentAdded = itemService.addComment(userId, itemId, comment);
    final URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(commentAdded.getId())
        .toUri();
    log.info("Comment added successfully with ID {}", commentAdded.getId());
    return ResponseEntity.ok().location(location).body(commentAdded);
  }

}
