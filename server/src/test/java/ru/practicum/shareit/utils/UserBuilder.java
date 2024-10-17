package ru.practicum.shareit.utils;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserBuilder {

   public UserDto buildUserDto() {
    return UserDto.builder()
        .email("test@example.com")
        .name("Test User")
        .build();
  }

  public UserDto buildUserDtoUpdated() {
    return UserDto.builder()
        .email("updated@example.com")
        .name("Updated User")
        .build();
  }

  public User buildUser() {
    return User.builder()
        .id(1L)
        .name("Test User")
        .email("test@example.com")
        .build();
  }

  public User buildUser(String name, String email) {
    return User.builder()
        .name(name)
        .email(email)
        .build();
  }



}
