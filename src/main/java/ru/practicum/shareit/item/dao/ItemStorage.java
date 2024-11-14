package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage extends JpaRepository<Item, Long> {

    Optional<List<Item>> findByOwner(long owner);

    @Query("select i.id from Item i where i.owner = :ownerId")
    List<Long> findItemIdByOwnerId(long ownerId);

    @Query("select i from Item i " +
            "where upper(i.name) like upper(concat('%', :text, '%')) " +
            "   or upper(i.description) like upper(concat('%', :text, '%'))")
    Optional<List<Item>> findByNameOrDescriptionContainingIgnoreCase(String text);

    @Query("select i.owner from Item i where i.id = :itemId")
    Long findOwnerIdByItemId(long itemId);

}
