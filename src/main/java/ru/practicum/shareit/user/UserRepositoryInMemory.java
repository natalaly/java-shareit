package ru.practicum.shareit.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * An in-memory implementation of the {@link UserRepository} interface.
 * <p>
 * This repository provides a basic, non-persistent storage mechanism for user entities, using a
 * {@link HashMap} to store users with their IDs as keys.
 *
 * @see User
 */
@Repository
public class UserRepositoryInMemory implements UserRepository {

  private final Map<Long, User> users = new HashMap<>();
  private Long usedId = 0L;

  @Override
  public List<User> findAll() {
    return users.values().stream()
        .toList();
  }

  @Override
  public Optional<User> findById(final Long id) {
    return Optional.ofNullable(users.get(id));
  }

  @Override
  public User save(final User user) {
    final Long id = generateUserId();
    user.setId(id);
    users.put(id, user);
    return user;
  }

  @Override
  public User update(final User user, Long id) {
    User userToUpdate = users.get(id);
    if (user.getName() != null) {
      userToUpdate.setName(user.getName());
    }
    if (user.getEmail() != null) {
      userToUpdate.setEmail(user.getEmail());
    }
    return userToUpdate;
  }

  @Override
  public void deleteById(Long id) {
    users.remove(id);
  }

  @Override
  public boolean existsByEmail(final String email) {
    return users.values().stream()
        .anyMatch(u -> u.getEmail().equals(email));
  }

  @Override
  public boolean existsById(final Long id) {
    return users.containsKey(id);
  }

  private Long generateUserId() {
    return ++usedId;
  }
}
