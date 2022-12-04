package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.data.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT ir FROM ItemRequest AS ir WHERE ir.requester.id = ?1")
    List<ItemRequest> getByRequesterId(long requesterId, Sort sort);

    @Query("SELECT ir FROM ItemRequest AS ir INNER JOIN Item AS i ON ir.id = i.request.id WHERE i.ownerId = ?1")
    List<ItemRequest> findAllByUserId(long userId, Sort sort);

    @Query("SELECT ir FROM ItemRequest AS ir INNER JOIN Item AS i ON ir.id = i.request.id WHERE i.ownerId = ?1")
    List<ItemRequest> findAllByUserId(long userId, Pageable pageable);
}