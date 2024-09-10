package ru.practicum.shareit.user;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

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
  public User update(final User user) {
    users.put(user.getId(), user);
    return user;
  }

  @Override
  public void deleteById(Long id) {
    users.remove(id);
  }

  @Override
  public boolean existsEmail(final String email, final Long... userId) {
    if (userId == null || userId.length == 0) {
      return users.values().stream()
          .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }
    Set<Long> userIdSet = new HashSet<>(Arrays.asList(userId));
    return users.values().stream()
        .anyMatch(u -> u.getEmail().equalsIgnoreCase(email) &&
            !userIdSet.contains(u.getId()));
  }


  @Override
  public boolean existsById(final Long id) {
    return users.containsKey(id);
  }

  private Long generateUserId() {
    return ++usedId;
  }
}
