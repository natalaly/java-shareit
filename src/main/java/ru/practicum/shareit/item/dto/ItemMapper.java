package ru.practicum.shareit.item.dto;

import java.util.Objects;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;

/**
 * Utility class for mapping between {@link Item} entities and {@link ItemDto}. This class provides
 * static methods to convert between different representations of item data:
 * <ul>
 *   <li> {@link #mapToItem(ItemDto)}: maps a {@link ItemDto} to a {@link Item} entity.</li>
 *   <li>{@link #mapToItemDto(Item)}: maps a {@link Item} to {@link ItemDto}.</li>
 * </ul>
 */
@UtilityClass
public class ItemMapper {

  public ItemDto mapToItemDto(final Item item) {
    Objects.requireNonNull(item, "Item cannot be null.");
    return ItemDto.builder()
        .id(item.getId())
        .name(item.getName())
        .description(item.getDescription())
        .available(item.isAvailable())
        .owner(item.getOwner())
        .request(item.getRequest())
        .build();
  }

  public Item mapToItem(final ItemDto itemDto) {
    Objects.requireNonNull(itemDto, "ItemDto cannot be null.");
    return Item.builder()
        .id(itemDto.getId())
        .name(itemDto.getName())
        .description(itemDto.getDescription())
        .available(itemDto.getAvailable())
        .owner(itemDto.getOwner())
        .request(itemDto.getRequest())
        .build();
  }

}
