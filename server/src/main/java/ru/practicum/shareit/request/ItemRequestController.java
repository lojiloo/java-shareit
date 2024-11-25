package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoWithItems;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestServiceImpl requestService;

    @PostMapping
    public RequestDtoWithItems createRequest(@RequestBody CreateRequestDto request,
                                             @RequestHeader("X-Sharer-User-Id") long requesterId) {
        return requestService.createRequest(request, requesterId);
    }

    @GetMapping
    public List<RequestDtoWithItems> getRequestsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getRequestsByUser(userId);
    }

    @GetMapping("/{requestId}")
    public RequestDtoWithItems getRequestById(@PathVariable long requestId) {
        return requestService.getRequestById(requestId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAllRequests() {
        return requestService.getAllRequests();
    }

}
