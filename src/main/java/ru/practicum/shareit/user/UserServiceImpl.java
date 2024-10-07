package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DatabaseException;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

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
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  @Transactional
  public UserDto createNewUser(final UserDto userDto) {
    log.debug("Persisting user with email: {}.", userDto.getEmail());
    final User userToSave = UserMapper.mapToUser(userDto);
    return UserMapper.mapToUserDto(saveOrUpdate(userToSave));
  }

  @Override
  @Transactional
  public UserDto updateUser(final UserDto userDto, final Long userId) {
    log.debug("Updating user with ID = {}.", userId);
    final User userToUpdate = getByIdOrThrow(userId);

    Optional.ofNullable(userDto.getEmail()).ifPresent(userToUpdate::setEmail);
    Optional.ofNullable(userDto.getName()).ifPresent(userToUpdate::setName);

    return UserMapper.mapToUserDto(saveOrUpdate(userToUpdate));
  }

  @Override
  public UserDto getUserById(final Long id) {
    log.debug("Fetching user by ID = {}.", id);
    return UserMapper.mapToUserDto(getByIdOrThrow(id));
  }

  @Override
  public User getByIdOrThrow(final Long id) {
    log.debug("Getting a user instance for ID = {} from the DB.", id);
    return userRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("User with ID = {} not found in DB.", id);
          return new NotFoundException("User not found.");
        });
  }

  @Override
  public List<UserDto> getAllUsers() {
    log.debug("Fetching all users from the database.");
    return userRepository.findAll().stream()
        .map(UserMapper::mapToUserDto)
        .toList();
  }

  @Override
  @Transactional
  public void deleteUserById(final Long id) {
    log.debug("Deleting user with ID = {}", id);
    validateUserExist(id);
    userRepository.deleteById(id);
    log.debug("User with ID = {} has been successfully deleted.", id);
  }

  @Override
  public void validateUserExist(final Long id) {
    log.debug("Validating user id {} is not null and exist in DB", id);
    if (id == null || !userRepository.existsById(id)) {
      log.warn("Validation User with ID = {} is not null and exists in DB failed.", id);
      throw new NotFoundException("User not found.");
    }
    log.debug("Success: user ID {} is not null and exist in DB.", id);
  }

  private User saveOrUpdate(final User user) {
    log.debug("Saving or updating user with email: {}", user.getEmail());
    try {
      return userRepository.save(user);
    } catch (DataIntegrityViolationException e) {
      if (e.getCause() instanceof ConstraintViolationException cause) {
        if ("uq_user_email".equalsIgnoreCase(cause.getConstraintName())) {
          log.warn("Failed to save user. Email already exists: {}", user.getEmail());
          throw new DuplicatedDataException("Email already exists.");
        }
      }
      log.error("Unexpected error during save or update operation for user with email: {}",
          user.getEmail(), e);
      throw new DatabaseException(
          "Unexpected error occurred during saving or updating to the database.");
    }
  }

}
