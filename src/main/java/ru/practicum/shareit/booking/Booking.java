package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {

  private Long id; // уникальный идентификатор бронирования
  private LocalDateTime start; // дата и время начала бронирования
  private LocalDateTime end; // дата и время конца бронирования
  private Item item; // вещь, которую пользователь бронирует
  private User booker; // пользователь, который осуществляет бронирование
  private BookingStatus status; // статус бронирования

}


