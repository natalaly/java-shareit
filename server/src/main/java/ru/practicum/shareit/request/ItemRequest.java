package ru.practicum.shareit.request;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.practicum.shareit.user.model.User;

/**
 * Represents a request for an item in the application. This entity is mapped to the "requests"
 * table in the database. The request contains information about the item needed and is created by a
 * user.
 */
@Entity
@Table(name = "requests")
@Getter
@Setter
@Accessors(chain = true)
public class ItemRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(updatable = false, nullable = false)
  private Long id;

  @Column(name = "description", length = 2000, nullable = false)
  private String description;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "requestor_id")
  @ToString.Exclude
  private User requestor;

  @Column(name = "created", nullable = false)
  private LocalDateTime created = LocalDateTime.now();

}
