package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoWithItems;

import java.util.List;

public interface ItemRequestService {
    RequestDtoWithItems createRequest(CreateRequestDto request, long requesterId);

    List<RequestDtoWithItems> getRequestsByUser(long userId);

    RequestDtoWithItems getRequestById(long requestId);

    List<RequestDto> getAllRequests();

}
