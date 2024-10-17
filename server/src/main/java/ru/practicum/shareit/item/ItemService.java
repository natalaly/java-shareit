package ru.practicum.shareit.item;

import java.util.List;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

/**
 * A service interface for managing item-related operations and interactions.
 * <p>
 * This interface provides methods for creating, updating, retrieving, and searching for items. It
 * handles various business logic and validation related to items, ensuring proper ownership and
 * availability checks.
 * <p>
 * The following methods are available:
 * <ul>
 *   <li>{@link #saveItem(Long, ItemDto)}: Adds a new {@link Item} to the system for a specific user.</li>
 *   <li>{@link #updateItem(Long, ItemDto, Long)}: Updates an existing {@link Item} identified by its ID, ensuring that only the owner can modify it.</li>
 *   <li>{@link #getItemById(Long, Long)}: Retrieves detailed information about a specific {@link Item} by its ID, ensuring the owner or viewer can access it.</li>
 *   <li>{@link #getItemOrThrow(Long)}: Retrieves an {@link Item} by its ID or throws an exception if it does not exist.</li>
 *   <li>{@link #getUserItems(Long)}: Retrieves a list of all {@link ItemDto} entities owned by a specific user.</li>
 *   <li>{@link #searchItemsByPartialText(String)}: Searches for {@link ItemDto} entities by a partial text match in the name or description, returning only available items.</li>
 *   <li>{@link #addComment(Long, Long, CommentDto)}: Allows a user to add a {@link Comment} to a specific item.</li>
 *   <li>{@link #getItemByRequestIds(List)}: Retrieves a list of {@link ItemDto} entities associated with the provided request IDs.</li>
 * </ul>
 *
 * @see ItemDto
 * @see ItemServiceImpl
 */
public interface ItemService {

  ItemDto saveItem(Long userId, ItemDto itemDto);

  ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId);

  ItemDto getItemById(Long itemId, Long userId);

  Item getItemOrThrow(Long itemId);

  List<ItemDto> getUserItems(Long userId);

  List<ItemDto> searchItemsByPartialText(String text);

  CommentDto addComment(Long userId, Long itemId, CommentDto comment);

  List<ItemDto> getItemByRequestIds(List<Long> requestIds);
}
