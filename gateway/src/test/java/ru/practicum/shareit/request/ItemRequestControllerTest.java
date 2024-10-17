package ru.practicum.shareit.request;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.utils.HeaderConstants;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private ItemRequestClient requestClient;

  @SneakyThrows
  @Test
  void createItemRequest_whenValidaData_thenCreated() {
    Long userId = 1L;
    ItemRequestDto request = new ItemRequestDto().setDescription("Item To Use");

    when(requestClient.createRequest(anyLong(), any(ItemRequestDto.class)))
        .thenReturn(ResponseEntity.created(null).build());

    mvc.perform(post("/requests")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

    verify(requestClient, times(1))
        .createRequest(eq(userId), any(ItemRequestDto.class));
    verifyNoMoreInteractions(requestClient);
  }

  @SneakyThrows
  @Test
  void createItemRequest_whenMissingDescription_thenBadRequest() {
    Long userId = 1L;
    ItemRequestDto request = new ItemRequestDto().setDescription(null);

    mvc.perform(post("/requests")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());

    verify(requestClient, never())
        .createRequest(anyLong(), any(ItemRequestDto.class));
  }


  @SneakyThrows
  @Test
  void getOwnItemRequests_whenValidInput_thenOk() {
    Long userId = 1L;
    when(requestClient.getOwnItemRequests(anyLong()))
        .thenReturn(ResponseEntity.ok().build());

    mvc.perform(get("/requests")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HeaderConstants.USER_ID_HEADER, userId))
        .andExpect(status().isOk());

    verify(requestClient, times(1)).getOwnItemRequests(anyLong());
    verifyNoMoreInteractions(requestClient);
  }

  @SneakyThrows
  @Test
  void getAllItemRequests() {
    Long userId = 1L;
    when(requestClient.getAllItemRequests(anyLong()))
        .thenReturn(ResponseEntity.ok().build());

    mvc.perform(get("/requests/all")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HeaderConstants.USER_ID_HEADER, userId))
        .andExpect(status().isOk());

    verify(requestClient, times(1)).getAllItemRequests(anyLong());
    verifyNoMoreInteractions(requestClient);
  }

  @SneakyThrows
  @Test
  void getItemRequestById() {
    Long userId = 1L;
    Long requestId = 2L;

    when(requestClient.getItemRequestById(anyLong(), anyLong()))
        .thenReturn(ResponseEntity.ok().build());

    mvc.perform(get("/requests/{requestId}", requestId)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HeaderConstants.USER_ID_HEADER, userId))
        .andExpect(status().isOk());

    verify(requestClient, times(1)).getItemRequestById(anyLong(), anyLong());
    verifyNoMoreInteractions(requestClient);
  }


}