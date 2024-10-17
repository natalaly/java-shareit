package ru.practicum.shareit.request;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.utils.HeaderConstants;
import ru.practicum.shareit.utils.RequestBuilder;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private ItemRequestService requestService;

  private ItemRequestDto requestDto;
  private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  @BeforeEach
  void setUp() {
    requestDto = buildRequestDto();
  }

  @SneakyThrows
  @Test
  void createItemRequest_whenValidaData_thenCreatedAndLocation() {
    Long userId = 1L;
    String created = requestDto.getCreated().format(formatter);

    when(requestService.saveRequest(anyLong(), any(ItemRequestDto.class)))
        .thenReturn(requestDto);

    mvc.perform(post("/requests")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(jsonPath("$.id").value(requestDto.getId()))
        .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
        .andExpect(jsonPath("$.requestorId").value(requestDto.getRequestorId()))
        .andExpect(jsonPath("$.created").value(created))
        .andExpect(jsonPath("$.items", hasSize(0)))
        .andExpect(header().string("Location", "http://localhost/requests/1"));

    verify(requestService, times(1))
        .saveRequest(eq(userId), any(ItemRequestDto.class));
    verifyNoMoreInteractions(requestService);
  }

  @SneakyThrows
  @Test
  void getOwnItemRequests_whenUserValid_thenOkReturnList() {
    Long userId = requestDto.getRequestorId();
    when(requestService.getUserItemRequests(anyLong()))
        .thenReturn(List.of(requestDto));

    mvc.perform(get("/requests")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HeaderConstants.USER_ID_HEADER, userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("[0].id").value(requestDto.getId()))
        .andExpect(jsonPath("$[0].description").value(requestDto.getDescription()))
        .andExpect(jsonPath("$[0].requestorId").value(userId))
        .andExpect(jsonPath("$[0].created")
            .value(requestDto.getCreated().format(formatter)))
        .andExpect(jsonPath("$[0].items", hasSize(0)));

    verify(requestService, times(1)).getUserItemRequests(anyLong());
    verifyNoMoreInteractions(requestService);
  }

  @SneakyThrows
  @Test
  void getAllItemRequests() {
    Long userId = requestDto.getRequestorId();
    when(requestService.getAll(anyLong()))
        .thenReturn(List.of(requestDto));

    mvc.perform(get("/requests/all")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HeaderConstants.USER_ID_HEADER, userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("[0].id").value(requestDto.getId()))
        .andExpect(jsonPath("$[0].description").value(requestDto.getDescription()))
        .andExpect(jsonPath("$[0].requestorId").value(userId))
        .andExpect(jsonPath("$[0].created")
            .value(requestDto.getCreated().format(formatter)))
        .andExpect(jsonPath("$[0].items", hasSize(0)));

    verify(requestService, times(1)).getAll(anyLong());
    verifyNoMoreInteractions(requestService);
  }

  @SneakyThrows
  @Test
  void getItemRequestById() {
    Long userId = requestDto.getRequestorId();
    Long requestId = requestDto.getId();

    when(requestService.getById(anyLong(), anyLong()))
        .thenReturn(requestDto);

    mvc.perform(get("/requests/{requestId}",requestId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HeaderConstants.USER_ID_HEADER, userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(requestDto.getId()))
        .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
        .andExpect(jsonPath("$.requestorId").value(userId))
        .andExpect(jsonPath("$.created")
            .value(requestDto.getCreated().format(formatter)))
        .andExpect(jsonPath("$.items", hasSize(0)));

    verify(requestService, times(1)).getById(anyLong(), anyLong());
    verifyNoMoreInteractions(requestService);
  }

  private ItemRequestDto buildRequestDto() {
    LocalDateTime created = LocalDateTime.now();
    return RequestBuilder.buildRequestDto(created);
  }
}