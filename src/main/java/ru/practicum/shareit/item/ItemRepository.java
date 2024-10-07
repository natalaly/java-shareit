package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

/**
 * A repository interface for managing {@link Item} persistence and retrieval operations.
 * <p>
 * This interface defines methods for performing common CRUD operations on {@link Item} entities, as
 * well as additional queries for retrieving items by their owner, searching for items by text, and
 * checking item ownership.
 * <p>
 * In addition to the standard CRUD operations provided by {@link JpaRepository}, the following
 * methods are included:
 * <ul>
 *   <li>{@link #findByIdAndOwnerId(Long, Long)}: Retrieves an {@link Item} by its ID and the owner's ID.</li>
 *   <li>{@link #findAllByOwnerIdOrderById(Long)}: Retrieves a list of {@link Item} entities owned by a specific user, ordered by item ID.</li>
 *   <li>{@link #findByText(String)}: Searches for available {@link Item} entities where the name or description contains the specified text.</li>
 *   <li>{@link #existsByOwnerId(Long)}: Checks whether any {@link Item} entities exist for a given owner ID.</li>
 * </ul>
 *
 * @see Item
 * @see JpaRepository
 */
public interface ItemRepository extends JpaRepository<Item, Long> {

  Optional<Item> findByIdAndOwnerId(Long itemId, Long ownerId);

  List<Item> findAllByOwnerIdOrderById(Long ownerId);

  @Query("""
      select it
      from Item as it
      where it.available = true
      and (lower(it.name) like lower(concat('%', :text, '%'))
      or lower(it.description) like lower(concat('%', :text, '%')))
      """)
  List<Item> findByText(@Param("text") String text);

  boolean existsByOwnerId(Long userId);
}
