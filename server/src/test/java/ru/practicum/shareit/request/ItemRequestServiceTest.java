package ru.practicum.shareit.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.RequestBuilder;
import ru.practicum.shareit.utils.UserBuilder;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

  @InjectMocks
  ItemRequestServiceImpl requestService;

  @Mock
  private ItemRequestRepository requestRepository;

  @Mock
  private UserService userService;

  @Mock
  private ItemService itemService;

  private Long userId;
  private Long requestId;
  private ItemRequestDto requestDto;
  private ItemRequest request;
  private List<ItemRequest> requestList;
  private List<ItemDto> itemList;

  @BeforeEach
  void setUp() {
    userId = 1L;
    requestId = 2L;
    requestDto = buildRequestDto();
    request = buildRequest(LocalDateTime.now())
        .setId(requestId);
    requestList = List.of(request);
    itemList = List.of();
  }

  @Test
  void saveRequest_whenValidInput_thenSuccess() {
    when(userService.getByIdOrThrow(userId)).thenReturn(buildUser());
    when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);

    ItemRequestDto result = requestService.saveRequest(userId, requestDto);

    assertNotNull(result);
    assertEquals(requestId, result.getId());
    verify(requestRepository, times(1)).save(any(ItemRequest.class));
    verify(userService, times(1)).getByIdOrThrow(userId);
    verifyNoMoreInteractions(userService, requestRepository);
  }

  @Test
  void saveRequest_whenUserNotExist_thenThrow() {
    doThrow(new NotFoundException("User not found")).when(userService).getByIdOrThrow(anyLong());

    assertThrows(NotFoundException.class, () -> requestService.saveRequest(userId, requestDto));

    verify(userService).getByIdOrThrow(userId);
    verify(requestRepository, never()).save(any(ItemRequest.class));
    verifyNoMoreInteractions(userService, requestRepository);
  }

  @Test
  void getUserItemRequests_whenFound_thenReturnList() {
    when(requestRepository.findByRequestorIdOrderByCreatedDesc(userId)).thenReturn(requestList);
    when(itemService.getItemByRequestIds(List.of(requestId))).thenReturn(itemList);

    List<ItemRequestDto> result = requestService.getUserItemRequests(userId);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(userService, times(1)).validateUserExist(userId);
    verify(requestRepository, times(1)).findByRequestorIdOrderByCreatedDesc(userId);
    verify(itemService, times(1)).getItemByRequestIds(List.of(requestId));
    verifyNoMoreInteractions(userService, requestRepository);
  }

  @Test
  void getUserItemRequests_whenUserDontHaveRequests_thenEmptyList() {
    when(requestRepository.findByRequestorIdOrderByCreatedDesc(userId)).thenReturn(List.of());

    List<ItemRequestDto> result = requestService.getUserItemRequests(userId);

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(userService, times(1)).validateUserExist(userId);
    verify(requestRepository, times(1)).findByRequestorIdOrderByCreatedDesc(userId);
    verifyNoMoreInteractions(userService, requestRepository);
  }

  @Test
  void getAll_success() {
    when(requestRepository.findByRequestorIdNotOrderByCreatedDesc(userId)).thenReturn(requestList);
    when(itemService.getItemByRequestIds(List.of(requestId))).thenReturn(itemList);

    List<ItemRequestDto> result = requestService.getAll(userId);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(userService, times(1)).validateUserExist(userId);
    verify(requestRepository, times(1)).findByRequestorIdNotOrderByCreatedDesc(userId);
    verify(itemService, times(1)).getItemByRequestIds(List.of(requestId));
    verifyNoMoreInteractions(userService, requestRepository, itemService);
  }

  @Test
  void getAll_emptyList() {
    when(requestRepository.findByRequestorIdNotOrderByCreatedDesc(userId)).thenReturn(List.of());

    List<ItemRequestDto> result = requestService.getAll(userId);

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(userService, times(1)).validateUserExist(userId);
    verify(requestRepository, times(1)).findByRequestorIdNotOrderByCreatedDesc(userId);
    verifyNoMoreInteractions(userService, requestRepository, itemService);
  }

  @Test
  void getById_whenValidAndExistedInput_thenSuccess() {
    when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
    when(itemService.getItemByRequestIds(List.of(requestId))).thenReturn(itemList);

    ItemRequestDto result = requestService.getById(userId, requestId);

    assertNotNull(result);
    assertEquals(requestId, result.getId());
    verify(userService, times(1)).validateUserExist(userId);
    verify(requestRepository, times(1)).findById(requestId);
    verify(itemService, times(1)).getItemByRequestIds(List.of(requestId));
    verifyNoMoreInteractions(userService, requestRepository, itemService);
  }

  @Test
  void getById_notFound() {
    when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class, () -> {
      requestService.getById(userId, requestId);
    });
    assertEquals("ItemRequest not found.", exception.getMessage());
    verify(userService, times(1)).validateUserExist(userId);
    verify(requestRepository, times(1)).findById(requestId);
    verifyNoMoreInteractions(userService, requestRepository, itemService);
  }

  private ItemRequestDto buildRequestDto() {
    return RequestBuilder.buildRequestDto(
        requestId, "Description", userId, LocalDateTime.now());
  }

  private ItemRequest buildRequest(LocalDateTime created) {
    return RequestBuilder.buildItemRequest(
        requestDto.getDescription(), UserBuilder.buildUser(), created);
  }

  private User buildUser() {
    return UserBuilder.buildUser();
  }
}