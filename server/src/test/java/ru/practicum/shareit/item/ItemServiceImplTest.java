package ru.practicum.shareit.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.BookingBuilder;
import ru.practicum.shareit.utils.ItemBuilder;
import ru.practicum.shareit.utils.RequestBuilder;
import ru.practicum.shareit.utils.UserBuilder;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

  @InjectMocks
  private ItemServiceImpl itemService;

  @Mock
  private ItemRepository itemRepository;
  @Mock
  private UserService userService;
  @Mock
  private BookingRepository bookingRepository;
  @Mock
  private CommentRepository commentRepository;
  @Mock
  private ItemRequestRepository requestRepository;

  private Item item;
  private Item item2;
  private ItemDto itemDto;
  private ItemDto itemDto2;
  private User owner;
  private ItemRequest itemRequest;
  private Booking lastBooking;
  private Booking nextBooking;
  private List<Booking> bookings;
  private List<Comment> comments;

  private final Long userId = 1L; // ownerId
  private final Long requestId = 2L;
  private final Long itemId = 1L;
  private final LocalDateTime now = LocalDateTime.now();

  @BeforeEach
  void setUp() {
    owner = UserBuilder.buildUser("Owner", "owner@test.com").setId(userId);
    itemDto = ItemBuilder.buildItemDto("Test Item", "Test Description");
    item = ItemMapper.mapToItem(itemDto, owner).setId(1L);
    itemRequest = RequestBuilder.buildItemRequest(
            UserBuilder.buildUser("Requester", "reqstr@test.com").setId(2L))
            .setId(requestId);
    item2 = ItemBuilder.buildItem("Test Item", "Test Description", true, owner).setId(2L);
    itemDto2 = ItemMapper.mapToItemDto(item2);

  }

  @Test
  void saveItem_whenValidInput_thenSave() {
    itemDto.setRequestId(requestId);
    item.setRequest(itemRequest);
    when(userService.getUserById(anyLong())).thenReturn(UserMapper.mapToUserDto(owner));
    when(itemRepository.save(any(Item.class))).thenReturn(item);
    when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

    ItemDto result = itemService.saveItem(userId, itemDto);

    assertNotNull(result);
    assertEquals(itemDto.getName(), result.getName());
    assertEquals(itemDto.getDescription(), result.getDescription());
    assertEquals(requestId, result.getRequestId());

    verify(userService, times(1)).getUserById(userId);
    verify(requestRepository, times(1)).findById(requestId);
    verify(itemRepository, times(1)).save(any(Item.class));
    verifyNoMoreInteractions(itemRepository, requestRepository, userService, commentRepository,
        bookingRepository);
  }

  @Test
  void saveItem_whenInvalidRequestId_thenSaveItemWithoutRequester() {
    itemDto.setRequestId(requestId);
    when(userService.getUserById(anyLong())).thenReturn(UserMapper.mapToUserDto(owner));
    when(itemRepository.save(any(Item.class))).thenReturn(item);
    when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

    ItemDto result = itemService.saveItem(userId, itemDto);

    assertNotNull(result);
    assertEquals(itemDto.getName(), result.getName());
    assertEquals(itemDto.getDescription(), result.getDescription());
    assertNull(result.getRequestId());

    verify(userService, times(1)).getUserById(userId);
    verify(requestRepository, times(1)).findById(requestId);
    verify(itemRepository, times(1)).save(any(Item.class));
    verifyNoMoreInteractions(itemRepository, requestRepository, userService, commentRepository,
        bookingRepository);
  }

  @Test
  void saveItem_whenUserNotFound_thenThrow() {

    when(userService.getUserById(anyLong()))
        .thenThrow(new NotFoundException("User not found"));

    NotFoundException exception = assertThrows(NotFoundException.class, () ->
        itemService.saveItem(userId, itemDto));

    assertEquals("User not found", exception.getMessage());
    verify(userService, times(1)).getUserById(userId);
    verifyNoInteractions(itemRepository, requestRepository);
  }

  @Test
  void updateItem_whenValidInput_thenUpdate() {

    doNothing().when(userService).validateUserExist(anyLong());
    when(itemRepository.existsByOwnerId(anyLong())).thenReturn(true);
    when(itemRepository.findByIdAndOwnerId(anyLong(), anyLong()))
        .thenReturn(Optional.of(item));
    when(itemRepository.save(any(Item.class))).thenReturn(item);

    ItemDto result = itemService.updateItem(userId, itemDto, itemId);

    assertNotNull(result);
    verify(userService, times(1)).validateUserExist(userId);
    verify(itemRepository, times(1)).existsByOwnerId(userId);
    verify(itemRepository, times(1)).findByIdAndOwnerId(itemId, userId);
    verify(itemRepository, times(1)).save(item);
    verifyNoMoreInteractions(itemRepository, userService);
    verifyNoInteractions(requestRepository, commentRepository, bookingRepository);
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("provideInvalidDataForUpdate")
  void updateItem_whenInvalidInput_thenThrow(String testName, boolean isOwner,
                                             Optional<Item> itemToUpdate) {

    doNothing().when(userService).validateUserExist(anyLong());
    when(itemRepository.existsByOwnerId(anyLong())).thenReturn(isOwner);//2
    lenient().when(itemRepository.findByIdAndOwnerId(anyLong(), anyLong()))//3
        .thenReturn(itemToUpdate);

    NotFoundException exception = assertThrows(NotFoundException.class, () ->
        itemService.updateItem(userId, itemDto, itemId));
    assertEquals(testName, exception.getMessage());

    verify(userService, times(1)).validateUserExist(userId);
    verify(itemRepository, times(1)).existsByOwnerId(userId);
    verify(itemRepository, never()).save(item);
    verifyNoInteractions(requestRepository, commentRepository, bookingRepository);
  }

  private static Stream<Arguments> provideInvalidDataForUpdate() {
    Item itemToUpdate = ItemBuilder.buildItem(
        "Test Item", "Test Description", true, UserBuilder.buildUser()).setId(1L);
    return Stream.of(
        Arguments.of("No items found for user with ID." + 1L, false,
            Optional.of(itemToUpdate)),
        Arguments.of("User is not the owner of the item.", true, Optional.empty())
    );
  }


  @Test
  void getItemById_whenValidInput_thenReturnWithBookingsAndComments() {

    prepareDataGetItem();
    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
    when(bookingRepository.findByItemIdAndItemOwnerId(anyLong(), anyLong()))
        .thenReturn(bookings);
    when(commentRepository.findAllByItemId(anyLong())).thenReturn(comments);

    ItemDto result = itemService.getItemById(itemId, userId);

    assertNotNull(result);
    assertEquals(lastBooking.getId(), result.getLastBooking().getId());
    assertEquals(nextBooking.getId(), result.getNextBooking().getId());
    assertEquals(1, result.getComments().size());

    verify(itemRepository).findById(itemId);
    verify(bookingRepository).findByItemIdAndItemOwnerId(itemId, userId);
    verify(commentRepository).findAllByItemId(itemId);
    verifyNoMoreInteractions(
        itemRepository, requestRepository, commentRepository, bookingRepository);
    verifyNoInteractions(userService);
  }

  @Test
  void getItemById_whenNoBookingsAndNoComments() {

    prepareDataGetItem();
    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
    when(bookingRepository.findByItemIdAndItemOwnerId(anyLong(), anyLong()))
        .thenReturn(List.of());
    when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of());

    ItemDto result = itemService.getItemById(itemId, userId);

    assertNotNull(result);
    assertNull(result.getLastBooking());
    assertNull(result.getNextBooking());
    assertEquals(0, result.getComments().size());

    verify(itemRepository).findById(itemId);
    verify(bookingRepository).findByItemIdAndItemOwnerId(itemId, userId);
    verify(commentRepository).findAllByItemId(itemId);
    verifyNoMoreInteractions(
        itemRepository, requestRepository, commentRepository, bookingRepository);
    verifyNoInteractions(userService);
  }

  @Test
  void getItemById_whenOnlyLastBooking() {

    prepareDataGetItem();
    List<Booking> pastBookings = List.of(lastBooking);
    when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
    when(bookingRepository.findByItemIdAndItemOwnerId(itemId, userId)).thenReturn(pastBookings);
    when(commentRepository.findAllByItemId(itemId)).thenReturn(comments);

    ItemDto result = itemService.getItemById(itemId, userId);

    assertNotNull(result);
    assertEquals(lastBooking.getId(), result.getLastBooking().getId());
    assertNull(result.getNextBooking());
    assertEquals(1, result.getComments().size());
    verify(itemRepository).findById(itemId);
    verify(bookingRepository).findByItemIdAndItemOwnerId(itemId, userId);
    verify(commentRepository).findAllByItemId(itemId);
    verifyNoMoreInteractions(
        itemRepository, commentRepository, bookingRepository);
    verifyNoInteractions(userService, requestRepository);
  }

  @Test
  void getItemById_OnlyNextBookings() {

    prepareDataGetItem();
    List<Booking> futureBookings = List.of(nextBooking);
    when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
    when(bookingRepository.findByItemIdAndItemOwnerId(itemId, userId))
        .thenReturn(futureBookings);
    when(commentRepository.findAllByItemId(itemId)).thenReturn(comments);

    ItemDto result = itemService.getItemById(itemId, userId);

    assertNotNull(result);
    assertEquals(nextBooking.getId(), result.getNextBooking().getId());
    assertNull(result.getLastBooking());
    assertEquals(1, result.getComments().size());
    verify(itemRepository).findById(itemId);
    verify(bookingRepository).findByItemIdAndItemOwnerId(itemId, userId);
    verify(commentRepository).findAllByItemId(itemId);
    verifyNoMoreInteractions(
        itemRepository, commentRepository, bookingRepository);
    verifyNoInteractions(userService, requestRepository);

  }

  @Test
  void getItemById_OnlyRejectedBookings() {

    prepareDataGetItem();
    bookings.forEach(b -> b.setStatus(BookingStatus.REJECTED));
    when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
    when(bookingRepository.findByItemIdAndItemOwnerId(itemId, userId))
        .thenReturn(bookings);
    when(commentRepository.findAllByItemId(itemId)).thenReturn(comments);

    ItemDto result = itemService.getItemById(itemId, userId);

    assertNotNull(result);
    assertNull(result.getNextBooking());
    assertNull(result.getLastBooking());
    assertEquals(1, result.getComments().size());
    verify(itemRepository).findById(itemId);
    verify(bookingRepository).findByItemIdAndItemOwnerId(itemId, userId);
    verify(commentRepository).findAllByItemId(itemId);
    verifyNoMoreInteractions(
        itemRepository, commentRepository, bookingRepository);
    verifyNoInteractions(userService, requestRepository);
  }

  @Test
  void getItemOrThrow_whenInvalidItemId_thenThrow() {
    when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class, () ->
        itemService.getItemOrThrow(itemId));
    assertEquals("Item not found.", exception.getMessage());

    verify(itemRepository, times(1)).findById(itemId);
    verifyNoMoreInteractions(itemRepository);
    verifyNoInteractions(userService, requestRepository, commentRepository, bookingRepository);
  }

  @Test
  void getUserItems_whenValidInput_thenReturnData() {
    prepareDataGetItemsForOwner();
    doNothing().when(userService).validateUserExist(anyLong());
    when(itemRepository.existsByOwnerId(anyLong())).thenReturn(true);
    when(itemRepository.findAllByOwnerIdOrderById(anyLong()))
        .thenReturn(List.of(item, item2));
    when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong()))
        .thenReturn(bookings);
    when(commentRepository.findAllByItemIdIn(anyList()))
        .thenReturn(comments);

    List<ItemDto> result = itemService.getUserItems(userId);

    assertNotNull(result);
    assertEquals(2, result.size());
    verify(userService).validateUserExist(userId);
    verify(itemRepository).existsByOwnerId(userId);
    verify(itemRepository).findAllByOwnerIdOrderById(userId);
    verify(bookingRepository).findAllByItemOwnerIdOrderByStartDesc(userId);
    verify(commentRepository).findAllByItemIdIn(anyList());
  }

  @Test
  void getUserItems_whenNoItemsForOwnerFound_thenEmptyList() {

    doNothing().when(userService).validateUserExist(anyLong());
    when(itemRepository.existsByOwnerId(anyLong())).thenReturn(true);
    when(itemRepository.findAllByOwnerIdOrderById(anyLong()))
        .thenReturn(List.of());

    List<ItemDto> result = itemService.getUserItems(userId);

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(userService).validateUserExist(userId);
    verify(itemRepository).existsByOwnerId(userId);
    verify(itemRepository).findAllByOwnerIdOrderById(userId);
    verify(bookingRepository, never()).findAllByItemOwnerIdOrderByStartDesc(anyLong());
    verify(commentRepository, never()).findAllByItemIdIn(anyList());
  }

  @Test
  void getUserItems_RejectedBookingsFiltered() {

    prepareDataGetItemsForOwner();
    bookings.getFirst().setStatus(BookingStatus.REJECTED);

    doNothing().when(userService).validateUserExist(anyLong());
    when(itemRepository.existsByOwnerId(anyLong())).thenReturn(true);
    when(itemRepository.findAllByOwnerIdOrderById(anyLong()))
        .thenReturn(List.of(item, item2));
    when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong()))
        .thenReturn(bookings);
    when(commentRepository.findAllByItemIdIn(anyList()))
        .thenReturn(comments);

    List<ItemDto> result = itemService.getUserItems(userId);

    assertNotNull(result);
    assertEquals(2, result.size());

    verify(itemRepository).findAllByOwnerIdOrderById(userId);
    verify(bookingRepository).findAllByItemOwnerIdOrderByStartDesc(userId);
    verify(commentRepository).findAllByItemIdIn(anyList());
    verify(bookingRepository).findAllByItemOwnerIdOrderByStartDesc(userId);
  }

  @Test
  void searchItemsByPartialText_ValidText_ReturnsMatchingItems() {
    when(itemRepository.findByText(anyString())).thenReturn(List.of(item, item2));

    List<ItemDto> result = itemService.searchItemsByPartialText("script");

    assertNotNull(result);
    assertEquals(2, result.size());

    verify(itemRepository).findByText("script");
  }


  @ParameterizedTest(name = "{0}")
  @MethodSource("provideSearchText")
  void searchItemsByPartialText_whenTextIsNullThenEmptyList(String testName, String text) {
    List<ItemDto> result = itemService.searchItemsByPartialText(text);

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verifyNoInteractions(itemRepository);
  }

  private static Stream<Arguments> provideSearchText() {
    return Stream.of(
        Arguments.of("Null value", null),
        Arguments.of("Blank value", "    ")
    );
  }

  @Test
  void addComment_whenValidInput_thenAddComment() {
    User booker = UserBuilder.buildUser("Booker", "booker@test.com").setId(2L);
    Comment comment = new Comment(1L, "Nice item!", item, booker, now);
    CommentDto commentDto = CommentMapper.mapToCommentDto(comment);
    ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);

    when(itemRepository.findById(anyLong()))
        .thenReturn(Optional.of(item));
    when(userService.getByIdOrThrow(anyLong())).thenReturn(booker);
    when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
        anyLong(), anyLong(), any(BookingStatus.class),
        captor.capture())).thenReturn(true);
    when(commentRepository.save(any(Comment.class))).thenReturn(comment);

    CommentDto result = itemService.addComment(booker.getId(), itemId, commentDto);

    assertNotNull(result);
    assertEquals(commentDto.getText(), result.getText());
    assertEquals(commentDto.getAuthorName(), result.getAuthorName());

    verify(itemRepository).findById(itemId);
    verify(userService).getByIdOrThrow(booker.getId());

  }

  @Test
  void addComment_whenUserIsNotAllowedToComment_thenThrow() {
    CommentDto commentDto = CommentDto.builder()
        .text("Nice item!").build();

    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
    when(userService.getByIdOrThrow(anyLong())).thenReturn(owner);

    when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
        anyLong(), anyLong(), any(BookingStatus.class), any(LocalDateTime.class))
    ).thenReturn(false);

    ValidationException exception = assertThrows(ValidationException.class, () ->
        itemService.addComment(1L, 1L, commentDto)
    );

    assertEquals("User is not allowed to leave a comment.", exception.getMessage());

    verify(commentRepository, never()).save(any(Comment.class));
  }

  @Test
  void getItemByRequestIds_returnListOfItems() {
    itemDto.setId(1L);
    itemDto2.setId(2L);
    List<Long> requestIds = List.of(1L, 2L);
    List<Item> items = List.of(item, item2);

    when(itemRepository.findByRequestIdIn(requestIds)).thenReturn(items);

    List<ItemDto> result = itemService.getItemByRequestIds(requestIds);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(itemDto, result.get(0));
    assertEquals(itemDto2, result.get(1));

    verify(itemRepository).findByRequestIdIn(requestIds);

  }

  private void prepareDataGetItem() {
    User booker = UserBuilder.buildUser("Booker", "booker@test.com").setId(2L);
    lastBooking = BookingBuilder.buildBooking(item, booker, now.minusDays(5), now.minusDays(2),
        BookingStatus.APPROVED).setId(1L);
    nextBooking = BookingBuilder.buildBooking(item, booker, now.plusDays(1), now.plusDays(3),
        BookingStatus.APPROVED).setId(2L);
    bookings = List.of(lastBooking, nextBooking);
    comments = List.of(new Comment(1L, "Test Comment", item,
        UserBuilder.buildUser("Commenter", "commenter@test.com").setId(2L), now));
  }

  private void prepareDataGetItemsForOwner() {
    User booker = UserBuilder.buildUser("Booker", "booker@test.com").setId(2L);

    lastBooking = BookingBuilder.buildBooking(item, booker, now.minusDays(5), now.minusDays(2),
        BookingStatus.APPROVED).setId(1L);
    nextBooking = BookingBuilder.buildBooking(item2, booker, now.plusDays(1), now.plusDays(3),
        BookingStatus.APPROVED).setId(2L);
    bookings = List.of(lastBooking, nextBooking);
    comments = List.of(new Comment(1L, "Test Comment", item,
        UserBuilder.buildUser("Commenter", "commenter@test.com").setId(2L), now));

  }
}