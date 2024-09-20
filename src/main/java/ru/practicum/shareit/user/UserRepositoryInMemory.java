package ru.practicum.shareit.user;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.CopyUtil;

/**
 * An in-memory implementation of the {@link UserRepository} interface.
 * <p>
 * This repository provides a basic, non-persistent storage mechanism for user entities, using a
 * {@link HashMap} to store users with their IDs as keys.
 *
 * @see User
 */
@Repository
@Slf4j
public class UserRepositoryInMemory implements UserRepository {

  private final Map<Long, User> users = new HashMap<>();
  private final Set<String> emailsInUse = new HashSet<>();
  private Long usedId = 0L;

  @Override
  public List<User> findAll() {
    return users.values().stream().map(this::copy)
        .toList();
  }

  @SneakyThrows
  @Override
  public Optional<User> findById(final Long id) {
    return Optional.ofNullable(users.get(id)).map(this::copy);
  }

  @Override
  public User save(final User user) {
    saveUniqueEmailOrThrow(user.getEmail(), user.getId());
    final Long id = generateUserId();
    user.setId(id);
    users.put(id, user);
    return copy(users.get(id));
  }

  @Override
  public User update(final User user) {
    saveUniqueEmailOrThrow(user.getEmail(), user.getId());
    users.put(user.getId(), user);
    return copy(users.get(user.getId()));
  }

  @Override
  public void deleteById(final Long id) {
    User removedUser = users.remove(id);
    if (removedUser != null) {
      emailsInUse.remove(removedUser.getEmail().toLowerCase());
    }
  }

  @Override
  public boolean existsById(final Long id) {
    return users.containsKey(id);
  }

  private Long generateUserId() {
    return ++usedId;
  }

  private void saveUniqueEmailOrThrow(final String emailToSave, final Long userId) {
    Objects.requireNonNull(emailToSave, "Email should be defined.");
    final String emailToCheck = emailToSave.toLowerCase();
    final String userEmail = findById(userId).map(User::getEmail).map(String::toLowerCase)
        .orElse(null);

    if (emailsInUse.contains(emailToCheck)) {
      if (userId == null || !emailToCheck.equals(userEmail)) {
        log.warn("Email {} already exists in the DB ", emailToSave);
        throw new DuplicatedDataException(
            "User with Email  " + emailToSave + " is already exists.");
      }
    }
    emailsInUse.add(emailToCheck);
  }

  private User copy(final User user) {
    return CopyUtil.copy(user, User.class);
  }
}
