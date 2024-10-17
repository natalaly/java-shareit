package ru.practicum.shareit.user;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.UserBuilder;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserService userService;

  @SneakyThrows
  @Test
  void createUser() {
    UserDto userDto = buildUserDto();
    User savedUser = buildUser();

    when(userService.createNewUser(any(UserDto.class)))
        .thenReturn(UserMapper.mapToUserDto(savedUser));

    mvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(savedUser.getId()))
        .andExpect(jsonPath("$.email").value(savedUser.getEmail()))
        .andExpect(header()
            .string("Location", "http://localhost/users/" + savedUser.getId()));

    verify(userService, times(1)).createNewUser(userDto);
    verifyNoMoreInteractions(userService);
  }

  @SneakyThrows
  @Test
  void createUser_whenDuplicatedEmail_thenConflict() {
    UserDto userDto = buildUserDto();

    when(userService.createNewUser(any(UserDto.class)))
        .thenThrow(new DuplicatedDataException("Email already exists."));

    mvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error").value("Email already exists."));

    verify(userService, times(1)).createNewUser(userDto);
    verifyNoMoreInteractions(userService);
  }


  @SneakyThrows
  @Test
  void updateUser() {
    Long userId = 1L;
    UserDto userDto = buildUserDtoUpdated();

    when(userService.updateUser(any(UserDto.class), anyLong()))
        .thenReturn(userDto.setId(userId));

    mvc.perform(patch("/users/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId))
        .andExpect(jsonPath("$.email").value(userDto.getEmail()))
        .andExpect(jsonPath("$.name").value(userDto.getName()));

    verify(userService, times(1)).updateUser(userDto, userId);
    verifyNoMoreInteractions(userService);
  }

  @SneakyThrows
  @Test
  void getAllUsers() {
    List<UserDto> users = List.of(buildUserDto().setId(1L));

    when(userService.getAllUsers()).thenReturn(users);

    mvc.perform(get("/users")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].email").value(users.getFirst().getEmail()))
        .andExpect(jsonPath("$[0].name").value(users.getFirst().getName()));

    verify(userService, times(1)).getAllUsers();
    verifyNoMoreInteractions(userService);
  }

  @SneakyThrows
  @Test
  void getUserById() {
    Long userId = 1L;
    UserDto userDto = buildUserDto();

    when(userService.getUserById(userId)).thenReturn(userDto.setId(userId));

    mvc.perform(get("/users/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId))
        .andExpect(jsonPath("$.email").value(userDto.getEmail()))
        .andExpect(jsonPath("$.name").value(userDto.getName()));

    verify(userService, times(1)).getUserById(userId);
    verifyNoMoreInteractions(userService);
  }

  @SneakyThrows
  @Test
  void getUserById_whenUserNotFound_thenThrow() {
    Long userId = 1L;

    when(userService.getUserById(userId))
        .thenThrow(new NotFoundException("User not found."));

    mvc.perform(get("/users/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("User not found."));

    verify(userService, times(1)).getUserById(userId);
    verifyNoMoreInteractions(userService);
  }

  @SneakyThrows
  @Test
  void deleteUser() {
    Long userId = 1L;

    doNothing().when(userService).deleteUserById(anyLong());

    mvc.perform(delete("/users/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    verify(userService, times(1)).deleteUserById(userId);
    verifyNoMoreInteractions(userService);
  }

  @SneakyThrows
  @Test
  void deleteUser_whenUserNotExist_thenThrow() {
    Long userId = 1L;

    doThrow(new NotFoundException("User not found."))
        .when(userService).deleteUserById(anyLong());

    mvc.perform(delete("/users/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("User not found."));

    verify(userService, times(1)).deleteUserById(userId);
    verifyNoMoreInteractions(userService);
  }

  private UserDto buildUserDto() {
    return UserBuilder.buildUserDto();
  }

  private UserDto buildUserDtoUpdated() {
    return UserBuilder.buildUserDtoUpdated();
  }

  private User buildUser() {
    return UserBuilder.buildUser();
  }

}