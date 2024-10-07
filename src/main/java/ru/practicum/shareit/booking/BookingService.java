package ru.practicum.shareit.booking;

import java.util.List;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

/**
 * A Service interface that handles business logic related to bookings.
 * <p> This service provides methods to create, update, and retrieve bookings for both bookers
 * and owners.
 * <ul>
 *   <li> {@link #createBooking(Long, BookingDto)}: Creates a new booking for a user.</li>
 *   <li> {@link #updateStatus(Long, Long, Boolean)}: Updates the status of a booking
 *        (approved/rejected) by the owner.</li>
 *   <li> {@link #getBookingById(Long, Long)}: Retrieves a booking by its ID for either the owner or the booker.</li>
 *   <li> {@link #getAllBookingForUser(Long, String)}: Retrieves all bookings for a specific user (booker) by {@link BookingState}.</li>
 *   <li> {@link #getAllBookingForOwner(Long, String)}: Retrieves all bookings for a specific owner with ability to filter by values of the {@link BookingState}.</li>
 * </ul>
 *
 * @see Booking
 * @see BookingController
 * @see BookingRepository
 * @see BookingState
 * @see BookingStatus
 *
 */
public interface BookingService {

  BookingResponseDto createBooking(Long userId, BookingDto bookingDto);

  BookingResponseDto updateStatus(Long id, Long ownerId, Boolean approved);

  BookingResponseDto getBookingById(Long bookingId, Long userId);

  List<BookingResponseDto> getAllBookingForUser(Long bookerId, String state);

  List<BookingResponseDto> getAllBookingForOwner(Long ownerId, String state);
}
