package ru.practicum.shareit.user;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;

/**
 * Service implementation class for managing user-related operations.
 *
 * @see User
 * @see UserDto
 * @see UserRepository
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  public List<UserDto> getAllUsers() {
    return userRepository.findAll().stream()
        .map(UserMapper::mapToUserDto)
        .toList();
  }

  @Override
  public UserDto getUserById(final Long id) {
    return UserMapper.mapToUserDto(getByIdOrThrow(id));
  }

  @Override
  public UserDto createNewUser(final UserDto userDto) {
    validateEmailUnique(userDto.getEmail());
    return UserMapper.mapToUserDto(
        userRepository.save(UserMapper.mapToUser(userDto)));
  }

  @Override
  public UserDto updateUser(final UserDto userDto, final Long userId) {
    validateUserExist(userId);
    if (userDto.getEmail() != null) {
      validateEmailUnique(userDto.getEmail());
    }
    return UserMapper.mapToUserDto(userRepository.update(UserMapper.mapToUser(userDto), userId));
  }

  @Override
  public void deleteUserById(Long id) {
    validateUserExist(id);
    userRepository.deleteById(id);
  }

  @Override
  public void validateUserExist(final Long id) {
    log.debug("Validating user id {} is not null and exist in DB", id);
    if (id == null || !userRepository.existsById(id)) {
      log.warn("Validation User with ID = {} is not null and exists in DB failed.", id);
      throw new NotFoundException("User with ID = " + id + " not found.");
    }
    log.debug("Success in validating user id {} is not null and exist in DB", id);
  }

  private User getByIdOrThrow(Long id) {
    log.debug("Getting a user instance for ID = {} from the repository", id);
    return userRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("User with ID = {} not found in DB.", id);
          return new NotFoundException("User with ID = " + id + " not found.");
        });
  }

  private void validateEmailUnique(final String email) {
    log.debug("Validating email {} is not null and does not exist in DB", email);
    if (email == null || email.isBlank() || userRepository.existsByEmail(email)) {
      log.warn("Email {} already exists in the DB ", email);
      throw new DuplicatedDataException("User with Email  " + email + " is already exists.");
    }
    log.debug("Success in validating user email {} is not null and does not exist in DB", email);
  }

}
