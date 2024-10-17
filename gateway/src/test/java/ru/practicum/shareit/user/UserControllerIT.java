package ru.practicum.shareit.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import ru.practicum.shareit.user.dto.UserDto;

@WebMvcTest(UserController.class)
class UserControllerIT {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserClient userClient;

  @SneakyThrows
  @Test
  void createUser_whenValidInput_thenCreated() {
    UserDto userDto = UserDto.builder()
        .name("User").email("user@test.com").build();

    when(userClient.createUser(any(UserDto.class)))
        .thenReturn(ResponseEntity.created(null).build());

    mvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isCreated());

    verify(userClient, times(1)).createUser(any(UserDto.class));
    verifyNoMoreInteractions(userClient);
  }

  @SneakyThrows
  @Test
  void createUser_whenNameIsMissing_thenBadRequest() {
    UserDto userDto = UserDto.builder()
        .email("user@test.com")
        .build();

    mvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Name can not be blank."));

    verify(userClient, never()).createUser(any(UserDto.class));
  }

  @SneakyThrows
  @Test
  void createUser_whenEmailIsMissing_thenBadRequest() {
    UserDto userDto = UserDto.builder()
        .name("User")
        .build();

    mvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Email can not be blank."));

    verify(userClient, never()).createUser(any(UserDto.class));
  }

  @SneakyThrows
  @Test
  void createUser_whenInvalidFormatOfEmail_thenBadRequest() {
    UserDto userDto = UserDto.builder()
        .name("User")
        .email("notEmail")
        .build();

    mvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Email should be correct format."));

    verify(userClient, never()).createUser(any(UserDto.class));
  }

  @SneakyThrows
  @Test
  void updateUser_whenValidInput_thenOk() {
    Long userId = 1L;
    UserDto userDto = UserDto.builder()
        .name("New Name")
        .build();

    when(userClient.updateUser(anyLong(), any(UserDto.class)))
        .thenReturn(ResponseEntity.ok().build());

    mvc.perform(patch("/users/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isOk());

    verify(userClient, times(1)).updateUser(userId, userDto);
    verifyNoMoreInteractions(userClient);
  }

  @SneakyThrows
  @Test
  void getAllUsers_whenValidInput_theOk() {

    when(userClient.getAllUsers()).thenReturn(ResponseEntity.ok().build());

    mvc.perform(get("/users")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(userClient, times(1)).getAllUsers();
    verifyNoMoreInteractions(userClient);
  }

  @SneakyThrows
  @Test
  void getUserById_whenValidInput_thenOk() {
    Long userId = 1L;

    when(userClient.getUserById(anyLong())).thenReturn(ResponseEntity.ok().build());

    mvc.perform(get("/users/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(userClient, times(1)).getUserById(userId);
    verifyNoMoreInteractions(userClient);
  }

  @SneakyThrows
  @Test
  void getUserById_whenNullUserId_thenNotFount() {
    Long userId = null;

    Objects.requireNonNull(mvc.perform(get("/users/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andReturn().getResponse().getErrorMessage()).equalsIgnoreCase("No static resource users.");

    verify(userClient, never()).getUserById(anyLong());
  }


  @SneakyThrows
  @Test
  void deleteUser_whenValidInput_thenOk() {
    Long userId = 1L;

    when(userClient.deleteUser(anyLong())).thenReturn(ResponseEntity.ok().build());

    mvc.perform(delete("/users/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(userClient, times(1)).deleteUser(userId);
    verifyNoMoreInteractions(userClient);
  }

  @SneakyThrows
  @Test
  void deleteUser_whenUserDontExist_thenNotFound() {
    Long userId = 1L;

    when(userClient.deleteUser(anyLong())).thenReturn(ResponseEntity.notFound().build());

    mvc.perform(delete("/users/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

    verify(userClient, times(1)).deleteUser(userId);
    verifyNoMoreInteractions(userClient);
  }

}