package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerId(long bookerId);

    List<Booking> findByItemId(long itemId);

    @Query("select b.item.id from Booking b where b.booker.id = :bookerId")
    List<Long> findItemIdByBookerId(long bookerId);

    @Query("select b.end from Booking b where (b.booker.id = :userId or b.item.owner = :userId) and b.item.id = :itemId")
    LocalDateTime findEndOfBookingByUserIdAndItemId(long userId, long itemId);

    @Query("select b from Booking b where b.booker.id = :bookerId and b.end >= :today")
    List<Booking> findCurrentByBookerId(long bookerId, LocalDateTime today);

    @Query("select b from Booking b where b.item.id = :itemId and b.end >= :today")
    Booking findCurrentByItemId(long itemId, LocalDateTime today);

    @Query("select b from Booking b where b.booker.id = :bookerId and b.end < :today")
    List<Booking> findPastByBookerId(long bookerId, LocalDateTime today);

    @Query("select b from Booking b where b.item.id = :itemId and b.end < :today")
    List<Booking> findPastByItemId(long itemId, LocalDateTime today);

    @Query("select b from Booking b where b.booker.id = :bookerId and b.start > :today")
    List<Booking> findFutureByBookerId(long bookerId, LocalDateTime today);

    @Query("select b from Booking b where b.item.id = :itemId and b.start > :today")
    List<Booking> findFutureByItemId(long itemId, LocalDateTime today);

    List<Booking> findByStatusEqualsAndBookerIdEqualsOrderByStartDesc(Status status, long bookerId);

    List<Booking> findByStatusEqualsAndItemIdEqualsOrderByStartDesc(Status status, long itemId);

    @Query("select b.item.id from Booking b where b.id = :bookingId")
    Long findItemIdByBookingId(long bookingId);

    @Query("select b from Booking b where b.item.id = :itemId and b.start > :now order by b.start")
    List<Booking> findNextBookings(long itemId, LocalDateTime now);

    @Query("select b from Booking b where b.item.id = :itemId and b.end < :now order by b.end desc")
    List<Booking> findLastBookings(long itemId, LocalDateTime now);

}
