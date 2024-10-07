package ru.practicum.shareit.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

/**
 * REST controller for managing users. Provides endpoints for creating, updating, retrieving, and
 * deleting users.
 */

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<UserDto> createUser(@Validated(Create.class) @RequestBody UserDto user) {
    log.info("Received request POST /users");
    final UserDto userSaved = userService.createNewUser(user);

    final URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(userSaved.getId())
        .toUri();

    log.info("User created successfully with ID {} at {}.", userSaved.getId(), location.toString());
    return ResponseEntity.created(location).body(userSaved);
  }

  @PatchMapping("/{userId}")
  public ResponseEntity<UserDto> updateUser(
      @Validated(Update.class) @RequestBody UserDto user,
      @PathVariable("userId") @NotNull @Positive Long userId) {
    log.info("Received request PATCH /users/{} to update with data: {}", userId, user);
    final UserDto userUpdated = userService.updateUser(user, userId);
    log.info("User updated successfully with ID {}", userUpdated.getId());
    return ResponseEntity.ok(userUpdated);
  }

  @GetMapping
  public ResponseEntity<List<UserDto>> getAllUsers() {
    log.info("Received request GET /users ");
    final List<UserDto> users = userService.getAllUsers();
    log.info("Returning {} users", users.size());
    return ResponseEntity.ok(users);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserDto> getUserById(
      @PathVariable("userId") @NotNull @Positive Long userId) {
    log.info("Received request GET /users/{}", userId);
    final UserDto user = userService.getUserById(userId);
    log.info("Returning user: {}", user);
    return ResponseEntity.ok(user);
  }

  @DeleteMapping("/{userId}")
  public void deleteUser(@PathVariable("userId") @NotNull @Positive Long id) {
    log.info("Received request DELETE /users/{} to delete user.", id);
    userService.deleteUserById(id);
    log.info("User deleted successfully with ID {}", id);
  }

}
