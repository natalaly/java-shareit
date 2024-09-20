package ru.practicum.shareit.item;

import java.util.List;
import ru.practicum.shareit.item.dto.ItemDto;

/**
 * A service interface for managing item-related operations and interactions.
 * <p>
 * This interface provides methods for various operations related to items, such as creating,
 * updating, retrieving, and searching for items.
 * <p>
 * Methods include:
 * <ul>
 *   <li>{@link #saveItem(Long, ItemDto)}: Adds a new item to the system for a specific user.</li>
 *   <li>{@link #updateItem(Long, ItemDto, Long)}: Updates an existing item identified by its ID, ensuring that only the owner can modify it.</li>
 *   <li>{@link #getUserItems(Long)}: Retrieves a list of all items owned by a specific user.</li>
 *   <li>{@link #getItemById(Long)}: Retrieves details of a specific item by its ID.</li>
 *   <li>{@link #searchItemsByPartialText(String)}: Searches for items based on a partial text match in the name or description, returning only available items.</li>
 * </ul>
 *
 * @see ItemDto
 * @see ItemServiceImpl
 */
public interface ItemService {

  ItemDto saveItem(Long userId, ItemDto itemDto);

  ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId);

  List<ItemDto> getUserItems(Long userId);

  ItemDto getItemById(Long itemId);

  List<ItemDto> searchItemsByPartialText(String text);

}
