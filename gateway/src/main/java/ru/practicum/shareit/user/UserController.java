package ru.practicum.shareit.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

/**
 * The UserController handles incoming HTTP requests related to user management. It forwards
 * requests to the {@link UserClient} for further processing.
 * <p>
 * This controller performs validation on user input before passing it to the backend services.
 */

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

  private final UserClient userClient;

  @PostMapping
  public ResponseEntity<Object> createUser(@Validated(Create.class) @RequestBody UserDto userDto) {
    log.info("Received request POST /users");
    return userClient.createUser(userDto);
  }

  @PatchMapping("/{userId}")
  public ResponseEntity<Object> updateUser(
      @Validated(Update.class) @RequestBody UserDto userDto,
      @PathVariable("userId") @NotNull @Positive Long userId) {
    log.info("Received request PATCH /users/{} to update with data: {}", userId, userDto);
    return userClient.updateUser(userId, userDto);
  }

  @GetMapping
  public ResponseEntity<Object> getAllUsers() {
    log.info("Received request GET /users ");
    return userClient.getAllUsers();
  }

  @GetMapping("/{userId}")
  public ResponseEntity<Object> getUserById(
      @PathVariable("userId") @NotNull @Positive Long userId) {
    log.info("Received request GET /users/{}", userId);
    return userClient.getUserById(userId);
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Object> deleteUser(@PathVariable("userId") @NotNull @Positive Long id) {
    log.info("Received request DELETE /users/{} to delete user.", id);
    return userClient.deleteUser(id);
  }

}
