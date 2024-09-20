package ru.practicum.shareit.booking;

import lombok.Getter;

@Getter
public enum BookingStatus {

  WAITING, // новое бронирование, ожидает одобрения
  APPROVED, // бронирование подтверждено владельцем
  REJECTED, // бронирование отклонено владельцем
  CANCELED // бронирование отменено создателем

}
