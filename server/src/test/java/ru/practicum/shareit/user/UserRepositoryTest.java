package ru.practicum.shareit.user;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.UserBuilder;

@DataJpaTest
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EntityManager em;

  @Test
  public void save_whenUserWithDuplicatedEmailToSave_thenThrow() {
    String email = "same@email.com";
    User userData = buildUserWithData("Existed User", email);
    em.persist(userData);
    em.flush();

    User userToSave = buildUserWithData("Attempting", email);

    assertThatThrownBy(() -> userRepository.saveAndFlush(userToSave))
        .isInstanceOf(DataIntegrityViolationException.class)
        .hasMessageContaining("constraint")
        .hasMessageContaining("UQ_USER_EMAIL");
  }

  private User buildUserWithData(String name, String email) {
    return UserBuilder.buildUser(name, email);
  }

}