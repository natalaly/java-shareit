package ru.practicum.shareit.item;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

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
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

  private final ItemRepository itemRepository;
  private final UserService userService;
  private final BookingRepository bookingRepository;
  private final CommentRepository commentRepository;

  @Override
  @Transactional
  public ItemDto saveItem(final Long userId, final ItemDto itemDto) {
    log.debug("Persisting a new item with data: {} for user with ID {}.", itemDto, userId);
    final User owner = UserMapper.mapToUser(userService.getUserById(userId));

    final Item itemToSave = ItemMapper.mapToItem(itemDto, owner);

    return ItemMapper.mapToItemDto(itemRepository.save(itemToSave));
  }

  @Override
  @Transactional
  public ItemDto updateItem(final Long userId, final ItemDto itemDto, final Long itemId) {
    log.debug("Updating item with ID = {}.", itemId);
    validateOwner(userId);

    final Item itemToUpdate = getItemByIdAndOwnerOrThrow(itemId, userId);
    Optional.ofNullable(itemDto.getName()).ifPresent(itemToUpdate::setName);
    Optional.ofNullable(itemDto.getDescription()).ifPresent(itemToUpdate::setDescription);
    Optional.ofNullable(itemDto.getAvailable()).ifPresent(itemToUpdate::setAvailable);

    return ItemMapper.mapToItemDto(itemRepository.save(itemToUpdate));
  }

  @Override
  public ItemDto getItemById(final Long itemId, final Long userId) {
    log.debug("Retrieving item with ID = {}.", itemId);
    validateUser(userId);

    final Item item = getItemOrThrow(itemId);
    LocalDateTime now = LocalDateTime.now();

    final List<Booking> bookingsForItem = bookingRepository.findByItemIdAndItemOwnerId(itemId,
        userId);
    final BookingShortDto lastBooking = getLastBooking(bookingsForItem, now);
    final BookingShortDto nextBooking = getNextBooking(bookingsForItem, now);

    final List<CommentDto> comments =
        CommentMapper.mapToCommentDto(commentRepository.findAllByItemId(itemId));

    return ItemMapper.mapToItemDto(item, lastBooking, nextBooking, comments);
  }

  @Override
  public Item getItemOrThrow(final Long itemId) {
    return itemRepository.findById(itemId)
        .orElseThrow(() -> {
          log.warn("Fail to get item with itemId {} from DB.", itemId);
          return new NotFoundException("Item not found.");
        });
  }

  @Override
  public List<ItemDto> getUserItems(final Long userId) {
    log.debug("Retrieving items owned by user with ID = {}.", userId);
    validateOwner(userId);
    LocalDateTime now = LocalDateTime.now();

    final List<Item> ownerItems = itemRepository.findAllByOwnerIdOrderById(userId);
    if (ownerItems.isEmpty()) {
      return Collections.emptyList();
    }

    final Map<Long, List<Booking>> bookingsForItems =
        bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId)
            .stream()
            .filter(b -> !b.getStatus().equals(BookingStatus.REJECTED))
            .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

    final Map<Long, List<Comment>> commentsForItems =
        commentRepository.findAllByItemIdIn(ownerItems
                .stream()
                .map(Item::getId)
                .toList())
            .stream()
            .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

    return ownerItems.stream()
        .map(item ->
            ItemMapper.mapToItemDto(
                item,
                getLastBooking(bookingsForItems.get(item.getId()), now),
                getNextBooking(bookingsForItems.get(item.getId()), now),
                CommentMapper.mapToCommentDto(commentsForItems.get(item.getId()))))
        .toList();
  }

  @Override
  public List<ItemDto> searchItemsByPartialText(final String text) {
    if (text == null || text.isBlank()) {
      log.info("Search text is null or blank. Returning an empty result list.");
      return Collections.emptyList();
    }
    log.debug("Searching available for renting items by text {}.", text);
    return ItemMapper.mapToItemDto(itemRepository.findByText(text));
  }

  @Override
  @Transactional
  public CommentDto addComment(final Long userId, final Long itemId, final CommentDto comment) {
    log.debug("Persisting a new comment with text: {} for item ID {} by user ID {}."
        , comment, itemId, userId);

    final Item item = getItemOrThrow(itemId);
    final User user = userService.getByIdOrThrow(userId);

    validateBookingsByBookerAndItem(itemId, userId, LocalDateTime.now());

    final Comment commentToSave = CommentMapper.mapToComment(comment, item, user,
        LocalDateTime.now());
    return CommentMapper.mapToCommentDto(commentRepository.save(commentToSave));
  }

  private void validateBookingsByBookerAndItem(final Long itemId, final Long userId,
                                               final LocalDateTime now) {
    log.debug("Fetching PAST bookings for itemId {}, bookerId {}.", itemId, userId);
    if (!bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
        itemId, userId, BookingStatus.APPROVED, now)) {
      log.warn("Failing matching itemId {} and bookerId {} for completed bookings.", itemId,
          userId);
      throw new ValidationException("User is not allowed to leave a comment.");
    }
    log.debug("Success: bookerId {} has completed bookings for itemId {}.", userId, itemId);
  }

  private BookingShortDto getLastBooking(final List<Booking> bookings, final LocalDateTime point) {
    if (bookings == null) {
      return null;
    }
    return bookings.stream()
        .filter(b -> b.getEnd().isBefore(point))
        .max(Comparator.comparing(Booking::getEnd))
        .map(BookingMapper::mapToShortDto) // Assuming you have a mapper for this
        .orElse(null);
  }

  private BookingShortDto getNextBooking(final List<Booking> bookings, final LocalDateTime point) {
    if (bookings == null || bookings.isEmpty()) {
      return null;
    }
    return bookings.stream()
        .filter(b -> b.getStart().isAfter(point))
        .min(Comparator.comparing(Booking::getStart))
        .map(BookingMapper::mapToShortDto) // Assuming you have a mapper for this
        .orElse(null);
  }


  private Item getItemByIdAndOwnerOrThrow(final Long itemId, final Long userId) {
    return itemRepository.findByIdAndOwnerId(itemId, userId)
        .orElseThrow(() -> {
          log.warn("Fail to get item with itemId {} and ownerId {} from DB.", itemId, userId);
          return new NotFoundException(
              "User is not the owner of the item.");
        });
  }

  private void validateOwner(final Long userId) {
    log.debug("Validating user with ID {} exists in DB and actually owns at least one item.",
        userId);
    validateUser(userId);
    if (!itemRepository.existsByOwnerId(userId)) {
      log.warn("Fail ownership validation,User with ID = {} does not possess any items.", userId);
      throw new NotFoundException("No items found for user with ID." + userId);
    }
    log.debug("Success in validating owner with ID {} is not null and possesses some items.",
        userId);
  }

  private void validateUser(final Long userId) {
    log.debug("Validating user existence for user ID = {}.", userId);
    userService.validateUserExist(userId);
  }

}
