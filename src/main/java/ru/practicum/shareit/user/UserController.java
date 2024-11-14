package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping
    public UserDto createUser(@RequestBody @Valid CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody @Valid UpdateUserRequest request,
                              @PathVariable long userId) {
        return userService.updateUser(request, userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUserDtoById(@PathVariable long userId) {
        return userService.getUserDtoById(userId);
    }

    @GetMapping
    public List<UserDto> getAllDtos() {
        return userService.getAllDtos();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
    }
}
