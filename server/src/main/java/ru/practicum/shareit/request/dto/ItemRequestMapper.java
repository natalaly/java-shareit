package ru.practicum.shareit.request.dto;

import java.util.List;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * Utility class for mapping between ItemRequest and ItemRequestDto objects.
 * <p>
 * This class provides methods to convert between the data transfer object (DTO) representation of
 * item requests and the entity representation.
 * <ul>
 *     <li>{@link #mapToItemRequest(ItemRequestDto, User)}: Maps an ItemRequestDto to an ItemRequest entity.</li>
 *     <li>{@link #mapToItemRequestDto(ItemRequest)}: Maps an ItemRequest entity to an ItemRequestDto without items.</li>
 *     <li>{@link #mapToItemRequestDto(ItemRequest, List)}: Maps an ItemRequest entity to an ItemRequestDto, including associated items.</li>
 * </ul>
 * @see ItemRequest
 * @see ItemRequestDto
 * @see User
 * @see ItemDto
 */
@UtilityClass
@Slf4j
public class ItemRequestMapper {

  public static ItemRequest mapToItemRequest(final ItemRequestDto request, final User requestor) {
    log.debug("Mapping ItemRequestDto {} from user {} to ItemRequest.", request, requestor);
    Objects.requireNonNull(request);
    Objects.requireNonNull(requestor);

    return new ItemRequest()
        .setId(request.getId())
        .setDescription(request.getDescription())
        .setRequestor(requestor);
  }

  public static ItemRequestDto mapToItemRequestDto(final ItemRequest request) {
    return mapToItemRequestDto(request, null);
  }

  public static ItemRequestDto mapToItemRequestDto(final ItemRequest request,
                                                   final List<ItemDto> items) {
    log.debug("Mapping ItemRequest {} to ItemRequestDto.", request);

    final ItemRequestDto dto = new ItemRequestDto()
        .setId(request.getId())
        .setDescription(request.getDescription())
        .setRequestorId(request.getRequestor().getId())
        .setCreated(request.getCreated());

    if (items != null) {
      dto.setItems(items);
    }
    return dto;
  }
}
