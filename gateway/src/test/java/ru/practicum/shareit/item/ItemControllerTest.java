package ru.practicum.shareit.item;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utils.HeaderConstants;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

  @Autowired
  MockMvc mvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  ItemClient itemClient;

  @SneakyThrows
  @Test
  void addItem_whenValidInput_thenCreated() {
    Long userId = 1L;
    ItemDto itemDto = ItemDto.builder()
        .name("item").description("Description").available(true).build();

    when(itemClient.addItem(anyLong(), any(ItemDto.class)))
        .thenReturn(ResponseEntity.created(null).build());

    mvc.perform(post("/items")
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(itemDto)))
        .andExpect(status().isCreated());

    verify(itemClient, times(1)).addItem(eq(userId), any(ItemDto.class));
    verifyNoMoreInteractions(itemClient);
  }

  @SneakyThrows
  @Test
  void addItem_whenHeaderMissing_thenInternalServerError() {
    ItemDto itemDto = ItemDto.builder()
        .name("item").description("Description").available(true).build();

    mvc.perform(post("/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(itemDto)))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error").value("Internal server error."));

    verify(itemClient, never()).addItem(anyLong(), any(ItemDto.class));
  }

  @SneakyThrows
  @Test
  void addItem_whenMissingNameOrDescriptionOrAvailable_thenBadRequest() {
    Long userId = 1L;
    ItemDto itemDto = ItemDto.builder().build();

    Objects.requireNonNull(mvc.perform(post("/items")
                .header(HeaderConstants.USER_ID_HEADER, userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", containsInAnyOrder(
                "Name can not be blank.",
                "Available should be defined.",
                "Description can not be blank."
            ))));
    verify(itemClient, never()).addItem(anyLong(), any(ItemDto.class));
  }

  @SneakyThrows
  @Test
  void updateItem_whenValidInput_thenOk() {
    Long userId = 1L;
    Long itemId = 1L;
    ItemDto itemDto = ItemDto.builder()
        .name("item").description("Description").build();

    when(itemClient.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
        .thenReturn(ResponseEntity.ok().build());

    mvc.perform(patch("/items/{itemId}", itemId)
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(itemDto)))
        .andExpect(status().isOk());

    verify(itemClient, times(1)).updateItem(eq(userId), eq(itemId), any(ItemDto.class));
    verifyNoMoreInteractions(itemClient);
  }

  @SneakyThrows
  @Test
  void updateItem_whenBlankNameOrDescription_thenBadRequest() {
    Long userId = 1L;
    Long itemId = 1L;
    ItemDto itemDto = ItemDto.builder()
        .name("  ").description("          ").build();

    mvc.perform(patch("/items/{itemId}", itemId)
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(itemDto)))
        .andExpect(status().isBadRequest());

    verify(itemClient, never()).updateItem(eq(userId), eq(itemId), any(ItemDto.class));
  }

  @SneakyThrows
  @Test
  void getUserItems_whenValidInput_thenOk() {
    Long userId = 1L;

    when(itemClient.getUserItems(anyLong()))
        .thenReturn(ResponseEntity.ok(null));

    mvc.perform(get("/items")
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(itemClient, times(1)).getUserItems(eq(userId));
    verifyNoMoreInteractions(itemClient);
  }


  @SneakyThrows
  @Test
  void getItemById_whenValidUserId_thenOk() {
    Long userId = 1L;
    Long itemId = 1L;
    when(itemClient.getItemById(anyLong(), anyLong()))
        .thenReturn(ResponseEntity.ok().build());

    mvc.perform(get("/items/{itemId}", itemId)
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(itemClient, times(1))
        .getItemById(eq(itemId), eq(userId));
    verifyNoMoreInteractions(itemClient);
  }

  @SneakyThrows
  @Test
  void searchItem_whenValidInput_thenOk() {
    Long userId = 1L;
    String searchText = "Test";

    when(itemClient.search(anyLong(), anyString()))
        .thenReturn(ResponseEntity.ok().build());

    mvc.perform(get("/items/search")
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .param("text", searchText)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(itemClient, times(1))
        .search(eq(userId), eq(searchText));
    verifyNoMoreInteractions(itemClient);
  }

  @SneakyThrows
  @Test
  void addCommentToItem_whenValidInput_thenOk() {
    Long userId = 1L;
    Long itemId = 1L;
    CommentDto commentDto = CommentDto.builder().text("SomeText").build();
    when(itemClient.addComment(anyLong(), anyLong(), any(CommentDto.class)))
        .thenReturn(ResponseEntity.ok().build());

    mvc.perform(post("/items/{itemId}/comment", itemId)
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(commentDto)))
        .andExpect(status().isOk());

    verify(itemClient, times(1))
        .addComment(eq(userId), eq(itemId), any(CommentDto.class));
    verifyNoMoreInteractions(itemClient);
  }

}