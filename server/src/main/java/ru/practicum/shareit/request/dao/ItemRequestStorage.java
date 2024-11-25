package ru.practicum.shareit.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface ItemRequestStorage extends JpaRepository<Request, Long> {

    List<Request> findByRequesterIdOrderByCreated(long requesterId);

}
