package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.StandardItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(long userId, StandardItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> update(long itemId, long userId, StandardItemDto itemDto) {
        return patch(String.format("/%d", itemId), userId, itemDto);
    }

    public ResponseEntity<Object> get(long itemId, long ownerId) {
        return get(String.format("/%d", itemId), ownerId);
    }

    public ResponseEntity<Object> get(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> search(String text) {
        Map<String, Object> parameters = Map.of("text", text);
        return get("/search?text={text}", null, parameters);
    }

    public ResponseEntity<Object> addComment(long itemId, long userId, CommentRequestDto commentRequestDto) {
        return post(String.format("/%d/comment", itemId), userId, commentRequestDto);
    }
}