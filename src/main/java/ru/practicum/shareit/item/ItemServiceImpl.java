package ru.practicum.shareit.item;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.UserService;

/**
 * Service implementation class for managing item-related operations.
 *
 * @see Item
 * @see ItemDto
 * @see ItemRepository
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

  private final ItemRepository itemRepository;
  private final UserService userService;

  @Override
  public ItemDto saveItem(final Long userId, final ItemDto itemDto) {
    final User owner = UserMapper.mapToUser(userService.getUserById(userId));
    final Item itemToSave = ItemMapper.mapToItem(itemDto);
    itemToSave.setOwner(owner);
    return ItemMapper.mapToItemDto(itemRepository.save(itemToSave));
  }

  @Override
  public ItemDto updateItem(final Long userId, final ItemDto itemDto, final Long itemId) {
    validateOwner(userId);
    final Item itemToUpdate = getItemByIdAndOwnerOrThrow(itemId, userId);

    if (itemDto.getName() != null) {
      itemToUpdate.setName(itemDto.getName());
    }
    if (itemDto.getDescription() != null) {
      itemToUpdate.setDescription(itemDto.getDescription());
    }
    if (itemDto.getAvailable() != null) {
      itemToUpdate.setAvailable(itemDto.getAvailable());
    }
    return ItemMapper.mapToItemDto(itemRepository.update(itemToUpdate));
  }

  @Override
  public List<ItemDto> getUserItems(final Long userId) {
    validateOwner(userId);
    return itemRepository.findAllByUserId(userId).stream()
        .map(ItemMapper::mapToItemDto)
        .toList();
  }

  @Override
  public ItemDto getItemById(final Long itemId) {
    return ItemMapper.mapToItemDto(getItemOrThrow(itemId));
  }

  @Override
  public List<ItemDto> searchItemsByPartialText(final String text) {
    return itemRepository.findByText(text).stream()
        .map(ItemMapper::mapToItemDto)
        .toList();
  }

  private Item getItemOrThrow(final Long itemId) {
    return itemRepository.findById(itemId)
        .orElseThrow(() -> {
          log.warn("Fail to get item with itemId {} from DB.", itemId);
          return new NotFoundException(String.format("Item with ID %d not found.", itemId));
        });
  }

  private Item getItemByIdAndOwnerOrThrow(final Long itemId, final Long userId) {
    return itemRepository.findByItemIdAndOwnerId(itemId, userId)
        .orElseThrow(() -> {
          log.warn("Fail to get item with itemId {} and ownerId {} from DB.", itemId, userId);
          return new NotFoundException(
              String.format("User with ID %d is not the owner of the item ID %d.", userId,
                  itemId));
        });
  }

  private void validateOwner(final Long userId) {
    log.debug("Validating user with ID {} exisys in DB and owns items.", userId);
    userService.validateUserExist(userId);
    if (!itemRepository.isExistedOwner(userId)) {
      log.warn("Fail ownership validation,User with ID = {} does not possess any items.", userId);
      throw new NotFoundException("No items found for user with ID." + userId);
    }
    log.debug("Success in validating owner with ID {} is not null and possesses items.", userId);
  }

}
