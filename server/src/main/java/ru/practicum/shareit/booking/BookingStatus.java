package ru.practicum.shareit.booking;

import lombok.Getter;

/**
 * Enum representing the different statuses a booking can have during its lifecycle.
 * <p>
 * The available statuses are:
 * <ul>
 *   <li>{@link #WAITING} — the booking has been created and is awaiting approval by the item owner.</li>
 *   <li>{@link #APPROVED} — the booking has been approved by the item owner and is confirmed.</li>
 *   <li>{@link #REJECTED} — the booking has been rejected by the item owner.</li>
 *   <li>{@link #CANCELED} — the booking has been canceled by the person who made the booking.</li>
 * </ul>
 */
@Getter
public enum BookingStatus {

  WAITING,
  APPROVED,
  REJECTED,
  CANCELED
}
