package ru.practicum.shareit.item;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

/**
 * A repository interface for managing {@link Comment} persistence and retrieval operations.
 * <p>
 * This interface defines methods for interacting with {@link Comment} data, including finding all
 * comments for a specific {@link Item} or for multiple items.
 * <p>
 * In addition to standard CRUD operations provided by {@link JpaRepository}, the following methods
 * are included:
 * <ul>
 *   <li>{@link #findAllByItemId(Long)}: Retrieves a list of {@link Comment} entities associated with a specific {@link Item} ID.</li>
 *   <li>{@link #findAllByItemIdIn(List)}: Retrieves a list of {@link Comment} entities associated with multiple {@link Item} IDs.</li>
 * </ul>
 *
 * @see Comment
 * @see Item
 * @see JpaRepository
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

  List<Comment> findAllByItemId(Long itemId);

  List<Comment> findAllByItemIdIn(List<Long> allOwnerItems);
}
