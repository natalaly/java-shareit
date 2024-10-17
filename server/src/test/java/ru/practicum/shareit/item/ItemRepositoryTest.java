package ru.practicum.shareit.item;


import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.ItemBuilder;
import ru.practicum.shareit.utils.RequestBuilder;
import ru.practicum.shareit.utils.UserBuilder;

@DataJpaTest
class ItemRepositoryTest {

  @Autowired
  ItemRepository itemRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  ItemRequestRepository itemRequestRepository;

  private User user;
  private Item item1;
  private Item item2;
  private Item item3;

  @BeforeEach
  void setUp() {
    user = userRepository.save(buildUser("Tester", "test@test.com"));

    item1 = buildItem("Item 1", "Description 1", true, user);
    item2 = buildItem("Item 2", "Description 2", true, user);
    item3 = buildItem("Item 3", "Description 3", false, user);

    itemRepository.saveAll(List.of(item1, item2, item3));
  }

  @Test
  void findByIdAndOwnerId_whenValidInput_thenItemFound() {

    Optional<Item> foundItem = itemRepository.findByIdAndOwnerId(item1.getId(), user.getId());

    assertThat(foundItem)
        .isPresent()
        .get()
        .extracting(Item::getId, Item::getName)
        .containsExactly(item1.getId(), item1.getName());
  }

  @Test
  void findAllByOwnerIdOrderById_whenValidInput_thenNotEmptyItemListReturned() {

    List<Item> items = itemRepository.findAllByOwnerIdOrderById(user.getId());

    assertThat(items)
        .isNotNull()
        .isNotEmpty()
        .hasSize(3)
        .extracting(Item::getId)
        .containsExactly(item1.getId(), item2.getId(), item3.getId());
  }

  @Test
  void findByText_whenValidInput_thenItemListReturned() {

    List<Item> foundItems = itemRepository.findByText("script");

    assertThat(foundItems)
        .hasSize(2)
        .extracting(Item::getName)
        .containsExactlyInAnyOrder("Item 1", "Item 2");
  }

  @Test
  void existsByOwnerId_whenItemWithGivenOwnerIdExists_thenTrue() {

    boolean exists = itemRepository.existsByOwnerId(user.getId());

    assertThat(exists).isTrue();
  }

  @Test
  void existsByOwnerId_whenItemWithGivenOwnerIdNotExists_thenFalse() {

    boolean exists = itemRepository.existsByOwnerId(5L);

    assertThat(exists).isFalse();
  }

  @Test
  void findByRequestIdIn_whenItemsThanCorrespondRequestsExist_thenItemList() {

    ItemRequest itemRequest = itemRequestRepository.save(
        RequestBuilder
            .buildItemRequest("Request 1", user, LocalDateTime.now().minusMonths(10)));
    item1.setRequest(itemRequest);
    itemRepository.save(item1);

    List<Item> foundItems = itemRepository.findByRequestIdIn(List.of(itemRequest.getId()));

    assertThat(foundItems)
        .isNotEmpty()
        .hasSize(1)
        .extracting(Item::getName)
        .containsExactly("Item 1");
  }

  @Test
  void findByRequestIdIn_whenNoItemsCorrespondedRequestsAreExisted_thenEmptyList() {
    List<Item> foundItems = itemRepository.findByRequestIdIn(List.of(1L));

    assertThat(foundItems)
        .isEmpty();
  }

  private Item buildItem(String name, String description, boolean available, User owner) {
    return ItemBuilder.buildItem(name, description, available, owner);

  }

  private User buildUser(String name, String email) {
    return UserBuilder.buildUser(name, email);
  }
}