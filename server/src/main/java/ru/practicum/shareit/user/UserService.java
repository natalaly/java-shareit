package ru.practicum.shareit.user;

import java.util.List;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

/**
 * A service interface for managing user-related operations and interactions.
 * <p>
 * This interface provides methods for various user-specific operations, such as creating, updating,
 * deleting users, and additional functionalities like managing user validation.
 * </p>
 * <p>
 * Methods include:
 * <ul>
 *  <li>{@link #createNewUser(UserDto)}: Creates a new user with the provided data.</li>
 *  <li>{@link #updateUser(UserDto, Long)}: Updates an existing user identified by their ID with the provided data.</li>
 *  <li>{@link #getUserById(Long)}: Retrieves a specific user by their ID.</li>
 *  <li>{@link #getAllUsers()}: Retrieves a list of all users.</li>
 *  <li>{@link #deleteUserById(Long)}: Deletes a user by their ID.</li>
 *  <li>{@link #validateUserExist(Long)}: Validates if a user with the given ID exists in the storage, throwing an exception if not.</li>
 * </ul>
 * </p>
 *
 * @see UserDto
 * @see UserServiceImpl
 */
public interface UserService {

  UserDto createNewUser(UserDto userDto);

  UserDto updateUser(UserDto userDto, Long userId);

  UserDto getUserById(Long id);

  User getByIdOrThrow(final Long id);

  List<UserDto> getAllUsers();

  void deleteUserById(Long id);

  void validateUserExist(Long id);
}
