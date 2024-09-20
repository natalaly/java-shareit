package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.Optional;
import ru.practicum.shareit.user.model.User;

/**
 * A repository interface for managing user persistence and retrieval operations.
 * <p>
 * This interface defines methods for common CRUD operations on user data, as well as additional
 * methods for specific queries like checking the existence of a user by email or ID.
 * <p>
 * Methods include:
 * <ul>
 *   <li>{@link #findAll()}: Retrieves all users from the storage.</li>
 *   <li>{@link #findById(Long)}: Retrieves a user by their ID.</li>
 *   <li>{@link #save(User)}: Saves a new user to the storage.</li>
 *   <li>{@link #update(User)}: Updates an existing user in the storage.</li>
 *   <li>{@link #deleteById(Long)}: Deletes a user by their ID.</li>
 *   <li>{@link #existsById(Long)}: Checks if a user exists with the given ID.</li>
 * </ul>
 *
 * @see User
 * @see UserRepositoryInMemory
 */
public interface UserRepository {

  Collection<User> findAll();

  Optional<User> findById(Long id);

  User save(User user);

  User update(User user);

  void deleteById(Long id);

  boolean existsById(Long id);

}
