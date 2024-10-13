package ru.practicum.shareit.request;

import java.util.List;
import ru.practicum.shareit.request.dto.ItemRequestDto;

/**
 * Service interface for managing item requests in the application.
 * <p>
 * This interface defines methods to handle item requests such as creating new requests,
 * retrieving requests made by a user, retrieving all requests, and fetching details of
 * a specific request.
 * <p>
 * The following operations are provided:
 * <ul>
 *   <li>
 *     {@link #saveRequest(Long, ItemRequestDto)}: Saves a new item request made by a user.
 *   </li>
 *   <li>
 *     {@link #getUserItemReqiests(Long)}: Retrieves item requests made by a specific user.
 *   </li>
 *   <li>
 *     {@link #getAll(Long)}: Retrieves a list of item requests created by other users.
 *   </li>
 *   <li>
 *     {@link #getById(Long, Long)}: Retrieves a specific item request by its ID and validates the user.
 *   </li>
 * </ul>
 */
public interface ItemRequestService {

  ItemRequestDto saveRequest(Long userId, ItemRequestDto request);

  List<ItemRequestDto> getUserItemReqiests(Long requestorId);

  List<ItemRequestDto> getAll(Long userId);

  ItemRequestDto getById(Long userId, Long requestId);
}
