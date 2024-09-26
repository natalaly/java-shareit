package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {

  @Query("""
      select case when count(b) > 0 then true else false end
      from Booking as b
      where b.item.id = :itemId
      and b.end > :start
      and b.start < :end
      and b.status = 'APPROVED'
      """)
  boolean existsByItemIdAndEndAfterAndStartBeforeAndStatus(
      @Param("itemId") Long itemId,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);

  Optional<Booking> findByIdAndItemOwnerId(Long bookingId, Long ownerId);

  @Query("""
      select  b
      from Booking as b
      where b.id = :bookingId
      and (b.booker.id = :userId or b.item.owner.id = :userId)
      """)
  Optional<Booking> findByIdAndItemOwnerIdOrBookerId(
      @Param("bookingId") Long bookingId,
      @Param("userId") Long userId);

  List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

  List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(
      Long bookerId,
      LocalDateTime now);

  List<Booking> findByBookerIdAndStatusAndEndBeforeOrderByStartDesc(
      Long bookerId,
      BookingStatus status,
      LocalDateTime now);

  List<Booking> findAllByBookerIdAndStatusAndStartBeforeAndEndAfterOrderByStartDesc(
      Long bookerId,
      BookingStatus status,
      LocalDateTime end,
      LocalDateTime start);

  List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

  List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

  List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
      Long ownerId,
      LocalDateTime now);

  List<Booking> findAllByItemOwnerIdAndStatusAndEndBeforeOrderByStartDesc(
      Long ownerId,
      BookingStatus bookingStatus,
      LocalDateTime now);

  List<Booking> findAllByItemOwnerIdAndStatusAndStartBeforeAndEndAfterOrderByStartDesc(
      Long ownerId,
      BookingStatus bookingStatus,
      LocalDateTime end,
      LocalDateTime start);

  List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus name);

  List<Booking> findByItemIdAndItemOwnerId(Long itemId, Long userId);

  Boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(
      Long itemId, Long userId,
      BookingStatus bookingStatus,
      LocalDateTime now);
}
