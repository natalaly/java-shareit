package ru.practicum.shareit.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.DatabaseException;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.UserBuilder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository repository;

  @InjectMocks
  private UserServiceImpl service;

  @Captor
  ArgumentCaptor<User> userArgumentCaptor;

  @SneakyThrows
  @Test
  @DisplayName("Test createUser() when email is not unique for DB.")
  void createUser_whenDuplicatedEmail_thenConflict() {
    UserDto userDto = buildUserDto();
    DataIntegrityViolationException dataIntegrityViolationException =
        new DataIntegrityViolationException("Duplicate email", new ConstraintViolationException(
            "Duplicate entry for email", null, "uq_user_email"));

    when(repository.save(any(User.class)))
        .thenThrow(dataIntegrityViolationException);

    DuplicatedDataException thrown = assertThrows(DuplicatedDataException.class,
        () -> service.createNewUser(userDto));
    assertEquals("Email already exists.", thrown.getMessage());

    verify(repository, times(1)).save(any(User.class));
    verifyNoMoreInteractions(repository);
  }

  @SneakyThrows
  @ParameterizedTest(name = "{0}")
  @MethodSource("provideDatabaseExceptionsNotEmailDuplication")
  @DisplayName("Test createUser() when exceptions other than email duplication are thrown by DB.")
  void createUser_whenOtherDbExceptions_thenDataBaseException(String testName,
                                                              Exception exception) {
    UserDto userDto = buildUserDto();
    DataIntegrityViolationException dataIntegrityViolationException =
        new DataIntegrityViolationException("Database Constraint Violation", exception);

    when(repository.save(any(User.class)))
        .thenThrow(dataIntegrityViolationException);

    DatabaseException thrown = assertThrows(DatabaseException.class,
        () -> service.createNewUser(userDto));
    assertEquals(
        "Unexpected error occurred during saving or updating to the database.",
        thrown.getMessage()
    );

    verify(repository, times(1)).save(any(User.class));
    verifyNoMoreInteractions(repository);
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("provideUserDtoData")
  @DisplayName("Test updateUser() with different fields number and combination to update.")
  void updateUser_whenAllFieldsToUpdate(String testName, UserDto userDto, User updatedUser,
                                        String expectedEmail, String expectedName) {
    Long userId = 1L;
    User existingUser = buildUser();

    when(repository.findById(userId)).thenReturn(Optional.of(existingUser));
    when(repository.save(any(User.class))).thenReturn(updatedUser);

    UserDto result = service.updateUser(userDto, userId);

    assertEquals(expectedEmail, result.getEmail());
    assertEquals(expectedName, result.getName());
    assertEquals(userId, result.getId());

    verify(repository).save(userArgumentCaptor.capture());
    assertEquals(expectedEmail, userArgumentCaptor.getValue().getEmail());
    assertEquals(expectedName, userArgumentCaptor.getValue().getName());
    assertEquals(userId, userArgumentCaptor.getValue().getId());

    verify(repository, times(1)).findById(userId);
    verify(repository, times(1)).save(any(User.class));
    verifyNoMoreInteractions(repository);
  }

  @Test
  void validateUserExist_whenUserExisted_thenDoNothing() {
    Long id = 1L;
    when(repository.existsById(any(Long.class))).thenReturn(true);

    service.validateUserExist(id);

    verify(repository, times(1)).existsById(1L);
    verifyNoMoreInteractions(repository);
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("provideInvalidIdOptions")
  @DisplayName("Test validateUserExist - negative scenarios")
  void validateUserExist_negativeScenarios(String testName, Long id) {

    lenient().when(repository.existsById(id)).thenReturn(false);

    NotFoundException thrown = assertThrows(NotFoundException.class,
        () -> service.validateUserExist(id));

    assertEquals("User not found.", thrown.getMessage());

    verify(repository, atMostOnce()).existsById(id);
    verifyNoMoreInteractions(repository);
  }

  private static Stream<Arguments> provideDatabaseExceptionsNotEmailDuplication() {
    return Stream.of(
        Arguments.of("Constraint Violation other that email duplication.",
            new ConstraintViolationException("", null, null)),
        Arguments.of("Foreign Key Violation",
            new SQLIntegrityConstraintViolationException("Foreign key constraint violation")),
        Arguments.of("Check Constraint Violation",
            new SQLIntegrityConstraintViolationException("Check constraint violation")),
        Arguments.of("Other SQL Exception",
            new SQLException("Generic SQL exception"))
    );
  }

  private static Stream<Arguments> provideUserDtoData() {
    return Stream.of(
        Arguments.of("All fields to update.",
            buildUserDtoUpdated(),
            buildUser().setEmail("updated@example.com").setName("Updated User"),
            "updated@example.com",
            "Updated User"),
        Arguments.of("Valid email to update.",
            buildUserDtoUpdated().setName(null),
            buildUser().setEmail("updated@example.com"),
            "updated@example.com",
            "Test User"),
        Arguments.of("Valid name to update",
            buildUserDtoUpdated().setEmail(null),
            buildUser().setName("Updated User"),
            "test@example.com",
            "Updated User"),
        Arguments.of("Null fields to update.",
            UserDto.builder().build(),
            buildUser(),
            "test@example.com",
            "Test User")
    );
  }

  private static Stream<Arguments> provideInvalidIdOptions() {
    return Stream.of(
        Arguments.of("Not existed user ID.", 110L),
        Arguments.of("Null ID.", null)
    );
  }

  private static UserDto buildUserDtoUpdated() {
    return UserBuilder.buildUserDtoUpdated();
  }

  private static User buildUser() {
    return UserBuilder.buildUser();
  }

  private UserDto buildUserDto() {
    return UserBuilder.buildUserDto();
  }

}