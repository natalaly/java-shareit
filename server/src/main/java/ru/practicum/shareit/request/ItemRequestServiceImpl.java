package ru.practicum.shareit.request;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

/**
 * Service implementation class for managing item request related operations.
 *
 * @see ItemRequest
 * @see ItemRequestDto
 * @see ItemRequestRepository
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

  private final ItemRequestRepository requestRepository;
  private final UserService userService;
  private final ItemService itemService;


  @Override
  @Transactional
  public ItemRequestDto saveRequest(final Long userId, final ItemRequestDto request) {
    log.debug("Persisting a new request with data: {} from user with ID {}.", request, userId);

    final User requestor = userService.getByIdOrThrow(userId);

    final ItemRequest requestToSave = ItemRequestMapper.mapToItemRequest(request, requestor);

    return ItemRequestMapper.mapToItemRequestDto(requestRepository.save(requestToSave));
  }

  @Override
  public List<ItemRequestDto> getUserItemReqiests(final Long requestorId) {
    log.debug("Retrieving item requests for the requestor ID = {}.", requestorId);
    validateUser(requestorId);

    final List<ItemRequest> ownerRequests =
        requestRepository.findByRequestorIdOrderByCreatedDesc(requestorId);
    if (ownerRequests.isEmpty()) {
      return Collections.emptyList();
    }

    final List<Long> requestIds = ownerRequests.stream().map(ItemRequest::getId).toList();

    final Map<Long, List<ItemDto>> requestItems = itemService.getItemByRequestIds(requestIds)
        .stream()
        .collect(Collectors.groupingBy(ItemDto::getRequestId));

    return ownerRequests.stream()
        .map(request ->
            ItemRequestMapper.mapToItemRequestDto(request, requestItems.get(request.getId())))
        .toList();
  }

  @Override
  public List<ItemRequestDto> getAll(final Long userId) {
    log.debug("Retrieving all item requests existed in DB.");
    validateUser(userId);

    final List<ItemRequest> allRequests =
        requestRepository.findByRequestorIdNotOrderByCreatedDesc(userId);
    if (allRequests.isEmpty()) {
      return Collections.emptyList();
    }
    final List<Long> requestIds = allRequests.stream().map(ItemRequest::getId).toList();

    final Map<Long, List<ItemDto>> requestItems = itemService.getItemByRequestIds(requestIds)
        .stream()
        .collect(Collectors.groupingBy(ItemDto::getRequestId));

    return allRequests.stream()
        .map(request ->
            ItemRequestMapper.mapToItemRequestDto(request, requestItems.get(request.getId())))
        .toList();
  }

  @Override
  public ItemRequestDto getById(Long userId, Long requestId) {
    log.debug("Retrieving item requests with ID {}.", requestId);
    validateUser(userId);

    final ItemRequest request = getItemRequestOrThrow(requestId);
    final List<ItemDto> itemsRelated = itemService.getItemByRequestIds(List.of(requestId));

    return ItemRequestMapper.mapToItemRequestDto(request, itemsRelated);
  }

  private ItemRequest getItemRequestOrThrow(final Long requestId) {
    return requestRepository.findById(requestId)
        .orElseThrow(() -> {
          log.warn("Fail to get item request with ID {} from DB.", requestId);
          return new NotFoundException("ItemRequest not found.");
        });
  }

  private void validateUser(final Long userId) {
    userService.validateUserExist(userId);
  }
}
