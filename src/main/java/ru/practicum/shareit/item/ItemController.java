package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

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

  private final ItemService itemService;

  @PostMapping
  public ResponseEntity<ItemDto> addNewItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Validated(Create.class) @RequestBody ItemDto item) {
    log.info("Received request POST /items for user with ID {} to add item {}", userId, item);
    ItemDto itemSaved = itemService.saveItem(userId, item);
    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(itemSaved.getId())
        .toUri();
    log.info("Item added successfully with ID {}", itemSaved.getId());
    return ResponseEntity.created(location).body(itemSaved);
  }

  @PatchMapping("/{itemId}")
  public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Validated(Update.class) @RequestBody ItemDto item,
                                            @PathVariable("itemId") @NotNull @Positive Long itemId) {
    log.info("Received request PATCH /items/{} for user,ID {} to update with data {}", itemId,
        userId, item);
    ItemDto itemUpdated = itemService.updateItem(userId, item, itemId);
    log.info("Item updated successfully: {}", itemUpdated);
    return ResponseEntity.ok(itemUpdated);
  }

  @GetMapping
  public ResponseEntity<List<ItemDto>> getAllItemFromUser(
      @RequestHeader("X-Sharer-User-Id") Long userId) {
    log.info("Received request GET /items from user with ID {}.", userId);
    List<ItemDto> items = itemService.getUserItems(userId);
    log.info("Returning {} items from user {} ", items.size(), userId);
    return ResponseEntity.ok(items);
  }

  @GetMapping("/{itemId}")
  public ResponseEntity<ItemDto> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable("itemId") @NotNull @Positive Long itemId) {
    log.info("Received request from user {} GET /items/{}.", userId, itemId);
    ItemDto item = itemService.getItemById(itemId);
    log.info("Returning item {} ", item);
    return ResponseEntity.ok(item);
  }

  @GetMapping("/search")
  public ResponseEntity<List<ItemDto>> searchItemByPartialText(
      @RequestHeader("X-Sharer-User-Id") Long userId,
      @RequestParam(name = "text") @NotNull String text) {
    log.info("Received request GET /items/search?text={} for user with ID {}.", text,
        userId);
    if (text.isBlank()) {
      log.info("Search text is blank, returning empty results for user ID {}", userId);
      return ResponseEntity.ok(List.of());
    }
    List<ItemDto> itemsFound = itemService.searchItemsByPartialText(text);
    log.info("Found {} items ", itemsFound.size());
    return ResponseEntity.ok(itemsFound);
  }

}
