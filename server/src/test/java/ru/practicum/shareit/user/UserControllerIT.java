package ru.practicum.shareit.user;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
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
import java.util.Optional;
import lombok.SneakyThrows;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.UserBuilder;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserRepository userRepository;

  @SneakyThrows
  @Test
  void createUser() {
    UserDto userDto = buildUserDto();
    User savedUser = buildUser();

    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(savedUser.getId()))
        .andExpect(jsonPath("$.email").value(savedUser.getEmail()))
        .andExpect(header()
            .string("Location", "http://localhost/users/" + savedUser.getId()));

    verify(userRepository, times(1)).save(any(User.class));
    verifyNoMoreInteractions(userRepository);
  }

  @SneakyThrows
  @Test
  void createUser_whenDuplicatedEmail_thenConflict() {
    UserDto userDto = buildUserDto();

    DataIntegrityViolationException dataIntegrityViolationException =
        new DataIntegrityViolationException("Duplicate email", new ConstraintViolationException(
            "Duplicate entry for email", null, "uq_user_email"));

    when(userRepository.save(any(User.class)))
        .thenThrow(dataIntegrityViolationException);

    mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error").value("Email already exists."));

    verify(userRepository, times(1)).save(any(User.class));
    verifyNoMoreInteractions(userRepository);
  }

  @SneakyThrows
  @Test
  void createUser_whenViolatingOtherBdConstraints_thenInternalServer() {
    UserDto userDto = buildUserDto();

    DataIntegrityViolationException dataIntegrityViolationException =
        new DataIntegrityViolationException("", new ConstraintViolationException("", null, null));

    when(userRepository.save(any(User.class)))
        .thenThrow(dataIntegrityViolationException);

    mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isInternalServerError());

    verify(userRepository, times(1)).save(any(User.class));
    verifyNoMoreInteractions(userRepository);
  }

  @SneakyThrows
  @Test
  void updateUser() {
    Long userId = 1L;
    UserDto userDto = buildUserDtoUpdated();
    User existingUser = buildUser();
    User updatedUser = buildUser();
    updatedUser.setEmail("new_email@example.com");
    updatedUser.setName("Updated User");

    when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
    when(userRepository.save(any(User.class))).thenReturn(updatedUser);

    mockMvc.perform(patch("/users/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId))
        .andExpect(jsonPath("$.email").value(updatedUser.getEmail()))
        .andExpect(jsonPath("$.name").value(updatedUser.getName()));

    verify(userRepository, times(1)).findById(userId);
    verify(userRepository, times(1)).save(any(User.class));
    verifyNoMoreInteractions(userRepository);
  }

  @SneakyThrows
  @Test
  void getAllUsers() {
    List<User> users = List.of(buildUser());

    when(userRepository.findAll()).thenReturn(users);

    mockMvc.perform(get("/users")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].email").value(users.getFirst().getEmail()))
        .andExpect(jsonPath("$[0].name").value(users.getFirst().getName()));

    verify(userRepository, times(1)).findAll();
    verifyNoMoreInteractions(userRepository);
  }

  @SneakyThrows
  @Test
  void getUserById() {
    Long userId = 1L;
    User user = buildUser();

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    mockMvc.perform(get("/users/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId))
        .andExpect(jsonPath("$.email").value(user.getEmail()))
        .andExpect(jsonPath("$.name").value(user.getName()));

    verify(userRepository, times(1)).findById(userId);
    verifyNoMoreInteractions(userRepository);
  }

  @SneakyThrows
  @Test
  void getUserById_whenUserNotFound_thenThrow() {
    Long userId = 1L;

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    mockMvc.perform(get("/users/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("User not found."));
    ;

    verify(userRepository, times(1)).findById(userId);
    verifyNoMoreInteractions(userRepository);
  }

  @SneakyThrows
  @Test
  void deleteUser() {
    Long userId = 1L;

    when(userRepository.existsById(userId)).thenReturn(true);

    mockMvc.perform(delete("/users/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    verify(userRepository, times(1)).existsById(userId);
    verify(userRepository, times(1)).deleteById(userId);
    verifyNoMoreInteractions(userRepository);
  }

  @SneakyThrows
  @Test
  void deleteUser_whenUserNotExist_thenThrow404() {
    Long userId = 1L;

    when(userRepository.existsById(userId)).thenReturn(false);

    mockMvc.perform(delete("/users/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("User not found."));

    verify(userRepository, times(1)).existsById(userId);
    verifyNoMoreInteractions(userRepository);
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