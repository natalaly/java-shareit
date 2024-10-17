package ru.practicum.shareit.user.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.practicum.shareit.user.model.User;

class UserMapperTest {

  @ParameterizedTest(name = "{0}")
  @DisplayName("mapToUserDto(user) - correct data input.")
  @MethodSource("provideValidUserData")
  public void mapToUserDto(final String testName, final User userData) {

    final UserDto userDto = UserMapper.mapToUserDto(userData);

    assertNotNull(userDto);
    assertEquals(userData.getId(), userDto.getId());
    assertEquals(userData.getName(), userDto.getName());
    assertEquals(userData.getEmail(), userDto.getEmail());
  }

  @Test
  public void mapToUserDto_NullInput() {
    assertThrows(NullPointerException.class, () -> UserMapper.mapToUserDto(null));
  }

  @ParameterizedTest(name = "{0}")
  @DisplayName("mapToUser(UserDto) - correct data input.")
  @MethodSource("provideValidUserDtoData")
  public void mapToUser(final String testName, final UserDto userData) {

    final User user = UserMapper.mapToUser(userData);

    assertNotNull(user);
    assertEquals(userData.getId(), user.getId());
    assertEquals(userData.getName(), user.getName());
    assertEquals(userData.getEmail(), user.getEmail());
  }

  @Test
  public void mapToUser_NullInput() {
    assertThrows(NullPointerException.class, () -> UserMapper.mapToUser(null));
  }

  private static Stream<Arguments> provideValidUserDtoData() {
    return Stream.of(
        Arguments.of("All fields populated correctly.",
            UserDto.builder()
                .id(1L)
                .name("Valid Name")
                .email("validEmail@test.ru")
                .build()),
        Arguments.of("Id - null.",
            UserDto.builder()
                .name("Valid Name")
                .email("validEmail@test.ru")
                .build()),
        Arguments.of("Name - null.",
            UserDto.builder()
                .id(1L)
                .email("validEmail@test.ru")
                .build()),
        Arguments.of("email - null.",
            UserDto.builder()
                .id(1L)
                .name("Valid Name")
                .build()),
        Arguments.of("All fields - null.",
            UserDto.builder()
                .build())
    );
  }

  private static Stream<Arguments> provideValidUserData() {
    return Stream.of(
        Arguments.of("All fields populated correctly.",
            User.builder()
                .id(1L)
                .name("Valid Name")
                .email("validEmail@test.ru")
                .build()),
        Arguments.of("Id - null.",
            User.builder()
                .name("Valid Name")
                .email("validEmail@test.ru")
                .build()),
        Arguments.of("Name - null.",
            User.builder()
                .id(1L)
                .email("validEmail@test.ru")
                .build()),
        Arguments.of("email - null.",
            User.builder()
                .id(1L)
                .name("Valid Name")
                .build()),
        Arguments.of("All fields - null.",
            User.builder()
                .build())
    );
  }

}