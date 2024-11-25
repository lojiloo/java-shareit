package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final ModelMapper mapper;

    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final BookingStorage bookingStorage;

    @Override
    public BookingDto addNewBooking(CreateBookingDto request, long bookerId) {
        User user = userStorage.findById(bookerId).orElseThrow(
                () -> new NotFoundException("Пользователь с id " + bookerId + " не найден")
        );
        Item item = itemStorage.findById(request.getItemId()).orElseThrow(
                () -> new NotFoundException("По вашему запросу ничего не удалось найти")
        );
        if (!item.getAvailable()) {
            throw new BadRequestException("Данная вещь пока не может быть забронирована");
        }

        Booking booking = mapper.map(request, Booking.class);

        booking.setStatus(Status.WAITING);
        booking.setBooker(user);
        booking.setItem(item);

        return mapper.map(bookingStorage.save(booking), BookingDto.class);
    }

    @Override
    public BookingDto updateBookingStatus(long bookingId, boolean approved, long ownerId) {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Бронирование не найдено")
        );

        if (!booking.getItem().getOwner().equals(ownerId)) {
            throw new ForbiddenException("Изменить статус бронирования может только владелец вещи");
        }

        Status status = approved ? Status.APPROVED : Status.REJECTED;
        booking.setStatus(status);

        return mapper.map(bookingStorage.save(booking), BookingDto.class);
    }

    @Override
    public BookingDto getBookingInfoById(long bookingId, long userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        } else if (!bookingStorage.existsById(bookingId)) {
            throw new NotFoundException("Бронирование не найдено");
        }

        long itemId = bookingStorage.findItemIdByBookingId(bookingId);
        long ownerId = itemStorage.findOwnerIdByItemId(itemId);
        long bookerId = bookingStorage.getReferenceById(bookingId).getBooker().getId();

        if (userId != ownerId && userId != bookerId) {
            throw new ForbiddenException("Наш сервис не предоставляет информацию по бронированию третьим лицам");
        }

        return mapper.map(bookingStorage.getReferenceById(bookingId), BookingDto.class);
    }

    @Override
    public List<BookingDto> findByStateForBooker(State state, long bookerId) {
        if (!userStorage.existsById(bookerId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        List<Booking> resultBooking = new ArrayList<>();
        LocalDateTime today = LocalDateTime.now();
        switch (state) {
            case ALL:
                resultBooking.addAll(bookingStorage.findByBookerId(bookerId));
                break;
            case PAST:
                resultBooking.addAll(bookingStorage.findPastByBookerId(bookerId, today));
                break;
            case CURRENT:
                resultBooking.addAll(bookingStorage.findCurrentByBookerId(bookerId, today));
                break;
            case FUTURE:
                resultBooking.addAll(bookingStorage.findFutureByBookerId(bookerId, today));
                break;
            case WAITING:
                resultBooking.addAll(bookingStorage.findByStatusEqualsAndBookerIdEqualsOrderByStartDesc(Status.WAITING, bookerId));
                break;
            case REJECTED:
                resultBooking.addAll(bookingStorage.findByStatusEqualsAndBookerIdEqualsOrderByStartDesc(Status.REJECTED, bookerId));
                break;
        }

        if (resultBooking.isEmpty()) {
            throw new NotFoundException("Бронирований с данным статусом не найдено");
        }

        return mapBookingListToBookingDtoList(resultBooking);
    }

    @Override
    public List<BookingDto> findByStateForOwner(State state, long ownerId) {
        List<Long> itemIds = itemStorage.findItemIdByOwnerId(ownerId);
        if (itemIds.isEmpty()) {
            throw new NotFoundException("В сервис ещё не добавлено ни одной вещи к бронированию");
        }

        List<Booking> resultBooking = new ArrayList<>();
        LocalDateTime today = LocalDateTime.now();
        switch (state) {
            case ALL:
                resultBooking.addAll(bookingStorage.findByItemIds(itemIds));
                break;
            case PAST:
                resultBooking.addAll(bookingStorage.findPastByItemIds(itemIds, today));
                break;
            case CURRENT:
                resultBooking.addAll(bookingStorage.findCurrentByItemIds(itemIds, today));
                break;
            case FUTURE:
                resultBooking.addAll(bookingStorage.findFutureByItemIds(itemIds, today));
                break;
            case WAITING:
                resultBooking.addAll(bookingStorage.findByStatusEqualsAndItemIdInOrderByStartDesc(Status.WAITING, itemIds));
                break;
            case REJECTED:
                resultBooking.addAll(bookingStorage.findByStatusEqualsAndItemIdInOrderByStartDesc(Status.REJECTED, itemIds));
                break;
        }

        if (resultBooking.isEmpty()) {
            throw new NotFoundException("Бронирований с данным статусом не найдено");
        }

        return mapBookingListToBookingDtoList(resultBooking);
    }

    private List<BookingDto> mapBookingListToBookingDtoList(List<Booking> bookings) {
        List<BookingDto> bookingDtos = new ArrayList<>();
        for (int i = 0; i < bookings.size(); i++) {
            bookingDtos.add(mapper.map(bookings.get(i), BookingDto.class));
        }

        return bookingDtos;
    }

}
