package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoWithItems;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTests {

    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;

    @MockBean
    ItemRequestServiceImpl service;

    @Test
    public void createRequestTest() throws Exception {
        CreateRequestDto request = requestDto();
        RequestDtoWithItems response = mapper.convertValue(request, RequestDtoWithItems.class);

        when(service.createRequest(any(), anyLong()))
                .thenReturn(response);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(response.getId()), Long.class))
                .andExpect(jsonPath("$.description", Matchers.is(response.getDescription())));

        verify(service, times(1)).createRequest(any(), anyLong());
    }

    @Test
    public void getRequestsByUserTest() throws Exception {
        CreateRequestDto request = requestDto();
        RequestDtoWithItems response = mapper.convertValue(request, RequestDtoWithItems.class);
        response.setId(1L);

        when(service.getRequestsByUser(anyLong()))
                .thenReturn(List.of(response));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Matchers.is(response.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", Matchers.is(response.getDescription())));

        verify(service, times(1)).getRequestsByUser(anyLong());
    }

    @Test
    public void getRequestByIdTest() throws Exception {
        CreateRequestDto request = requestDto();
        RequestDtoWithItems response = mapper.convertValue(request, RequestDtoWithItems.class);
        response.setId(1L);

        when(service.getRequestById(anyLong()))
                .thenReturn(response);

        mvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(response.getId()), Long.class))
                .andExpect(jsonPath("$.description", Matchers.is(response.getDescription())));

        verify(service, times(1)).getRequestById(anyLong());
    }

    @Test
    public void getAllRequestsTest() throws Exception {
        CreateRequestDto request = requestDto();
        RequestDto response = mapper.convertValue(request, RequestDto.class);
        response.setId(1L);

        when(service.getAllRequests())
                .thenReturn(List.of(response));

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Matchers.is(response.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", Matchers.is(response.getDescription())));

        verify(service, times(1)).getAllRequests();
    }

    private CreateRequestDto requestDto() {
        CreateRequestDto request = new CreateRequestDto();
        request.setDescription("jjjjjj");

        return request;
    }
}
