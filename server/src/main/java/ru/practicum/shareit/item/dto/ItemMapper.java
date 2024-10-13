package ru.practicum.shareit.item.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

/**
 * Utility class for mapping between {@link Item} entities and {@link ItemDto} data transfer
 * objects. This class provides static methods to convert between different representations of item
 * data.
 * <ul>
 *   <li>{@link #mapToItemDto(Item)}: Maps an {@link Item} entity to an {@link ItemDto}.</li>
 *   <li>{@link #mapToItemDto(Item, BookingShortDto, BookingShortDto, List)}: Maps an {@link Item}
 *   entity to an {@link ItemDto}, including last and next bookings and a list of comments.</li>
 *   <li>{@link #mapToItemDto(Iterable)}: Converts an {@link Iterable} collection of {@link Item} entities
 *   to a list of {@link ItemDto} objects.</li>
 *   <li>{@link #mapToItem(ItemDto, User)}: Maps an {@link ItemDto} to an {@link Item} entity, using the provided
 *   {@link User} as the owner.</li>
 * </ul>
 *
 * @see Item
 * @see ItemDto
 * @see BookingShortDto
 * @see CommentDto
 * @see User
 */
@UtilityClass
@Slf4j
public class ItemMapper {

  public ItemDto mapToItemDto(final Item item) {
    log.debug("Mapping Item {} to ItemDto.", item);
    Objects.requireNonNull(item, "Item cannot be null.");
    return ItemDto.builder()
        .id(item.getId())
        .name(item.getName())
        .description(item.getDescription())
        .available(item.isAvailable())
        .requestId(item.getRequest() == null ? null : item.getRequest().getId())
        .build();
  }

  public ItemDto mapToItemDto(final Item item,
                              final BookingShortDto lastBooking,
                              final BookingShortDto nextBooking,
                              final List<CommentDto> comments) {
    Objects.requireNonNull(item, "ItemDto cannot be null.");
    return mapToItemDto(item)
        .setLastBooking(lastBooking)
        .setNextBooking(nextBooking)
        .setComments(comments);
  }

  public List<ItemDto> mapToItemDto(final Iterable<Item> items) {
    if (items == null) {
      return Collections.emptyList();
    }
    final List<ItemDto> dtos = new ArrayList<>();
    items.forEach(i -> dtos.add(mapToItemDto(i)));
    return dtos;
  }

  public Item mapToItem(final ItemDto itemDto, final User owner) {
    log.debug("Mapping ItemDto {} to Item.", itemDto);
    Objects.requireNonNull(itemDto, "ItemDto cannot be null.");
    return Item.builder()
        .id(itemDto.getId())
        .name(itemDto.getName())
        .description(itemDto.getDescription())
        .owner(owner)
        .available(itemDto.getAvailable())
        .build();
  }
}
