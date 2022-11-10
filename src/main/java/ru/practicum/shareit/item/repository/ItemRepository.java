package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.data.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE i.ownerId = ?1")
    List<Item> findAllByOwnerId(long id);

    @Query("SELECT i FROM Item AS i WHERE i.available = TRUE AND (LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%')))")
    List<Item> findAllByText(String text);
}