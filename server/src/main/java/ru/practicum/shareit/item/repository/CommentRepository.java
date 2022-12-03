package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.data.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment AS c WHERE c.item.id = ?1")
    List<Comment> findAllByItemId(long itemId);

    @Query("SELECT c FROM Comment AS c INNER JOIN Item AS i ON c.item.id = i.id WHERE i.ownerId = ?1")
    List<Comment> findAllByOwnerId(long ownerId);
}