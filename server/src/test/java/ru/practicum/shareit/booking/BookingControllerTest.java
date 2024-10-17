package ru.practicum.shareit.booking;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.utils.BookingBuilder;
import ru.practicum.shareit.utils.HeaderConstants;
import ru.practicum.shareit.utils.UserBuilder;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

  @Autowired
  MockMvc mvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  BookingService bookingService;

  private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
  private BookingDto bookingDto;
  private BookingResponseDto bookingResponse;

  @BeforeEach
  void setUp() {
    LocalDateTime now = LocalDateTime.now();
    bookingDto = BookingBuilder.buildBookingDto(now);
    bookingResponse = BookingBuilder.buildBookingResponseDto(now);
  }

  @SneakyThrows
  @Test
  void createBooking_whenValidInput_thenCreatedReturnLocation() {
    Long userId = 1L;

    when(bookingService.createBooking(anyLong(), any(BookingDto.class)))
        .thenReturn(bookingResponse);

    mvc.perform(post("/bookings")
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookingDto)))
        .andExpect(status().isCreated())
        .andExpect(header()
            .string("Location", "http://localhost/bookings/" + bookingResponse.getId()))
        .andExpect(jsonPath("$.id").value(bookingResponse.getId()))
        .andExpect(jsonPath("$.item.id")
            .value(bookingResponse.getItem().getId()))
        .andExpect(jsonPath("$.start")
            .value(bookingResponse.getStart().format(formatter)))
        .andExpect(jsonPath("$.end")
            .value(bookingResponse.getEnd().format(formatter)))
        .andExpect(jsonPath("$.status").value("WAITING"));

    verify(bookingService, times(1))
        .createBooking(eq(userId), any(BookingDto.class));
    verifyNoMoreInteractions(bookingService);
  }

  @SneakyThrows
  @Test
  void updateBookingStatus_whenValidOwner_thenOkReturnUpdated() {
    Long ownerId = 1L;
    Long bookingId = 1L;
    Boolean approved = true;
    bookingResponse.setStatus(BookingStatus.APPROVED);

    when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean()))
        .thenReturn(bookingResponse);

    mvc.perform(patch("/bookings/{bookingId}", bookingId)
            .header(HeaderConstants.USER_ID_HEADER, ownerId)
            .param("approved", approved.toString())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(bookingResponse.getId()))
        .andExpect(jsonPath("$.item.id")
            .value(bookingResponse.getItem().getId()))
        .andExpect(jsonPath("$.status").value("APPROVED"));

    verify(bookingService, times(1))
        .updateStatus(eq(bookingId), eq(ownerId), eq(approved));
    verifyNoMoreInteractions(bookingService);
  }

  @SneakyThrows
  @Test
  void getBookingById_whenValidBookingId_thenReturnOkWithContent() {
    Long userId = 1L;
    Long bookingId = 1L;

    when(bookingService.getBookingById(anyLong(), anyLong()))
        .thenReturn(bookingResponse);

    mvc.perform(get("/bookings/{bookingId}", bookingId)
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(bookingResponse.getId()))
        .andExpect(jsonPath("$.item.id")
            .value(bookingResponse.getItem().getId()))
        .andExpect(jsonPath("$.start")
            .value(bookingResponse.getStart().format(formatter)))
        .andExpect(jsonPath("$.end")
            .value(bookingResponse.getEnd().format(formatter)))
        .andExpect(jsonPath("$.status")
            .value("WAITING"));

    verify(bookingService, times(1))
        .getBookingById(eq(bookingId), eq(userId));
    verifyNoMoreInteractions(bookingService);
  }

  @SneakyThrows
  @Test
  void getAllBookingForUser_whenUserIdAndStateAreValid_thenReturnOkAndList() {
    Long userId = 1L;
    String state = "WAITING";
    bookingResponse.setBooker(UserBuilder.buildUserDto().setId(userId));
    List<BookingResponseDto> bookings = List.of(bookingResponse);

    when(bookingService.getAllBookingForUser(anyLong(), anyString()))
        .thenReturn(bookings);

    mvc.perform(get("/bookings")
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .param("state", state)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(bookings.size()))
        .andExpect(jsonPath("$[0].id").value(bookingResponse.getId()))
        .andExpect(jsonPath("$[0].status").value("WAITING"))
        .andExpect(jsonPath("$[0].booker.id").value(userId))
        .andExpect(jsonPath("$[0].item.id")
            .value(bookingResponse.getItem().getId()))
        .andExpect(jsonPath("$[0].start")
            .value(bookingResponse.getStart().format(formatter)))
        .andExpect(jsonPath("$[0].end")
            .value(bookingResponse.getEnd().format(formatter)));

    verify(bookingService, times(1))
        .getAllBookingForUser(eq(userId), eq(state));
    verifyNoMoreInteractions(bookingService);
  }

  @SneakyThrows
  @Test
  void getAllBookingForOwner() {
    Long ownerId = 1L;
    String state = "ALL";
    bookingResponse.setBooker(UserBuilder.buildUserDto().setId(2L));

    List<BookingResponseDto> bookings = List.of(bookingResponse);

    when(bookingService.getAllBookingForOwner(anyLong(), anyString()))
        .thenReturn(bookings);

    mvc.perform(get("/bookings/owner")
            .header(HeaderConstants.USER_ID_HEADER, ownerId)
            .param("state", state)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()").value(bookings.size()))
        .andExpect(jsonPath("$[0].id").value(bookingResponse.getId()))
        .andExpect(jsonPath("$[0].status").value("WAITING"))
        .andExpect(jsonPath("$[0].booker.id").value(2L))
        .andExpect(jsonPath("$[0].item.id")
            .value(bookingResponse.getItem().getId()))
        .andExpect(jsonPath("$[0].start")
            .value(bookingResponse.getStart().format(formatter)))
        .andExpect(jsonPath("$[0].end")
            .value(bookingResponse.getEnd().format(formatter)));

    verify(bookingService, times(1))
        .getAllBookingForOwner(eq(ownerId), eq(state));
    verifyNoMoreInteractions(bookingService);
  }


}