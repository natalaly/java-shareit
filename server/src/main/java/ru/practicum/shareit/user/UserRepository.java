package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

/**
 * Repository interface for managing {@link User} entities.
 *
 * @see JpaRepository
 * @see User
 * @see UserService
 */

public interface UserRepository extends JpaRepository<User, Long> {

}
