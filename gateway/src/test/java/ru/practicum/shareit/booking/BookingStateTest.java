package ru.practicum.shareit.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.practicum.shareit.booking.dto.BookingState;

class BookingStateTest {

  @Test
  void getValidStates_ShouldReturnAllBookingStates() {

    List<String> validStates = BookingState.getValidStates();

    assertNotNull(validStates);
    assertEquals(6, validStates.size());
    assertTrue(validStates.contains("ALL"));
    assertTrue(validStates.contains("CURRENT"));
    assertTrue(validStates.contains("PAST"));
    assertTrue(validStates.contains("FUTURE"));
    assertTrue(validStates.contains("WAITING"));
    assertTrue(validStates.contains("REJECTED"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED"})
  void fromString_ValidState_ShouldReturnCorrectEnumValue(String state) {

    BookingState result = BookingState.fromString(state);

    assertNotNull(result);
    assertEquals(state.toUpperCase(), result.name());
  }

  @ParameterizedTest
  @ValueSource(strings = {"  all  ", " CuRrEnt", "PAsT", "future", "wAiTinG", "reJECTed"})
  void fromString_ValidStateWithDifferentCaseAndWhitespace_ShouldReturnCorrectEnumValue(
      String state) {

    BookingState result = BookingState.fromString(state);

    assertNotNull(result);
    assertEquals(state.trim().toUpperCase(), result.name());
  }

  @ParameterizedTest
  @ValueSource(strings = {"INVALID", "UNKNOWN", "abc", " "})
  void fromString_InvalidState_ShouldThrowIllegalArgumentException(String state) {

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        BookingState.fromString(state));
    assertEquals("Invalid state value.", exception.getMessage());
  }

  @Test
  void fromString_NullState_ShouldThrowNullPointerExceptionException() {

    NullPointerException exception = assertThrows(NullPointerException.class, () ->
        BookingState.fromString(null));
    assertEquals("Cannot invoke \"String.trim()\" because \"state\" is null",
        exception.getMessage());
  }
}