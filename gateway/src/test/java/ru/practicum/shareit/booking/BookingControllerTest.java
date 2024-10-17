package ru.practicum.shareit.booking;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.time.LocalDateTime;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.utils.HeaderConstants;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

  @Autowired
  MockMvc mvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  BookingClient client;

  @SneakyThrows
  @Test
  void bookItem_whenValidInput_thenCreated() {
    Long userId = 1L;
    Long itemId = 1L;
    BookingDto bookingDto = BookingDto.builder()
        .itemId(itemId)
        .start(LocalDateTime.now().plusDays(1))
        .end(LocalDateTime.now().plusDays(2)).build();

    when(client.bookItem(anyLong(), any(BookingDto.class)))
        .thenReturn(ResponseEntity.created(URI.create("created")).body("created"));

    mvc.perform(post("/bookings")
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookingDto)))
        .andExpect(status().isCreated())
        .andExpect(content().string("created"));;

    verify(client, times(1))
        .bookItem(eq(userId), any(BookingDto.class));
    verifyNoMoreInteractions(client);
  }

  @SneakyThrows
  @Test
  void bookItem_whenStartAfterEnd_thenBadRequest() {
    Long userId = 1L;

    BookingDto invalidBookingDto = BookingDto.builder()
        .itemId(1L)
        .start(LocalDateTime.now().plusDays(3))  // Invalid: Start after end
        .end(LocalDateTime.now().plusDays(2))
        .build();

    mvc.perform(post("/bookings")
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidBookingDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Invalid date. End date should be after start date."));

    verify(client, never()).bookItem(anyLong(), any(BookingDto.class));
  }

  @SneakyThrows
  @Test
  void bookItem_whenMissingStartAndEnd_thenBadRequest() {
    Long userId = 1L;
    BookingDto invalidBookingDto = BookingDto.builder()
        .itemId(1L)
        .build();

    mvc.perform(post("/bookings")
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidBookingDto)))
        .andExpect(status().isBadRequest())
        .andDo(print());

    verify(client, times(0)).bookItem(anyLong(), any(BookingDto.class));
  }

  @SneakyThrows
  @Test
  void bookItem_whenStartIsInThePast_thenBadRequest() {
    Long userId = 1L;
    BookingDto invalidBookingDto = BookingDto.builder()
        .itemId(1L)
        .start(LocalDateTime.now().minusDays(1))
        .end(LocalDateTime.now().plusDays(1))
        .build();

    mvc.perform(post("/bookings")
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidBookingDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error")
            .value("Invalid date.Start date must not be in the past."));

    verify(client, times(0)).bookItem(anyLong(), any(BookingDto.class));

  }

  @SneakyThrows
  @Test
  void updateStatus_whenValidOwner_thenOkReturnUpdated() {
    Long ownerId = 1L;
    Long bookingId = 1L;
    Boolean approved = true;

    when(client.updateStatus(anyLong(), anyLong(), anyBoolean()))
        .thenReturn(ResponseEntity.ok().build());

    mvc.perform(patch("/bookings/{bookingId}", bookingId)
            .header(HeaderConstants.USER_ID_HEADER, ownerId)
            .param("approved", approved.toString())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(client, times(1))
        .updateStatus(eq(bookingId), eq(ownerId), eq(approved));
    verifyNoMoreInteractions(client);
  }

  @SneakyThrows
  @Test
  void updateStatus_whenMissingApproved_thenInternalServerError() {
    Long ownerId = 1L;
    Long bookingId = 1L;

    mvc.perform(patch("/bookings/{bookingId}", bookingId)
            .header(HeaderConstants.USER_ID_HEADER, ownerId)
            .contentType(MediaType.APPLICATION_JSON)) // Missing approved parameter
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error").value("Internal server error."));

    verify(client, never()).updateStatus(anyLong(), anyLong(), anyBoolean());
  }

  @SneakyThrows
  @Test
  void getBooking_whenValidInput_thenOk() {
    Long userId = 1L;
    Long bookingId = 1L;

    when(client.getBooking(anyLong(), anyLong()))
        .thenReturn(ResponseEntity.ok().build());

    mvc.perform(get("/bookings/{bookingId}", bookingId)
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(client, times(1))
        .getBooking(eq(bookingId), eq(userId));
    verifyNoMoreInteractions(client);
  }

  @SneakyThrows
  @Test
  void getBookings_whenValidInput_thenOk() {
    Long userId = 1L;
    String state = "WAITING";

    when(client.getBookings(anyLong(), anyString()))
        .thenReturn(ResponseEntity.ok().build());

    mvc.perform(get("/bookings")
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .param("state", state)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(client, times(1))
        .getBookings(eq(userId), eq(state));
    verifyNoMoreInteractions(client);
  }

  @SneakyThrows
  @Test
  void getBookings_whenMissingStateParam_thenDefaultAll_andOk() {
    Long userId = 1L;
    String defaultState = "all";

    when(client.getBookings(anyLong(), anyString()))
        .thenReturn(ResponseEntity.ok().build());

    mvc.perform(get("/bookings")
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(client, times(1)).getBookings(eq(userId), eq(defaultState));
    verifyNoMoreInteractions(client);
  }

  @SneakyThrows
  @Test
  void getBookings_whenInvalidState_thenInternalServerError() {
    Long userId = 1L;
    String state = "hello";

    mvc.perform(get("/bookings")
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .param("state", state)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error")
            .value("Unknown state: hello"));

    verify(client, never())
        .getBookings(anyLong(), anyString());
  }

  @SneakyThrows
  @Test
  void getBookingsForOwner_whenValidInput_thenOk() {
    Long ownerId = 1L;
    String state = "FUTURE";

    when(client.getBookingsForOwner(anyLong(), anyString()))
        .thenReturn(ResponseEntity.ok().build());

    mvc.perform(get("/bookings/owner")
            .header(HeaderConstants.USER_ID_HEADER, ownerId)
            .param("state", state)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(client, times(1))
        .getBookingsForOwner(eq(ownerId), eq(state));
    verifyNoMoreInteractions(client);
  }

  @SneakyThrows
  @Test
  void getBookingsForOwner_whenMissingStateParam_thenDefaultAll_andOk() {
    Long userId = 1L;
    String defaultState = "All";

    when(client.getBookingsForOwner(anyLong(), anyString()))
        .thenReturn(ResponseEntity.ok().build());

    mvc.perform(get("/bookings/owner")
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(client, times(1))
        .getBookingsForOwner(eq(userId), eq(defaultState));
    verifyNoMoreInteractions(client);
  }

  @SneakyThrows
  @Test
  void getBookingsForOwner_whenNullStateParam_thenDefaultAll_andOk() {
    Long userId = 1L;
    String defaultState = "All";
    String state = "";

    when(client.getBookingsForOwner(anyLong(), anyString()))
        .thenReturn(ResponseEntity.ok().build());

    mvc.perform(get("/bookings/owner")
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .param("state",state)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(client, times(1))
        .getBookingsForOwner(eq(userId), eq(defaultState));
    verifyNoMoreInteractions(client);
  }


}