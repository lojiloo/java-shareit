package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CreateCommentDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTests {

    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;

    @MockBean
    ItemServiceImpl service;

    @Test
    public void addNewItemTest() throws Exception {
        CreateItemDto request = createItem();
        ItemDto response = mapper.convertValue(request, ItemDto.class);
        response.setId(1L);

        when(service.addNewItem(any(), anyLong()))
                .thenReturn(response);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(response.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(response.getName())))
                .andExpect(jsonPath("$.description", Matchers.is(response.getDescription())))
                .andExpect(jsonPath("$.available", Matchers.is(response.getAvailable()), Boolean.class));

        verify(service, times(1)).addNewItem(any(), anyLong());
    }

    @Test
    public void addNewCommentTest() throws Exception {
        CreateCommentDto request = createComment();
        CommentDto response = mapper.convertValue(request, CommentDto.class);
        response.setId(1L);
        response.setItemName("item");
        response.setAuthorName("author");

        when(service.addNewComment(any(), anyLong(), anyLong()))
                .thenReturn(response);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(request))
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(response.getId()), Long.class))
                .andExpect(jsonPath("$.text", Matchers.is(response.getText())))
                .andExpect(jsonPath("$.itemName", Matchers.is(response.getItemName())))
                .andExpect(jsonPath("$.authorName", Matchers.is(response.getAuthorName())));

        verify(service, times(1)).addNewComment(any(), anyLong(), anyLong());
    }

    @Test
    public void updateItemTest() throws Exception {
        UpdateItemDto request = updateItem();
        ItemDto response = mapper.convertValue(request, ItemDto.class);
        response.setId(1L);

        when(service.updateItem(any(), anyLong(), anyLong()))
                .thenReturn(response);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(response.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(response.getName())))
                .andExpect(jsonPath("$.description", Matchers.is(response.getDescription())))
                .andExpect(jsonPath("$.available", Matchers.is(response.getAvailable()), Boolean.class));

        verify(service, times(1)).updateItem(any(), anyLong(), anyLong());
    }

    @Test
    public void getItemByIdTest() throws Exception {
        CreateItemDto request = createItem();
        ItemDtoWithBookings response = mapper.convertValue(request, ItemDtoWithBookings.class);
        response.setId(1L);

        when(service.getItemById(anyLong()))
                .thenReturn(response);

        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(response.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(response.getName())))
                .andExpect(jsonPath("$.description", Matchers.is(response.getDescription())))
                .andExpect(jsonPath("$.available", Matchers.is(response.getAvailable()), Boolean.class));

        verify(service, times(1)).getItemById(anyLong());
    }

    @Test
    public void getAllItemsOfUserTest() throws Exception {
        CreateItemDto request = createItem();
        ItemDtoWithBookings response = mapper.convertValue(request, ItemDtoWithBookings.class);
        response.setId(1L);

        when(service.getAllItemsOfUser(anyLong()))
                .thenReturn(List.of(response));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Matchers.is(response.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", Matchers.is(response.getName())))
                .andExpect(jsonPath("$[0].description", Matchers.is(response.getDescription())))
                .andExpect(jsonPath("$[0].available", Matchers.is(response.getAvailable()), Boolean.class));

        verify(service, times(1)).getAllItemsOfUser(anyLong());
    }

    @Test
    public void searchItemTest() throws Exception {
        CreateItemDto request = createItem();
        ItemDto response = mapper.convertValue(request, ItemDto.class);
        response.setId(1L);

        when(service.searchItem(anyString()))
                .thenReturn(List.of(response));

        mvc.perform(get("/items/search")
                        .param("text", "item")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Matchers.is(response.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", Matchers.is(response.getName())))
                .andExpect(jsonPath("$[0].description", Matchers.is(response.getDescription())))
                .andExpect(jsonPath("$[0].available", Matchers.is(response.getAvailable()), Boolean.class));

        verify(service, times(1)).searchItem(anyString());
    }

    private CreateItemDto createItem() {
        CreateItemDto item = new CreateItemDto();
        item.setName("item name");
        item.setDescription("item description");
        item.setAvailable(true);

        return item;
    }

    private UpdateItemDto updateItem() {
        UpdateItemDto item = new UpdateItemDto();
        item.setName("item name");
        item.setDescription("item description");
        item.setAvailable(true);

        return item;
    }

    private CreateCommentDto createComment() {
        CreateCommentDto comment = new CreateCommentDto();
        comment.setText("wow");

        return comment;
    }
}
