package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDtoForItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestStorage;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoWithItems;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ModelMapper mapper;

    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final ItemRequestStorage itemRequestStorage;

    @Override
    public RequestDtoWithItems createRequest(CreateRequestDto createRequestDto, long requesterId) {
        User user = userStorage.findById(requesterId).orElseThrow(
                () -> new NotFoundException("Пользователь с id " + requesterId + " не найден")
        );

        Request request = mapper.map(createRequestDto, Request.class);

        request.setRequester(user);
        request.setCreated(LocalDateTime.now());

        return mapper.map(itemRequestStorage.save(request), RequestDtoWithItems.class);
    }

    @Override
    public List<RequestDtoWithItems> getRequestsByUser(long userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        List<Request> requests = itemRequestStorage.findByRequesterIdOrderByCreated(userId);
        if (requests.isEmpty()) {
            throw new NotFoundException("У вас ещё не создано ни одного запроса к аренде");
        }
        List<Long> requestIds = requests.stream().map(Request::getId).toList();

        Map<Long, List<Item>> itemsSuggested = itemStorage.findByRequestIdIn(requestIds)
                .stream()
                .collect(Collectors.groupingBy(item -> item.getRequestId()));

        List<RequestDtoWithItems> dtos = new ArrayList<>();
        for (Request request : requests) {
            dtos.add(mapper.map(request, RequestDtoWithItems.class));
        }

        for (RequestDtoWithItems dto : dtos) {
            long id = dto.getId();

            if (itemsSuggested.containsKey(id)) {
                dto.setItems(itemsSuggested.get(id)
                        .stream()
                        .map(item -> mapper.map(item, ItemDtoForItemRequest.class))
                        .toList());
            }
        }

        return dtos;
    }

    @Override
    public RequestDtoWithItems getRequestById(long requestId) {
        Request request = itemRequestStorage.findById(requestId).orElseThrow(
                () -> new NotFoundException("Не удалось найти запрос с id " + requestId)
        );
        RequestDtoWithItems dto = mapper.map(request, RequestDtoWithItems.class);

        List<ItemDtoForItemRequest> itemsSuggested = itemStorage.findByRequestId(requestId).stream()
                .map(item -> mapper.map(item, ItemDtoForItemRequest.class))
                .toList();
        if (!itemsSuggested.isEmpty()) {
            dto.setItems(itemsSuggested);
        }

        return dto;
    }

    @Override
    public List<RequestDto> getAllRequests() {
        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        Pageable pageable = PageRequest.of(0, 20).withSort(sort);
        Page<Request> requests = itemRequestStorage.findAll(pageable);

        return requests.getContent()
                .stream()
                .map(request -> mapper.map(request, RequestDto.class))
                .toList();
    }
}
