package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingStorage extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerId(long bookerId);

    @Query("select b from Booking b where b.item.id in (:itemIds)")
    List<Booking> findByItemIds(List<Long> itemIds);

    @Query("select case when count(b.item.id) > 0 then true else false end from Booking b where b.booker.id = :bookerId")
    boolean existsItemIdByBookerId(long bookerId);

    @Query("select b.end from Booking b where (b.booker.id = :userId or b.item.owner = :userId) and b.item.id = :itemId")
    LocalDateTime findEndOfBookingByUserIdAndItemId(long userId, long itemId);

    @Query("select b from Booking b where b.booker.id = :bookerId " +
            "and (b.start <= :today and b.end >= :today)")
    List<Booking> findCurrentByBookerId(long bookerId, LocalDateTime today);

    @Query("select b from Booking b where b.item.id in (:itemIds) " +
            "and (b.start <= :today and b.end >= :today)")
    List<Booking> findCurrentByItemIds(List<Long> itemIds, LocalDateTime today);

    @Query("select b from Booking b where b.booker.id = :bookerId and b.end < :today")
    List<Booking> findPastByBookerId(long bookerId, LocalDateTime today);

    @Query("select b from Booking b where b.item.id in (:itemIds) and b.end < :today")
    List<Booking> findPastByItemIds(List<Long> itemIds, LocalDateTime today);

    @Query("select b from Booking b where b.booker.id = :bookerId and b.start > :today")
    List<Booking> findFutureByBookerId(long bookerId, LocalDateTime today);

    @Query("select b from Booking b where b.item.id in (:itemIds) and b.start > :today")
    List<Booking> findFutureByItemIds(List<Long> itemIds, LocalDateTime today);

    List<Booking> findByStatusEqualsAndBookerIdEqualsOrderByStartDesc(Status status, long bookerId);

    List<Booking> findByStatusEqualsAndItemIdInOrderByStartDesc(Status status, List<Long> itemIds);

    @Query("select b.item.id from Booking b where b.id = :bookingId")
    Long findItemIdByBookingId(long bookingId);

    @Query("select b from Booking b where b.item.id in (:itemIds) and b.start > :now order by b.start limit 1")
    List<Booking> findNextBookings(List<Long> itemIds, LocalDateTime now);

    @Query("select b from Booking b where b.item.id in (:itemIds) and b.end < :now order by b.end desc limit 1")
    List<Booking> findLastBookings(List<Long> itemIds, LocalDateTime now);

}
