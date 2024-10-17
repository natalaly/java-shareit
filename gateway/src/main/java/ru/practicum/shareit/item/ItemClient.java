package ru.practicum.shareit.item;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

/**
 * ItemClient is responsible for forwarding item-related requests from the gateway to the main
 * application (shareIt-server). It extends {@link BaseClient} to leverage common HTTP request
 * functionalities.
 */
@Service
public class ItemClient extends BaseClient {

  private static final String API_PREFIX = "/items";

  @Autowired
  public ItemClient(@Value("${shareit-server.url}") String serverUrl,
                    RestTemplateBuilder builder) {
    super(builder
        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
        .build()
    );
  }

  public ResponseEntity<Object> addItem(Long userId, ItemDto itemDto) {
    return post("", userId, itemDto);
  }

  public ResponseEntity<Object> updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
    String path = String.format("/%s", itemId);
    return patch(path, ownerId, itemDto);
  }

  public ResponseEntity<Object> getUserItems(Long ownerId) {
    return get("", ownerId);
  }

  public ResponseEntity<Object> getItemById(Long userId, Long itemId) {
    String path = String.format("/%s", itemId);
    return get(path, userId);
  }

  public ResponseEntity<Object> search(Long userId, String text) {
    String path = "/search?text={text}";
    Map<String, Object> parameters = Map.of("text", text);
    return get(path, userId, parameters);
  }

  public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDto commentDto) {
    String path = String.format("/%s/comment", itemId);
    return post(path, userId, commentDto);
  }

}
