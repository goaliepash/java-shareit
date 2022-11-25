package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.data.dto.CommentDto;
import ru.practicum.shareit.item.data.dto.CommentRequestDto;
import ru.practicum.shareit.item.data.dto.StandardItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController itemController;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(itemController).build();
    }

    @Test
    void testCreate() throws Exception {
        StandardItemDto itemDto = new StandardItemDto(
                1L,
                "Name",
                "Description",
                true,
                null,
                null,
                List.of(),
                1L
        );
        Mockito.when(itemService.create(Mockito.anyLong(), Mockito.any())).thenReturn(itemDto);
        mvc
                .perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void testUpdate() throws Exception {
        StandardItemDto itemDto = new StandardItemDto(
                1L,
                "Name",
                "Description",
                true,
                null,
                null,
                List.of(),
                1L
        );
        Mockito.when(itemService.update(Mockito.anyLong(), Mockito.anyLong(), Mockito.any())).thenReturn(itemDto);
        mvc
                .perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void testGet() throws Exception {
        StandardItemDto itemDto = new StandardItemDto(
                1L,
                "Name",
                "Description",
                true,
                null,
                null,
                List.of(),
                1L
        );
        Mockito.when(itemService.get(Mockito.anyLong(), Mockito.anyLong())).thenReturn(itemDto);
        mvc
                .perform(get("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void testGetAll() throws Exception {
        StandardItemDto itemDto1 = new StandardItemDto(
                1L,
                "Name",
                "Description",
                true,
                null,
                null,
                List.of(),
                1L
        );
        StandardItemDto itemDto2 = new StandardItemDto(
                2L,
                "Name 2",
                "Description 2",
                true,
                null,
                null,
                List.of(),
                2L
        );
        Mockito.when(itemService.get(Mockito.anyLong())).thenReturn(List.of(itemDto1, itemDto2));
        mvc
                .perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    void search() throws Exception {
        StandardItemDto itemDto1 = new StandardItemDto(
                1L,
                "Name",
                "Description",
                true,
                null,
                null,
                List.of(),
                1L
        );
        StandardItemDto itemDto2 = new StandardItemDto(
                2L,
                "Name 2",
                "Description 2",
                true,
                null,
                null,
                List.of(),
                2L
        );
        Mockito.when(itemService.search(Mockito.anyString())).thenReturn(List.of(itemDto1, itemDto2));
        mvc
                .perform(get("/items/search?text=Name")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    void searchEmptyList() throws Exception {
        Mockito.when(itemService.search(Mockito.anyString())).thenReturn(List.of());
        mvc
                .perform(get("/items/search?text=Name")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    void testAddComment() throws Exception {
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("Text");
        CommentDto commentDto = new CommentDto(1L, "Text", "Name", LocalDateTime.now());
        Mockito.when(itemService.addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any())).thenReturn(commentDto);
        mvc
                .perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentRequestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }
}