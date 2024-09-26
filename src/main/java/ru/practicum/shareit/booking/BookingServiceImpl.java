package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserAuthorizationException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

/**
 * Service implementation class for managing booking-related operations. This class also contains
 * helper methods for validating the availability of an item, checking user authorization, and
 * handling various booking states.
 *
 * @see Booking
 * @see BookingDto
 * @see BookingResponseDto
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

  private final BookingRepository bookingRepository;
  private final UserService userService;
  private final ItemService itemService;


  @Override
  @Transactional
  public BookingResponseDto createBooking(final Long userId, final BookingDto bookingDto) {
    log.debug("Creating a booking {} for user with ID {}.", bookingDto, userId);
    validateUserAuthorized(userId);

    final User booker = userService.getByIdOrThrow(userId);
    final Item itemToBook = itemService.getItemOrThrow(bookingDto.getItemId());

    if (userId.equals(itemToBook.getId())) {
      throw new NotFoundException("Booker can not be owner of item to book.");
    }
    validateItemAvailable(itemToBook, bookingDto);

    final Booking bookingToSave = BookingMapper.mapToBooking(bookingDto);
    bookingToSave.setItem(itemToBook);
    bookingToSave.setBooker(booker);
    bookingToSave.setStatus(BookingStatus.WAITING);

    final Booking saved = bookingRepository.save(bookingToSave);
    return BookingMapper.mapToResponseDto(saved);
  }

  @Override
  @Transactional
  public BookingResponseDto updateStatus(
      final Long bookingId, final Long userId, final Boolean approved) {
    log.debug("Updating a booking status of booking {} by owner ID {}.", bookingId, userId);

    validateUserAuthorized(userId);
    final Booking bookingToUpdate = getBookingByIdAndOwnerOrThrow(bookingId, userId);
    bookingToUpdate.updateStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

    final Booking bookingUpdated = bookingRepository.save(bookingToUpdate);
    return BookingMapper.mapToResponseDto(bookingUpdated);
  }

  @Override
  public BookingResponseDto getBookingById(final Long bookingId, final Long userId) {
    log.debug("Fetching Booking ID {} where user ID {} is either Owner or Booker.",
        bookingId, userId);
    validateUserAuthorized(userId);

    final Booking bookingFound =
        bookingRepository.findByIdAndItemOwnerIdOrBookerId(bookingId, userId)
            .orElseThrow(() -> {
              log.warn("Booking ID {} where user ID {} is either Owner or Booker not found.",
                  bookingId, userId);
              return new NotFoundException("Booking not found.");
            });
    return BookingMapper.mapToResponseDto(bookingFound);
  }

  @Override
  public List<BookingResponseDto> getAllBookingForUser(final Long bookerId, final String state) {
    log.debug("Fetching all bookings for borrower ID {} and state {}.",
        bookerId, state);
    validateUserAuthorized(bookerId);
    return getBookingsForUserOrOwner(bookerId, state, true);
  }

  @Override
  public List<BookingResponseDto> getAllBookingForOwner(final Long ownerId, final String state) {
    log.debug("Fetching all bookings for items owner ID {} and state {}.",
        ownerId, state);
    validateUserAuthorized(ownerId);
    return getBookingsForUserOrOwner(ownerId, state, false);
  }

  private List<BookingResponseDto> getBookingsForUserOrOwner(
      final Long id, final String state, final boolean isUser) {
    LocalDateTime now = LocalDateTime.now();

    final List<Booking> bookings =
        switch (BookingState.fromString(state)) {
          case WAITING -> isUser ?
              getAllByBookerAndStatus(id, BookingStatus.WAITING)
              : getAllByOwnerAndStatus(id, BookingStatus.WAITING);
          case REJECTED -> isUser ?
              getAllByBookerAndStatus(id, BookingStatus.REJECTED)
              : getAllByOwnerAndStatus(id, BookingStatus.REJECTED);
          case CURRENT -> isUser ?
              getCurrentByBooker(id, now)
              : getCurrentByOwner(id, now);
          case PAST -> isUser ?
              getPastByBooker(id, now)
              : getPastByOwner(id, now);
          case FUTURE -> isUser ?
              getFutureByBooker(id, now)
              : getFutureByOwner(id, now);
          default -> isUser ?
              bookingRepository.findAllByBookerIdOrderByStartDesc(id)
              : bookingRepository.findAllByItemOwnerIdOrderByStartDesc(id);
        };

    return BookingMapper.mapToResponseDto(bookings);
  }

  private List<Booking> getFutureByOwner(final Long ownerId, final LocalDateTime now) {
    return bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
  }

  private List<Booking> getPastByOwner(final Long ownerId, final LocalDateTime now) {
    return bookingRepository.findAllByItemOwnerIdAndStatusAndEndBeforeOrderByStartDesc(
        ownerId, BookingStatus.APPROVED, now);
  }

  private List<Booking> getCurrentByOwner(final Long ownerId, final LocalDateTime now) {
    return bookingRepository.findAllByItemOwnerIdAndStatusAndStartBeforeAndEndAfterOrderByStartDesc(
        ownerId, BookingStatus.APPROVED, now, now);
  }

  private List<Booking> getAllByOwnerAndStatus(final Long ownerId, final BookingStatus status) {
    return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, status);
  }

  private List<Booking> getFutureByBooker(final Long bookerId, final LocalDateTime now) {
    return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
        bookerId, now);
  }

  private List<Booking> getPastByBooker(final Long bookerId, final LocalDateTime now) {
    return bookingRepository.findByBookerIdAndStatusAndEndBeforeOrderByStartDesc(
        bookerId, BookingStatus.APPROVED, now);
  }

  private List<Booking> getCurrentByBooker(final Long bookerId, final LocalDateTime now) {
    return bookingRepository.findAllByBookerIdAndStatusAndStartBeforeAndEndAfterOrderByStartDesc(
        bookerId, BookingStatus.APPROVED,
        now, now);
  }

  private List<Booking> getAllByBookerAndStatus(final Long bookerId, final BookingStatus status) {
    return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, status);
  }

  private Booking getBookingByIdAndOwnerOrThrow(final Long bookingId, final Long ownerId) {
    log.debug("Fetching booking from DB by ID {} and item owner ID {}", bookingId, ownerId);
    userService.validateUserExist(ownerId);
    return bookingRepository.findByIdAndItemOwnerId(bookingId, ownerId)
        .orElseThrow(() -> {
          log.warn("Booking ID {} not found for owner ID {}.", bookingId, ownerId);
          return new NotFoundException("Booking not found or user is not the owner.");
        });
  }

  private void validateItemAvailable(final Item item, final BookingDto booking) {
    log.info("Validating availability for item ID {} for the period {} - {}.",
        item.getId(), booking.getStart(), booking.getEnd());
    if (!item.isAvailable()) {
      log.warn("Item with ID {} is unavailable, available status = {}.", item.getId(),
          item.isAvailable());
      throw new ValidationException(
          "Item is currently unavailable for borrowing.");
    }
    log.debug("ID {} has available status = {}", item.getId(), item.isAvailable());

    validateItemNotBooked(item.getId(), booking.getStart(), booking.getEnd());
    log.debug("Success: Item ID {} has not time overlap and is available for booking.",
        item.getId());
  }

  private void validateItemNotBooked(final Long itemId, LocalDateTime start, LocalDateTime end) {
    if (bookingRepository.existsByItemIdAndEndAfterAndStartBeforeAndStatus(itemId, start, end)) {
      throw new ValidationException("Item is already booked for the specified time range.");
    }
  }

  private void validateUserAuthorized(final Long userId) {
    try {
      userService.validateUserExist(userId);
    } catch (NotFoundException e) {
      log.warn("User with ID {} is not authorized in a system.", userId);
      throw new UserAuthorizationException("User is not authorized.");
    }
  }

}
