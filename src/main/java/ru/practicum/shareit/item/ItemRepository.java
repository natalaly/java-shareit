package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

/**
 * A repository interface for managing item persistence and retrieval operations.
 * <p>
 * This interface defines methods for common CRUD operations on item data, as well as additional
 * methods for specific queries like finding items by user ID, searching by text, and checking the
 * existence of items owned by a user.
 * <p>
 * Methods include:
 * <ul>
 *   <li>{@link #save(Item)}: Saves a new item to the storage.</li>
 *   <li>{@link #update(Item)}: Updates an existing item in the storage.</li>
 *   <li>{@link #findById(Long)}: Retrieves an item by its ID.</li>
 *   <li>{@link #findByItemIdAndOwnerId(Long, Long)}: Retrieves an item by its ID and owner ID.</li>
 *   <li>{@link #findAllByUserId(Long)}: Retrieves all items owned by a specific user.</li>
 *   <li>{@link #findByText(String)}: Searches for items containing the specified text in their name or description.</li>
 *   <li>{@link #deleteByOwnerId(Long)}: Deletes all items owned by a specific user.</li>
 *   <li>{@link #isExistedOwner(Long)}: Checks if there are any items owned by a specific user.</li>
 * </ul>
 *
 * @see Item
 * @see ItemRepositoryInMemory
 * @see UserService
 */
public interface ItemRepository {

  Item save(Item item);

  Item update(Item item);

  Optional<Item> findById(Long itemId);

  Optional<Item> findByItemIdAndOwnerId(Long itemId, Long ownerId);

  List<Item> findAllByUserId(Long ownerId);

  List<Item> findByText(String text);

  void deleteByOwnerId(Long ownerId);

  boolean isExistedOwner(Long userId);
}
