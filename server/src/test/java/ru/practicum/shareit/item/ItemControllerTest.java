package ru.practicum.shareit.item;

import static org.mockito.ArgumentMatchers.any;
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
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utils.CommentBuilder;
import ru.practicum.shareit.utils.HeaderConstants;
import ru.practicum.shareit.utils.ItemBuilder;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

  @Autowired
  MockMvc mvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  ItemService itemService;
  private ItemDto itemDto;
  private final Long userId = 1L;
  private final Long itemId = 1L;

  @BeforeEach
  void setUp() {
    itemDto = ItemBuilder.buildItemDto(itemId);
  }

  @SneakyThrows
  @Test
  void addNewItem_whenValidInput_thenCreatedWithLocation() {
    when(itemService.saveItem(anyLong(), any(ItemDto.class)))
        .thenReturn(itemDto);

    mvc.perform(post("/items")
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(itemDto)))
        .andExpect(status().isCreated())
        .andExpect(header()
            .string("Location", "http://localhost/items/" + itemDto.getId()))
        .andExpect(jsonPath("$.id").value(itemDto.getId()))
        .andExpect(jsonPath("$.name").value(itemDto.getName()))
        .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
        .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));

    verify(itemService, times(1)).saveItem(eq(userId), any(ItemDto.class));
    verifyNoMoreInteractions(itemService);
  }

  @SneakyThrows
  @Test
  void updateItem_whenValidInput_thenOkWithContent() {
    when(itemService.updateItem(anyLong(), any(ItemDto.class), anyLong()))
        .thenReturn(itemDto);

    mvc.perform(patch("/items/{itemId}", itemId)
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(itemDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(itemDto.getId()))
        .andExpect(jsonPath("$.name").value(itemDto.getName()))
        .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
        .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));

    verify(itemService, times(1)).updateItem(eq(userId), any(ItemDto.class), eq(itemId));
    verifyNoMoreInteractions(itemService);
  }

  @SneakyThrows
  @Test
  void getAllItemFromUser_whenValidInput_thenOkAndItemList() {
    List<ItemDto> items = List.of(itemDto);

    when(itemService.getUserItems(anyLong()))
        .thenReturn(items);

    mvc.perform(get("/items")
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(items.size()))
        .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
        .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
        .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()));

    verify(itemService, times(1)).getUserItems(eq(userId));
    verifyNoMoreInteractions(itemService);
  }


  @SneakyThrows
  @Test
  void getItemById_whenValidUserId_thenOkAndItem() {
    when(itemService.getItemById(anyLong(), anyLong()))
        .thenReturn(itemDto);

    mvc.perform(get("/items/{itemId}", itemId)
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(itemDto.getId()))
        .andExpect(jsonPath("$.name").value(itemDto.getName()))
        .andExpect(jsonPath("$.description")
            .value(itemDto.getDescription()));

    verify(itemService, times(1))
        .getItemById(eq(itemId), eq(userId));
    verifyNoMoreInteractions(itemService);
  }

  @SneakyThrows
  @Test
  void searchItemByPartialText_whenValidInput_thenOkWithListOfItems() {
    List<ItemDto> itemsFound = List.of(itemDto);
    String searchText = "Test";

    when(itemService.searchItemsByPartialText(anyString())).thenReturn(itemsFound);

    mvc.perform(get("/items/search")
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .param("text", searchText)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(itemsFound.size()))
        .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
        .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
        .andExpect(jsonPath("$[0].description")
            .value(itemDto.getDescription()));
    ;

    verify(itemService, times(1))
        .searchItemsByPartialText(eq(searchText));
    verifyNoMoreInteractions(itemService);
  }

  @SneakyThrows
  @Test
  void addCommentToItem_whenValidInput_thenOkWithLocationAndContent() {
    CommentDto commentDto = CommentBuilder.buildCommentDto();
    when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
        .thenReturn(commentDto);

    mvc.perform(post("/items/{itemId}/comment", itemId)
            .header(HeaderConstants.USER_ID_HEADER, userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(commentDto)))
        .andExpect(status().isOk())
        .andExpect(header()
            .string("Location",
                String.format("http://localhost/items/%s/comment/%s", itemDto.getId(),
                    commentDto.getId())))
        .andExpect(jsonPath("$.id").value(commentDto.getId()))
        .andExpect(jsonPath("$.text").value(commentDto.getText()));

    verify(itemService, times(1))
        .addComment(eq(userId), eq(itemId), any(CommentDto.class));
    verifyNoMoreInteractions(itemService);
  }

}