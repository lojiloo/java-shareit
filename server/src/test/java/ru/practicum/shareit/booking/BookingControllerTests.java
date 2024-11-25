package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTests {

    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;

    @MockBean
    BookingServiceImpl service;

    @Test
    public void addNewBookingTest() throws Exception {
        CreateBookingDto request = createBooking();
        BookingDto response = mapper.convertValue(request, BookingDto.class);
        response.setId(1L);

        when(service.addNewBooking(any(), anyLong()))
                .thenReturn(response);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(response.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", Matchers.is(response.getItemId()), Long.class));

        verify(service, times(1)).addNewBooking(any(), anyLong());
    }

    @Test
    public void updateBookingStatusTest() throws Exception {
        CreateBookingDto request = createBooking();
        BookingDto response = mapper.convertValue(request, BookingDto.class);
        response.setId(1L);
        response.setStatus(Status.APPROVED);

        when(service.updateBookingStatus(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(response);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(response.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", Matchers.is(response.getItemId()), Long.class))
                .andExpect(jsonPath("$.status", Matchers.is(String.valueOf(response.getStatus()))));

        verify(service, times(1)).updateBookingStatus(anyLong(), anyBoolean(), anyLong());
    }

    @Test
    public void getBookingInfoByIdTest() throws Exception {
        CreateBookingDto request = createBooking();
        BookingDto response = mapper.convertValue(request, BookingDto.class);
        response.setId(1L);

        when(service.getBookingInfoById(anyLong(), anyLong()))
                .thenReturn(response);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(response.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", Matchers.is(response.getItemId()), Long.class));

        verify(service, times(1)).getBookingInfoById(anyLong(), anyLong());
    }

    @Test
    public void findByStateForBookerTest() throws Exception {
        CreateBookingDto request = createBooking();
        BookingDto response = mapper.convertValue(request, BookingDto.class);
        response.setId(1L);
        response.setStatus(Status.REJECTED);

        when(service.findByStateForBooker(any(), anyLong()))
                .thenReturn(List.of(response));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", String.valueOf(Status.REJECTED))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Matchers.is(response.getId()), Long.class))
                .andExpect(jsonPath("$[0].itemId", Matchers.is(response.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].status", Matchers.is(String.valueOf(response.getStatus()))));

        verify(service, times(1)).findByStateForBooker(any(), anyLong());
    }

    @Test
    public void findByStateForOwnerTest() throws Exception {
        CreateBookingDto request = createBooking();
        BookingDto response = mapper.convertValue(request, BookingDto.class);
        response.setId(1L);
        response.setStatus(Status.REJECTED);

        when(service.findByStateForOwner(any(), anyLong()))
                .thenReturn(List.of(response));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", String.valueOf(Status.REJECTED))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Matchers.is(response.getId()), Long.class))
                .andExpect(jsonPath("$[0].itemId", Matchers.is(response.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].status", Matchers.is(String.valueOf(response.getStatus()))));

        verify(service, times(1)).findByStateForOwner(any(), anyLong());
    }

    private CreateBookingDto createBooking() {
        CreateBookingDto booking = new CreateBookingDto();
        booking.setItemId(1L);

        return booking;
    }
}
