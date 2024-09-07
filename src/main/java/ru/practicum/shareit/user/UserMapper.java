package ru.practicum.shareit.user;

import java.util.Objects;
import lombok.experimental.UtilityClass;

/**
 * Utility class for mapping between {@link User} entities and {@link UserDto}. This class provides
 * static methods to convert between different representations of user data:
 * <ul>
 *   <li> {@link #mapToUser(UserDto)}: maps a {@link UserDto} to a {@link User} entity.</li>
 *   <li>{@link #mapToUserDto(User)}: maps a {@link User} to {@link UserDto}.</li>
 * </ul>
 */
@UtilityClass
public class UserMapper {

  public UserDto mapToUserDto(final User user) {
    Objects.requireNonNull(user, "User cannot be null");
    return UserDto.builder()
        .id(user.getId())
        .name(user.getName())
        .email(user.getEmail())
        .build();
  }

  public User mapToUser(final UserDto userDto) {
    Objects.requireNonNull(userDto, "UserDto cannot be null");
    return User.builder()
        .id(userDto.getId())
        .name(userDto.getName())
        .email(userDto.getEmail())
        .build();
  }
}
