package ru.practicum.shareit.booking;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.BookingBuilder;
import ru.practicum.shareit.utils.ItemBuilder;
import ru.practicum.shareit.utils.UserBuilder;

@DataJpaTest
class BookingRepositoryTest {

  @Autowired
  private BookingRepository bookingRepository;

  @Autowired
  private ItemRepository itemRepository;
  @Autowired
  private UserRepository userRepository;

  private List<User> users;
  private List<Item> items;
  private List<Booking> bookings;

  @BeforeEach
  void setUp() {
    users = List.of(
        userRepository.save(buildUser("User 1", "user1@test.com")),
        userRepository.save(buildUser("User 2", "user2@test.com"))
    );
    items = List.of(
        itemRepository.save(
            buildItem("Item 1", "Item description 1", true, users.get(0))),
        itemRepository.save(
            buildItem("Item 2", "Item description 2", true, users.get(0)))
    );
    bookings = List.of(
        bookingRepository.save(
            buildBooking(items.get(0), users.get(1),
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(3),
                BookingStatus.APPROVED)),
        bookingRepository.save(
            buildBooking(items.get(0), users.get(1),
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(4),
                BookingStatus.APPROVED)),
        bookingRepository.save(
            buildBooking(items.get(1), users.get(1),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING))
    );
  }

  @Test
  void existsByItemIdAndEndAfterAndStartBeforeAndStatus() {
    boolean exists =
        bookingRepository.existsByItemIdAndEndAfterAndStartBeforeAndStatus(
            items.getFirst().getId(),
            LocalDateTime.now().minusDays(6),
            LocalDateTime.now()
        );
    assertThat(exists).isTrue();
  }

  @Test
  void findByIdAndItemOwnerId() {
    Optional<Booking> booking =
        bookingRepository.findByIdAndItemOwnerId(
            bookings.getFirst().getId(), users.getFirst().getId());
    assertThat(booking).isPresent();
    assertThat(booking.get().getId()).isEqualTo(bookings.getFirst().getId());
  }

  @Test
  void findByIdAndItemOwnerIdOrBookerId() {
    Optional<Booking> booking =
        bookingRepository.findByIdAndItemOwnerIdOrBookerId(
            bookings.getFirst().getId(), users.get(1).getId());
    assertThat(booking).isPresent();
    assertThat(booking.get().getId()).isEqualTo(bookings.getFirst().getId());
  }

  @Test
  void findAllByBookerIdOrderByStartDesc() {
    List<Booking> result = bookingRepository.findAllByBookerIdOrderByStartDesc(
        users.get(1).getId());
    assertThat(result).hasSize(3);
    assertThat(result.get(0).getStart()).isAfter(result.get(1).getStart());
  }

  @Test
  void findAllByBookerIdAndStartAfterOrderByStartDesc() {
    List<Booking> result =
        bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(users.get(1).getId(),
            LocalDateTime.now());
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getStart()).isAfter(result.get(1).getStart());
  }

  @Test
  void findByBookerIdAndEndBeforeOrderByStartDesc() {
    List<Booking> result =
        bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(users.get(1).getId(),
            LocalDateTime.now());
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getId()).isEqualTo(bookings.getFirst().getId());
  }

  @Test
  void findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
    List<Booking> result =
        bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            users.get(1).getId(), LocalDateTime.now());
    assertThat(result).isEmpty();
  }

  @Test
  void findAllByBookerIdAndStatusOrderByStartDesc() {
    List<Booking> result =
        bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
            users.get(1).getId(), BookingStatus.APPROVED);
    assertThat(result).hasSize(2);
  }

  @Test
  void findAllByItemOwnerIdOrderByStartDesc() {
    List<Booking> result = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(
        users.getFirst().getId());
    assertThat(result).hasSize(3);
  }

  @Test
  void findAllByItemOwnerIdAndStartAfterOrderByStartDesc() {
    List<Booking> result =
        bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
            users.getFirst().getId(), LocalDateTime.now());
    assertThat(result).hasSize(2);
  }

  @Test
  void findAllByItemOwnerIdAndEndBeforeOrderByStartDesc() {
    List<Booking> result =
        bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
            users.getFirst().getId(), LocalDateTime.now());
    assertThat(result).hasSize(1);
  }

  @Test
  void findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
    List<Booking> result =
        bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            users.getFirst().getId(), LocalDateTime.now());
    assertThat(result).isEmpty();
  }

  @Test
  void findAllByItemOwnerIdAndStatusOrderByStartDesc() {
    List<Booking> result =
        bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
            users.getFirst().getId(), BookingStatus.APPROVED);
    assertThat(result).hasSize(2);
  }

  @Test
  void findByItemIdAndItemOwnerId() {
    List<Booking> result =
        bookingRepository.findByItemIdAndItemOwnerId(
            items.getFirst().getId(), users.getFirst().getId());
    assertThat(result).hasSize(2);
  }

  @Test
  void existsByItemIdAndBookerIdAndStatusAndEndBefore() {
    boolean exists = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
        items.get(0).getId(),
        users.get(1).getId(),
        BookingStatus.APPROVED,
        LocalDateTime.now()
    );
    assertThat(exists).isTrue();
  }

  private Item buildItem(
      String name, String description, boolean available, User owner) {
    return ItemBuilder.buildItem(name, description, available, owner);

  }

  private User buildUser(String name, String email) {
    return UserBuilder.buildUser(name, email);
  }

  private Booking buildBooking(Item item, User booker, LocalDateTime start, LocalDateTime end,
                               BookingStatus status) {
    return BookingBuilder.buildBooking(item, booker, start, end, status);
  }
}