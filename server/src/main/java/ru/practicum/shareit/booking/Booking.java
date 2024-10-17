package ru.practicum.shareit.booking;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

/**
 * Represents a booking entity which stores information about the rental process of an {@link Item}
 * by a {@link User}.
 * <p>
 * This class is mapped to the "bookings" table in the database.
 */
@Entity
@Table(name = "bookings")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@Builder
@Accessors(chain = true)
public class Booking {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(updatable = false, nullable = false)
  private Long id;

  @Column(name = "start_date", nullable = false, updatable = false)
  private LocalDateTime start;

  @Column(name = "end_date", nullable = false, updatable = false)
  private LocalDateTime end;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "item_id", updatable = false)
  @ToString.Exclude
  private Item item;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "booker_id", updatable = false)
  @ToString.Exclude
  private User booker;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private BookingStatus status;

  public void updateStatus(final BookingStatus newStatus) {
    if (this.status == BookingStatus.WAITING) {
      this.status = newStatus;
    } else {
      throw new ValidationException("Invalid status transition.");
    }
  }
}


