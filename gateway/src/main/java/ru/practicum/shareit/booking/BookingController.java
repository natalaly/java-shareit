package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.utils.HeaderConstants;
import ru.practicum.shareit.validation.ValidState;

/**
 * Controller for handling booking-related requests. Manages creation, status update, and retrieval
 * of bookings for users and owners.
 */
@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

  private static final String USER_ID_HEADER = HeaderConstants.USER_ID_HEADER;

  private final BookingClient bookingClient;

  @PostMapping
  public ResponseEntity<Object> bookItem(
      @RequestHeader(USER_ID_HEADER) long userId,
      @Validated @RequestBody BookingDto requestDto) {
    log.info("Received request POST /bookings for user with ID {} to create booking {}.",
        userId, requestDto);
    return bookingClient.bookItem(userId, requestDto);
  }

  @PatchMapping("/{bookingId}")
  public ResponseEntity<Object> updateStatus(
      @RequestHeader(USER_ID_HEADER) Long ownerId,
      @PathVariable("bookingId") @NotNull @Positive Long bookingId,
      @RequestParam(name = "approved") @NotNull Boolean approved) {
    log.info("Received request PATCH /bookings/{}?approved={} "
        + "to update booking status by item owner ID {}.", bookingId, approved, ownerId);
    return bookingClient.updateStatus(ownerId, bookingId, approved);
  }

  @GetMapping("/{bookingId}")
  public ResponseEntity<Object> getBooking(
      @RequestHeader(USER_ID_HEADER) long userId,
      @PathVariable @NotNull @Positive Long bookingId) {
    log.info("Get booking {}, userId={}", bookingId, userId);
    return bookingClient.getBooking(userId, bookingId);
  }

  @GetMapping
  public ResponseEntity<Object> getBookings(
      @RequestHeader(USER_ID_HEADER) long userId,
      @RequestParam(name = "state", defaultValue = "all") @ValidState String stateParam) {
    log.info("Get booking with state {}, userId={}.", stateParam, userId);
    return bookingClient.getBookings(userId, stateParam);
  }

  @GetMapping("/owner")
  public ResponseEntity<Object> getBookingsForOwner(
      @RequestHeader(USER_ID_HEADER) Long ownerId,
      @RequestParam(value = "state", defaultValue = "All") @ValidState String state) {
    log.info("GET /bookings/owner?state={} for owner ID {}.", state, ownerId);
    return bookingClient.getBookingsForOwner(ownerId, state);
  }

}
