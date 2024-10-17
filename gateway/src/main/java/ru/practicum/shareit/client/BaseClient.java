package ru.practicum.shareit.client;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.utils.HeaderConstants;

/**
 * An abstract class that provides common functionality for making RESTful HTTP requests.
 * <p>
 * It utilizes Spring's {@link RestTemplate} to communicate with remote services, offering support
 * for various HTTP methods like {@code GET}, {@code POST}, {@code PUT}, {@code PATCH}, and
 * {@code DELETE}.
 * <p>
 * The main method responsible for sending requests is
 * {@link #makeAndSendRequest(HttpMethod, String, Long, Map, Object)}, which is used internally by
 * the helper methods such as {@link #get(String, long)}, {@link #post(String, long, Object)},
 * {@link #patch(String, Long, Map, Object)}, and others. This method processes the actual request
 * and provides error handling, logging, and response preparation.
 * <p>
 * The class handles setting default headers {@link #defaultHeaders(Long)}, including user ID when
 * necessary.
 * <p>
 * It processes responses {@link #prepareGatewayResponse(ResponseEntity<Object>)}to ensure that HTTP
 * status codes and response bodies are handled appropriately.
 */
@Slf4j
public class BaseClient {

  protected final RestTemplate rest;

  public BaseClient(RestTemplate rest) {
    this.rest = rest;
  }

  protected ResponseEntity<Object> get(String path) {
    return get(path, null, null);
  }

  protected ResponseEntity<Object> get(String path, long userId) {
    return get(path, userId, null);
  }

  protected ResponseEntity<Object> get(String path, Long userId,
                                       @Nullable Map<String, Object> parameters) {
    return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null);
  }

  protected <T> ResponseEntity<Object> post(String path, T body) {
    return post(path, null, null, body);
  }

  protected <T> ResponseEntity<Object> post(String path, long userId, T body) {
    return post(path, userId, null, body);
  }

  protected <T> ResponseEntity<Object> post(String path, Long userId,
                                            @Nullable Map<String, Object> parameters, T body) {
    return makeAndSendRequest(HttpMethod.POST, path, userId, parameters, body);
  }

  protected <T> ResponseEntity<Object> put(String path, long userId, T body) {
    return put(path, userId, null, body);
  }

  protected <T> ResponseEntity<Object> put(String path, long userId,
                                           @Nullable Map<String, Object> parameters, T body) {
    return makeAndSendRequest(HttpMethod.PUT, path, userId, parameters, body);
  }

  protected <T> ResponseEntity<Object> patch(String path, T body) {
    return patch(path, null, null, body);
  }

  protected <T> ResponseEntity<Object> patch(String path, long userId) {
    return patch(path, userId, null, null);
  }

  protected <T> ResponseEntity<Object> patch(String path, long userId, T body) {
    return patch(path, userId, null, body);
  }

  protected <T> ResponseEntity<Object> patch(String path, Long userId,
                                             @Nullable Map<String, Object> parameters, T body) {
    return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body);
  }

  protected ResponseEntity<Object> delete(String path) {
    return delete(path, null, null);
  }

  protected ResponseEntity<Object> delete(String path, long userId) {
    return delete(path, userId, null);
  }

  protected ResponseEntity<Object> delete(String path, Long userId,
                                          @Nullable Map<String, Object> parameters) {
    return makeAndSendRequest(HttpMethod.DELETE, path, userId, parameters, null);
  }

  private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, Long userId,
                                                        @Nullable Map<String, Object> parameters,
                                                        @Nullable T body) {
    log.debug("Sending the request to the ShareIt Server.");
    HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

    ResponseEntity<Object> shareitServerResponse;
    try {
      if (parameters != null) {
        shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class,
            parameters);
      } else {
        shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class);
      }
    } catch (HttpStatusCodeException e) {
      return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
    }
    return prepareGatewayResponse(shareitServerResponse);
  }

  private HttpHeaders defaultHeaders(Long userId) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    if (userId != null) {
      headers.set(HeaderConstants.USER_ID_HEADER, String.valueOf(userId));
    }
    return headers;
  }

  private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
    log.debug("Response HTTP Status Code:{}", response.getStatusCode());
    log.debug("Response body: {}", response.getBody());
    log.debug("Response headers: {}", response.getHeaders());
    if (response.getStatusCode().is2xxSuccessful()) {
      return response;
    }

    ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

    if (response.hasBody()) {
      return responseBuilder.body(response.getBody());
    }

    return responseBuilder.build();
  }
}
