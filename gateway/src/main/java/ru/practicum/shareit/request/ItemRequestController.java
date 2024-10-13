package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.utils.HeaderConstants;

/**
 * A Controller for handling item request-related operations for the ShareIt application.
 * <p>
 * This controller provides endpoints for creating, retrieving, and managing item requests. It
 * utilizes the {@link ItemRequestClient} to communicate with the ShareIt server.
 */
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

  private static final String USER_ID_HEADER = HeaderConstants.USER_ID_HEADER;
  private final ItemRequestClient requestClient;

  @PostMapping
  public ResponseEntity<Object> createItemRequest(
      @RequestHeader(USER_ID_HEADER) Long userId,
      @Validated @RequestBody ItemRequestDto requestDto) {
    log.info("Received request POST /requests from user with ID {} with description {}",
        userId, requestDto);
    return requestClient.createRequest(userId, requestDto);
  }

  @GetMapping
  public ResponseEntity<Object> getOwnItemRequests(
      @RequestHeader(USER_ID_HEADER) Long userId) {
    log.info("Received request GET /requests from requestor with ID {}.", userId);
    return requestClient.getOwnItemRequests(userId);
  }

  @GetMapping("/all")
  public ResponseEntity<Object> getAllItemRequests(
      @RequestHeader(USER_ID_HEADER) Long userId) {
    log.info("Received request GET /requests/all");
    return requestClient.getAllItemRequests(userId);
  }

  @GetMapping("/{requestId}")
  public ResponseEntity<Object> getItemRequestById(
      @RequestHeader(USER_ID_HEADER) long userId,
      @PathVariable("requestId") @NotNull @Positive long requestId) {
    log.info("Received request from user ID {} GET /requests/{}.", userId, requestId);
    return requestClient.getItemRequestById(userId, requestId);
  }
}
