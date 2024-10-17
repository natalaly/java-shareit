package ru.practicum.shareit.request;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.shareit.request.dto.ItemRequestDto;

/**
 * Rest Controller for managing item requests.
 * <p>
 * This class handles HTTP requests related to item requests, including creating,
 * retrieving, and listing item requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j

public class ItemRequestController {

  private static final String USER_ID_HEADER = "X-Sharer-User-Id";
  private final ItemRequestService requestService;

  @PostMapping
  public ResponseEntity<ItemRequestDto> createItemRequest(
      @RequestHeader(USER_ID_HEADER) Long userId,
      @RequestBody ItemRequestDto request) {
    log.info("Received request POST /requests from user with ID {} with description {}",
        userId, request);
    final ItemRequestDto requestSaved = requestService.saveRequest(userId, request);
    final URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(requestSaved.getId())
        .toUri();
    log.info("Item added successfully with ID {}", requestSaved.getId());
    return ResponseEntity.created(location).body(requestSaved);
  }

  @GetMapping
  public ResponseEntity<List<ItemRequestDto>> getOwnItemRequests(
      @RequestHeader(USER_ID_HEADER) Long userId) {
    log.info("Received request GET /requests from requestor with ID {}.", userId);
    final List<ItemRequestDto> requests = requestService.getUserItemRequests(userId);
    log.info("Returning {} item requests from user {} ", requests.size(), userId);
    return ResponseEntity.ok(requests);
  }

  @GetMapping("/all")
  public ResponseEntity<List<ItemRequestDto>> getAllItemRequests(
      @RequestHeader(USER_ID_HEADER) Long userId) {
    log.info("Received request GET /requests/all");
    final List<ItemRequestDto> allRequests = requestService.getAll(userId);
    log.info("Returning {} item requests.", allRequests.size());
    return ResponseEntity.ok(allRequests);
  }

  @GetMapping("/{requestId}")
  public ResponseEntity<ItemRequestDto> getItemRequestById(
      @RequestHeader(USER_ID_HEADER) long userId,
      @PathVariable("requestId") long requestId) {
    log.info("Received request from user ID {} GET /requests/{}.", userId, requestId);
    final ItemRequestDto request = requestService.getById(userId, requestId);
    log.info("Returning item request.");
    return ResponseEntity.ok(request);
  }
}
