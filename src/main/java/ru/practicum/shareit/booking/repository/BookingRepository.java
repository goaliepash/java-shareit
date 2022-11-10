package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 ORDER BY b.start DESC")
    List<Booking> findAllByBooker(long bookerId);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND b.status = :#{#status} ORDER BY b.start DESC")
    List<Booking> findAllByBookerAndStatus(long bookerId, @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findAllPastByBooker(long bookerId);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findAllCurrentByBooker(long bookerId);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND b.start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findAllFutureByBooker(long bookerId);

    @Query("SELECT b FROM Booking AS b INNER JOIN Item AS i ON b.item.id = i.id WHERE i.ownerId = ?1 ORDER BY b.start DESC")
    List<Booking> findAllByOwner(long ownerId);

    @Query("SELECT b FROM Booking AS b INNER JOIN Item AS i ON b.item.id = i.id WHERE i.ownerId = ?1 AND b.status = :#{#status} ORDER BY b.start DESC")
    List<Booking> findAllByOwnerAndStatus(long ownerId, @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking AS b INNER JOIN Item AS i ON b.item.id = i.id WHERE i.ownerId = ?1 AND b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findAllPastByOwner(long ownerId);

    @Query("SELECT b FROM Booking AS b INNER JOIN Item AS i ON b.item.id = i.id WHERE i.ownerId = ?1 AND b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findAllCurrentByOwner(long ownerId);

    @Query("SELECT b FROM Booking AS b INNER JOIN Item AS i ON b.item.id = i.id WHERE i.ownerId = ?1 AND b.start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findAllFutureByOwner(long ownerId);

    @Query("SELECT b FROM Booking AS b INNER JOIN Item AS i ON b.item.id = i.id WHERE b.item.id = ?1 AND b.start <= CURRENT_TIMESTAMP AND i.ownerId = ?2 ORDER BY b.start DESC")
    List<Booking> getLastByItemId(long itemId, long ownerId);

    @Query("SELECT b FROM Booking AS b INNER JOIN Item AS i ON b.item.id = i.id WHERE b.item.id = ?1 AND b.end > CURRENT_TIMESTAMP AND i.ownerId = ?2 ORDER BY b.start ASC")
    List<Booking> getNextByItemId(long itemId, long ownerId);

    @Query("SELECT b FROM Booking AS b WHERE b.item.id = ?1 AND b.status != :#{#status}")
    List<Booking> findAllByItemIdAndStatus(long itemId, @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking AS b WHERE b.item.id = ?1 AND b.start <= CURRENT_TIMESTAMP")
    List<Booking> findAllCurrentByItemId(long itemId);
}