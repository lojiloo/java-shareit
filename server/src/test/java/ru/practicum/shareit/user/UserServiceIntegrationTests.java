package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTests {

    private final UserService service;

    @Test
    public void createNewUserTest() {
        CreateUserDto request1 = new CreateUserDto();
        request1.setName("Alla Pugacheva");
        request1.setEmail("arlekino@ah.com");

        UserDto userDto = service.createUser(request1);

        assertThat(userDto.getName(), equalTo(request1.getName()));
        assertThat(userDto.getEmail(), equalTo(request1.getEmail()));

        CreateUserDto request2 = new CreateUserDto();
        request2.setName("Maxim Galkin");
        request2.setEmail("arlekino@ah.com");
        assertThrows(ConflictException.class, () -> service.createUser(request2));
    }

    @Test
    public void updateUserTest() {
        CreateUserDto request1 = new CreateUserDto();
        request1.setName("Johnny Depp");
        request1.setEmail("sgduFYta6@sr1.test");
        UserDto dto1 = service.createUser(request1);

        CreateUserDto request2 = new CreateUserDto();
        request2.setName("John Cena");
        request2.setEmail("sa6@sr1.test");
        UserDto dto2 = service.createUser(request2);

        UpdateUserDto request3 = new UpdateUserDto();
        request3.setEmail("sgduFYta6@sr1.test");

        assertThrows(ConflictException.class, () -> service.updateUser(request3, dto2.getId()));
    }
}

