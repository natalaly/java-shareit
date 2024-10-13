package ru.practicum.shareit.request;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link ItemRequest} entities.
 * <ul>
 *   <li>
 *     {@link #findByRequestorIdOrderByCreatedDesc(Long)}: Retrieves item requests made by a specific requestor,
 *     ordered by the creation date in descending order.
 *   </li>
 *   <li>
 *     {@link #findByRequestorIdNotOrderByCreatedDesc(Long)}: Retrieves item requests made by users other than the
 *     specified user, ordered by the creation date in descending order.
 *   </li>
 * </ul>
 */
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

  List<ItemRequest> findByRequestorIdOrderByCreatedDesc(Long requestorId);

  List<ItemRequest> findByRequestorIdNotOrderByCreatedDesc(Long userId);
}
