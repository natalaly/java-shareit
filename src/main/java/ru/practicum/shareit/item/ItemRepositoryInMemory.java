package ru.practicum.shareit.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.utils.CopyUtil;

/**
 * An in-memory implementation of the {@link ItemRepository} interface.
 * <p>
 * This repository provides a basic, non-persistent storage mechanism for item entities, using a
 * {@link HashMap} to store items: each user's items are stored as a list within the map, keyed by
 * the user's ID.
 *
 * @see Item
 */
@Repository
public class ItemRepositoryInMemory implements ItemRepository {

  private final Map<Long, List<Item>> items = new HashMap<>();
  private Long lastUsedId = 0L;

  private Long generateId() {
    return ++lastUsedId;
  }

  @Override
  public Item save(final Item item) {
    final Long id = generateId();
    item.setId(id);
    items.computeIfAbsent(item.getOwner().getId(), k -> new ArrayList<>()).add(item);
    return copy(item);
  }

  @Override
  public Item update(final Item item) {
    List<Item> userItems = items.get(item.getOwner().getId());
    if (userItems != null) {
      userItems = userItems.stream()
          .map((currentItem -> currentItem.getId().equals(item.getId()) ? item : currentItem))
          .toList();
      items.put(item.getOwner().getId(), userItems);
      return copy(item);
    } else {
      throw new NotFoundException("Item or owner not found.");
    }
  }

  @Override
  public Optional<Item> findById(final Long itemId) {
    return items.values().stream()
        .flatMap(Collection::stream)
        .filter(i -> i.getId().equals(itemId))
        .map(this::copy)
        .findFirst();
  }

  @Override
  public Optional<Item> findByItemIdAndOwnerId(final Long itemId, final Long ownerId) {
    return items.get(ownerId).stream()
        .filter(i -> i.getId().equals(itemId))
        .map(this::copy)
        .findFirst();
  }

  @Override
  public List<Item> findAllByUserId(final Long ownerId) {
    return items.getOrDefault(ownerId, Collections.emptyList()).stream()
        .map(this::copy)
        .toList();
  }

  @Override
  public List<Item> findByText(final String text) {
    if (text == null || text.isBlank()) {
      return Collections.emptyList();
    }
    return items.values().stream()
        .flatMap(Collection::stream)
        .filter(i -> i.isAvailable() &&
            (i.getName().toLowerCase().contains(text.toLowerCase()) ||
                i.getDescription().toLowerCase().contains(text.toLowerCase())))
        .map(this::copy)
        .collect(Collectors.toList());

  }

  @Override
  public void deleteByOwnerId(final Long ownerId) {
    items.remove(ownerId);
  }

  @Override
  public boolean isExistedOwner(final Long userId) {
    return items.containsKey(userId);
  }

  private Item copy(final Item item) {
    return CopyUtil.copy(item, Item.class);
  }
}
