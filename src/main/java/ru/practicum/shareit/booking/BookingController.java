package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.ValidState;

/**
 * REST controller for managing bookings. This controller handles operations related to booking
 * creation, status updates, and fetching bookings for both users and item owners.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

  private static final String USER_ID_HEADER = "X-Sharer-User-Id";
  private final BookingService bookingService;

  @PostMapping
  public ResponseEntity<BookingResponseDto> createBooking(
      @RequestHeader(USER_ID_HEADER) Long userId,
      @Validated(Create.class) @RequestBody BookingDto bookingDto) {
    log.info("Received request POST /bookings for user with ID {} to create booking {}.",
        userId, bookingDto);
    final BookingResponseDto bookingCreated = bookingService.createBooking(userId, bookingDto);
    final URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(bookingCreated.getId())
        .toUri();
    log.info("Booking created successfully with ID {} and status {}.",
        bookingCreated.getId(), bookingCreated.getStatus());
    return ResponseEntity.created(location).body(bookingCreated);
  }

  @PatchMapping("/{bookingId}")
  public ResponseEntity<BookingResponseDto> updateBookingStatus(
      @RequestHeader(USER_ID_HEADER) Long ownerId,
      @PathVariable("bookingId") @NotNull @Positive Long bookingId,
      @RequestParam(name = "approved") @NotNull Boolean approved) {
    log.info("Received request PATCH /bookings/{bookingId}?approved={} "
        + "to update booking status by item owner ID {}.", approved, ownerId);
    final BookingResponseDto updatedBooking = bookingService.updateStatus(bookingId, ownerId,
        approved);
    log.info("Booking status updated successfully: {}", updatedBooking);
    return ResponseEntity.ok(updatedBooking);
  }

  @GetMapping("/{bookingId}")
  public ResponseEntity<BookingResponseDto> getBookingById(
      @RequestHeader(USER_ID_HEADER) Long userId,
      @PathVariable("bookingId") @NotNull @Positive Long bookingId) {
    log.info("Received GET /bookings/{} frim user ID {}.", bookingId, userId);
    final BookingResponseDto booking = bookingService.getBookingById(bookingId, userId);
    log.info("Returning booking data: {}", booking);
    return ResponseEntity.ok(booking);
  }

  @GetMapping
  public ResponseEntity<List<BookingResponseDto>> getAllBookingForUser(
      @RequestHeader(USER_ID_HEADER) Long userId,
      @RequestParam(value = "state", defaultValue = "All")
      @ValidState String state) {
    log.info("Received GET /bookings?state={} for booker ID {}.", state, userId);
    final List<BookingResponseDto> bookings = bookingService.getAllBookingForUser(userId, state);
    log.info("Returning {} bookings for user ID {}", bookings.size(), userId);
    return ResponseEntity.ok(bookings);
  }

  @GetMapping("/owner")
  public ResponseEntity<List<BookingResponseDto>> getAllBookingForOwner(
      @RequestHeader(USER_ID_HEADER) Long ownerId,
      @RequestParam(value = "state", defaultValue = "All")
      @ValidState String state) {
    log.info("GET /bookings/owner?state={} for owner ID {}.", state, ownerId);
    final List<BookingResponseDto> bookings = bookingService.getAllBookingForOwner(ownerId, state);
    log.info("Returning {} bookings for owner ID {}", bookings.size(), ownerId);
    return ResponseEntity.ok(bookings);
  }


}
