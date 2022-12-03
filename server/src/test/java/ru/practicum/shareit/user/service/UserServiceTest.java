package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.user.data.User;
import ru.practicum.shareit.user.data.dto.UserDto;
import ru.practicum.shareit.user.data.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    private void initUserService() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void testCreate() {
        User mockCreatedUser = new User();
        mockCreatedUser.setId(1L);
        mockCreatedUser.setName("Pavel");
        mockCreatedUser.setEmail("pavel@mail.ru");

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(mockCreatedUser);

        UserDto expectedUserDto = UserMapper.toUserDto(mockCreatedUser);
        UserDto actualUserDto = userService.create(new UserDto(1L, "Pavel", "pavel@mail.ru"));

        Assertions.assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    void testUpdate() {
        User mockUpdatedUser = new User();
        mockUpdatedUser.setId(1L);
        mockUpdatedUser.setName("Pavel");
        mockUpdatedUser.setEmail("pavel@mail.ru");

        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(mockUpdatedUser);

        UserDto expectedUserDto = new UserDto(1L, "Pavel Update", "pavelupdate@mail.ru");
        UserDto actualUserDto = userService.update(1L, new UserDto(1L, "Pavel Update", "pavelupdate@mail.ru"));

        Assertions.assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    void testGet() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Pavel");
        mockUser.setEmail("pavel@mail.ru");

        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(userRepository.getReferenceById(1L)).thenReturn(mockUser);

        UserDto expectedUserDto = new UserDto(1L, "Pavel", "pavel@mail.ru");
        UserDto actualUserDto = userService.get(1);

        Assertions.assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    void testGetWithWrongUser() {
        Mockito.when(userRepository.existsById(99L)).thenReturn(false);

        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> userService.get(99L)
        );

        Assertions.assertEquals("Пользователь с идентификатором 99 не найден.", exception.getMessage());
    }

    @Test
    void testDelete() {
        userService.delete(Mockito.anyLong());
    }

    @Test
    void testGetAll() {
        User mockUser1 = new User();
        mockUser1.setId(1L);
        mockUser1.setName("Pavel1");
        mockUser1.setEmail("pavel1@mail.ru");

        User mockUser2 = new User();
        mockUser2.setId(2L);
        mockUser2.setName("Pavel2");
        mockUser2.setEmail("pavel2@mail.ru");

        Mockito.when(userRepository.findAll()).thenReturn(List.of(mockUser1, mockUser2));

        List<UserDto> expectedUsers = List.of(UserMapper.toUserDto(mockUser1), UserMapper.toUserDto(mockUser2));
        List<UserDto> actualUsers = userService.get();

        Assertions.assertEquals(expectedUsers, actualUsers);
    }
}