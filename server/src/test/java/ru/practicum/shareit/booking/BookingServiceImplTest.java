package ru.practicum.shareit.booking;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserAuthorizationException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.BookingBuilder;
import ru.practicum.shareit.utils.ItemBuilder;
import ru.practicum.shareit.utils.UserBuilder;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

  @InjectMocks
  private BookingServiceImpl bookingService;

  @Mock
  private BookingRepository bookingRepository;

  @Mock
  private UserService userService;

  @Mock
  private ItemService itemService;

  private Long itemId;
  private Long userId;
  private List<User> userList;
  private List<Item> itemList;
  private List<Booking> bookingList;
  private LocalDateTime currentTime;
  private User user;
  private Item item;
  private Booking booking;
  private BookingDto bookingDto;

  @BeforeEach
  void setUp() {
    userId = 1L;
    itemId = 1L;

    bookingDto = buildBookingDto();
    user = buildUser();
    item = buildItem();
    item.setId(itemId);
    booking = buildBooking();
  }

  @Test
  void createBooking_whenValidInput_created() {
    item.setOwner(User.builder().id(10L).build());
    booking.setId(1L);
    doNothing().when(userService).validateUserExist(userId);
    when(userService.getByIdOrThrow(userId)).thenReturn(user);
    when(itemService.getItemOrThrow(itemId)).thenReturn(item);
    when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
    when(
        bookingRepository.existsByItemIdAndEndAfterAndStartBeforeAndStatus(anyLong(), any(), any()))
        .thenReturn(false);

    BookingResponseDto result = bookingService.createBooking(userId, bookingDto);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    verify(userService, times(1)).validateUserExist(anyLong());
    verify(bookingRepository, times(1)).save(any(Booking.class));
    verify(bookingRepository, times(1))
        .existsByItemIdAndEndAfterAndStartBeforeAndStatus(anyLong(), any(), any());
    verify(userService, times(1)).getByIdOrThrow(userId);
    verify(itemService, times(1)).getItemOrThrow(itemId);
    verifyNoMoreInteractions(userService, bookingRepository, itemService);
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("provideInvalidBookingCases")
  void createBooking_negativeScenarios(
      String testName, Long ownerId, boolean available, boolean alreadyBooked,
      Class<? extends Throwable> expectedException) {

    item.setOwner(User.builder().id(ownerId).build());
    item.setAvailable(available);
    booking.setId(1L);
    doNothing().when(userService).validateUserExist(userId);
    when(userService.getByIdOrThrow(userId)).thenReturn(user);
    when(itemService.getItemOrThrow(itemId)).thenReturn(item);
    lenient().when(
            bookingRepository.existsByItemIdAndEndAfterAndStartBeforeAndStatus(anyLong(), any(), any()))
        .thenReturn(alreadyBooked);
    lenient().when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

    Throwable exception = assertThrows(expectedException, () ->
        bookingService.createBooking(userId, bookingDto));
    assertEquals(testName, exception.getMessage());
  }

  private static Stream<Arguments> provideInvalidBookingCases() {

    return Stream.of(
        Arguments.of("Booker can not be owner of item to book.",
            1L, true, false, NotFoundException.class),
        Arguments.of("Item is currently unavailable for borrowing.",
            10L, false, false, ValidationException.class),
        Arguments.of("Item is already booked for the specified time range.",
            10L, true, true, ValidationException.class)
    );
  }

  @Test
  void createBooking_validateUserAuthorizedThrowUserAuthorizationException() {

    doThrow(new NotFoundException("User not found"))
        .when(userService).validateUserExist(anyLong());

    UserAuthorizationException exception = assertThrows(
        UserAuthorizationException.class, () ->
            bookingService.createBooking(userId, bookingDto));

    assertEquals("User is not authorized.", exception.getMessage());
    verify(userService, times(1)).validateUserExist(userId);
    verifyNoMoreInteractions(userService, bookingRepository, itemService);
  }

  @Test
  void createBooking_whenItemToBookNotExist_thenThrow() {

    doNothing().when(userService).validateUserExist(userId);
    when(userService.getByIdOrThrow(anyLong())).thenReturn(user);
    doThrow(new NotFoundException("Item not found."))
        .when(itemService).getItemOrThrow(anyLong());

    NotFoundException exception = assertThrows(
        NotFoundException.class, () ->
            bookingService.createBooking(userId, bookingDto));

    assertEquals("Item not found.", exception.getMessage());
    verify(userService, times(1)).validateUserExist(userId);
    verify(userService, times(1)).getByIdOrThrow(userId);
    verify(itemService, times(1)).getItemOrThrow(itemId);
    verifyNoMoreInteractions(userService, bookingRepository, itemService);
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("provideStatusesForUpdating")
  void updateStatus_whenValidInput_thenUpdate(String testName, Boolean approved,
                                              BookingStatus status) {
    booking.setId(1L);

    doNothing().when(userService).validateUserExist(anyLong());
    when(bookingRepository.findByIdAndItemOwnerId(anyLong(), anyLong()))
        .thenReturn(Optional.of(booking));
    when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

    BookingResponseDto result = bookingService.updateStatus(1L, userId, approved);

    assertNotNull(result);
    assertEquals(status, result.getStatus());
    verify(userService, times(2)).validateUserExist(userId);
    verify(bookingRepository, times(1))
        .findByIdAndItemOwnerId(1L, userId);
    verify(bookingRepository).save(booking);
    verifyNoMoreInteractions(userService, bookingRepository, itemService);
  }


  private static Stream<Arguments> provideStatusesForUpdating() {
    return Stream.of(
        Arguments.of("To Approved", true, BookingStatus.APPROVED),
        Arguments.of("To Rejected", false, BookingStatus.REJECTED)
    );
  }

  @Test
  void updateStatus_whenBookingNotFoundOrUserIsNotOwner_thenThrow() {

    doNothing().when(userService).validateUserExist(anyLong());
    when(bookingRepository.findByIdAndItemOwnerId(anyLong(), anyLong()))
        .thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class,
        () -> bookingService.updateStatus(1L, userId, true));
    assertEquals("Booking not found or user is not the owner.", exception.getMessage());
    verify(bookingRepository, never()).save(any(Booking.class));
  }

  @Test
  void updateStatus_whenInvalidStatusTransition_thenThrowValidationException() {
    booking.setId(1L);
    booking.setStatus(BookingStatus.APPROVED);
    doNothing().when(userService).validateUserExist(anyLong());
    when(bookingRepository.findByIdAndItemOwnerId(anyLong(), anyLong()))
        .thenReturn(Optional.of(booking));

    ValidationException exception = assertThrows(ValidationException.class,
        () -> bookingService.updateStatus(1L, userId, true));

    assertEquals("Invalid status transition.", exception.getMessage());
    verify(bookingRepository, never()).save(any(Booking.class));
  }


  @Test
  void getBookingById_whenValidInputThenReturnBooking() {
    booking.setId(1L);

    doNothing().when(userService).validateUserExist(anyLong());
    when(bookingRepository.findByIdAndItemOwnerIdOrBookerId(anyLong(), anyLong()))
        .thenReturn(Optional.of(booking));

    BookingResponseDto result = bookingService.getBookingById(1L, userId);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(BookingStatus.WAITING, result.getStatus());
    verify(userService, times(1)).validateUserExist(anyLong());
    verify(bookingRepository, times(1)).findByIdAndItemOwnerIdOrBookerId(anyLong(), anyLong());
    verifyNoMoreInteractions(userService, bookingRepository, itemService);
  }

  @Test
  void getBookingById_whenBookingNotFound_thenThrowNotFoundException() {

    doNothing().when(userService).validateUserExist(anyLong());
    when(bookingRepository.findByIdAndItemOwnerIdOrBookerId(anyLong(), anyLong()))
        .thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class,
        () -> bookingService.getBookingById(1L, userId));

    assertEquals("Booking not found.", exception.getMessage());
    verify(userService, times(1)).validateUserExist(anyLong());
    verify(bookingRepository).findByIdAndItemOwnerIdOrBookerId(anyLong(), anyLong());
    verifyNoMoreInteractions(userService, bookingRepository, itemService);
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("provideStates")
  void getAllBookingForUser_whenValidInput(String state, BookingStatus status) {

    User owner = UserBuilder.buildUser("Item1 Owner", "owner@test.com").setId(2L);
    item.setOwner(owner);
    booking.setId(1L);
    booking.setBooker(user);
    booking.setItem(item);
    List<Booking> bookings = List.of(booking);
    bookings.getFirst().setStatus(status);

    doNothing().when(userService).validateUserExist(anyLong());

    lenient().when(bookingRepository
            .findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any()))
        .thenReturn(bookings);
    lenient().when(bookingRepository
            .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any()))
        .thenReturn(bookings);
    lenient().when(bookingRepository
            .findByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any()))
        .thenReturn(bookings);
    lenient().when(bookingRepository
            .findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any()))
        .thenReturn(bookings);
    lenient().when(bookingRepository
            .findAllByBookerIdOrderByStartDesc(anyLong()))
        .thenReturn(bookings);

    List<BookingResponseDto> result = bookingService.getAllBookingForUser(userId, state);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(status, result.getFirst().getStatus());
    assertEquals(userId, result.getFirst().getBooker().getId());

    verify(userService, times(1)).validateUserExist(anyLong());
    switch (state) {
      case "WAITING" -> verify(bookingRepository, times(1))
          .findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
      case "REJECTED" -> verify(bookingRepository, times(1))
          .findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
      case "CURRENT" -> verify(bookingRepository, times(1))
          .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(userId), any());
      case "PAST" -> verify(bookingRepository, times(1))
          .findByBookerIdAndEndBeforeOrderByStartDesc(eq(userId), any());
      case "FUTURE" -> verify(bookingRepository, times(1))
          .findAllByBookerIdAndStartAfterOrderByStartDesc(eq(userId), any());
      case "ALL" -> verify(bookingRepository, times(1))
          .findAllByBookerIdOrderByStartDesc(userId);
    }
    verifyNoMoreInteractions(userService, itemService, bookingRepository);
  }

  private static Stream<Arguments> provideStates() {
    return Stream.of(
        Arguments.of("WAITING", BookingStatus.WAITING),
        Arguments.of("REJECTED", BookingStatus.REJECTED),
        Arguments.of("CURRENT", BookingStatus.APPROVED),
        Arguments.of("PAST", BookingStatus.APPROVED),
        Arguments.of("FUTURE", BookingStatus.APPROVED),
        Arguments.of("ALL", BookingStatus.APPROVED)
    );

  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("provideOwnerStates")
  void getAllBookingForOwner_whenValidInput(String state, BookingStatus status) {

    User booker = UserBuilder.buildUser("Item1 Owner", "owner@test.com").setId(2L);
    item.setOwner(user);
    booking.setId(1L);
    booking.setBooker(booker);
    booking.setItem(item);
    booking.setStatus(status);
    List<Booking> bookings = List.of(booking);

    doNothing().when(userService).validateUserExist(anyLong());

    lenient().when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(),
            eq(BookingStatus.WAITING)))
        .thenReturn(state.equals("WAITING") ? bookings : List.of());
    lenient().when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(),
            eq(BookingStatus.REJECTED)))
        .thenReturn(state.equals("REJECTED") ? bookings : List.of());
    lenient().when(
            bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(),
                any()))
        .thenReturn(state.equals("CURRENT") ? bookings : List.of());
    lenient().when(
            bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any()))
        .thenReturn(state.equals("PAST") ? bookings : List.of());
    lenient().when(
            bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any()))
        .thenReturn(state.equals("FUTURE") ? bookings : List.of());
    lenient().when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong()))
        .thenReturn(state.equals("ALL") ? bookings : List.of());

    List<BookingResponseDto> result = bookingService.getAllBookingForOwner(userId, state);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(status, result.getFirst().getStatus());
    assertNotEquals(userId, result.getFirst().getBooker().getId());

    // Verify user authorization
    verify(userService, times(1)).validateUserExist(anyLong());

    // Verify that the correct repository method is called based on the state
    switch (state) {
      case "WAITING" -> verify(bookingRepository, times(1))
          .findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
      case "REJECTED" -> verify(bookingRepository, times(1))
          .findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
      case "CURRENT" -> verify(bookingRepository, times(1))
          .findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(userId), any());
      case "PAST" -> verify(bookingRepository, times(1))
          .findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(eq(userId), any());
      case "FUTURE" -> verify(bookingRepository, times(1))
          .findAllByItemOwnerIdAndStartAfterOrderByStartDesc(eq(userId), any());
      case "ALL" -> verify(bookingRepository, times(1))
          .findAllByItemOwnerIdOrderByStartDesc(userId);
    }

    verifyNoMoreInteractions(userService, itemService, bookingRepository);
  }

  private static Stream<Arguments> provideOwnerStates() {
    return Stream.of(
        Arguments.of("WAITING", BookingStatus.WAITING),
        Arguments.of("REJECTED", BookingStatus.REJECTED),
        Arguments.of("CURRENT", BookingStatus.APPROVED),
        Arguments.of("PAST", BookingStatus.APPROVED),
        Arguments.of("FUTURE", BookingStatus.APPROVED),
        Arguments.of("ALL", BookingStatus.APPROVED)
    );
  }

  private Booking buildBooking() {
    LocalDateTime timePoint = LocalDateTime.now();
    return BookingBuilder.buildBooking(timePoint, item, user);

  }

  private Item buildItem() {
    return ItemBuilder.buildItem();
  }

  private User buildUser() {
    return UserBuilder.buildUser();
  }

  private BookingDto buildBookingDto() {
    LocalDateTime timePoint = LocalDateTime.now();
    return BookingBuilder.buildBookingDto(timePoint);
  }
}


