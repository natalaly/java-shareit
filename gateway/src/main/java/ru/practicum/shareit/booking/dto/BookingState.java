package ru.practicum.shareit.booking.dto;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Enum representing the possible states of a booking query in the presentation layer.
 * <p>
 * This enum is used in the API layer to filter bookings based on their status or time period.
 * <p>
 * The available states are:
 * <ul>
 *   <li>{@link #CURRENT} — bookings that are currently active.</li>
 *   <li>{@link #PAST} — bookings that have already ended.</li>
 *   <li>{@link #FUTURE} — bookings that are scheduled to start in the future.</li>
 *   <li>{@link #WAITING} — bookings that are waiting for approval by the owner.</li>
 *   <li>{@link #REJECTED} — bookings that were rejected by the owner.</li>
 *   <li>{@link #ALL} — a query state representing all bookings, regardless of their current status.</li>
 * </ul>
 * <p>
 * This enum is used primarily for filtering booking queries in the API layer and differs
 * from {@code BookingStatus}, which reflects the actual state of a booking in the data model.
 */
@Getter
@Slf4j
public enum BookingState {

	ALL,
	CURRENT,
	FUTURE,
	PAST,
	REJECTED,
	WAITING;

	public static List<String> getValidStates() {
		return Arrays.stream(values())
				.map(Enum::name)
				.toList();
	}

	public static BookingState fromString(final String state) {
		for (BookingState bs : BookingState.values()) {
			if (bs.name().equalsIgnoreCase(state.trim())) {
				return bs;
			}
		}
		log.warn("Invalid state value: {}", state);
		throw new IllegalArgumentException("Invalid state value.");
	}
}
