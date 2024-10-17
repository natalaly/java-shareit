package ru.practicum.shareit.request;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.RequestBuilder;
import ru.practicum.shareit.utils.UserBuilder;

@DataJpaTest
class ItemRequestRepositoryTest {

  @Autowired
  private ItemRequestRepository requestRepository;

  @Autowired
  private UserRepository userRepository;

  private User requestor;
  private User otherUser;
  private List<ItemRequest> requestList;

  @BeforeEach
  void setUp() {

    requestor = userRepository.save(buildUser("requester@test.com", "Requester Name"));
    otherUser = userRepository.save(buildUser("otherUser@test.com", "Other User"));

    requestList = new ArrayList<>();
    requestList.add(requestRepository.save(
        buildItemRequest("First Request", requestor, LocalDateTime.now().minusDays(1))));
    requestList.add(requestRepository.save(
        buildItemRequest("Second Request", requestor, LocalDateTime.now().minusDays(2))));
    requestList.add(requestRepository.save(
        buildItemRequest("Other User Request", otherUser, LocalDateTime.now().minusDays(3))));

  }

  @Test
  void findByRequestorIdOrderByCreatedDesc() {
    List<ItemRequest> foundRequests =
        requestRepository.findByRequestorIdOrderByCreatedDesc(requestor.getId());

    assertThat(foundRequests).hasSize(2);
    assertThat(foundRequests.get(0).getDescription()).isEqualTo("First Request");
    assertThat(foundRequests.get(1).getDescription()).isEqualTo("Second Request");
  }

  @Test
  void findByRequestorIdNotOrderByCreatedDesc() {
    List<ItemRequest> foundRequests =
        requestRepository.findByRequestorIdNotOrderByCreatedDesc(requestor.getId());

    assertThat(foundRequests).hasSize(1);
    assertThat(foundRequests.getFirst().getDescription()).isEqualTo("Other User Request");

  }

  private ItemRequest buildItemRequest(String description, User requester, LocalDateTime created) {
    return RequestBuilder.buildItemRequest(description, requester, created);
  }

  private User buildUser(String name, String email) {
    return UserBuilder.buildUser(name, email);
  }
}