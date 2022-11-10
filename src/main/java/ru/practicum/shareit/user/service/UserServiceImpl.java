package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.data.User;
import ru.practicum.shareit.user.data.dto.UserDto;
import ru.practicum.shareit.user.data.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public UserDto create(UserDto userDto) {
        User user = repository.save(UserMapper.fromUserDto(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        User updatedUser = repository.getReferenceById(userId);
        if (userDto.getName() != null) {
            updatedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            updatedUser.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(repository.save(updatedUser));
    }

    @Override
    public UserDto get(long userId) {
        try {
            return UserMapper.toUserDto(repository.getReferenceById(userId));
        } catch (EntityNotFoundException exception) {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден.", userId));
        }
    }

    @Override
    public void delete(long userId) {
        repository.deleteById(userId);
    }

    @Override
    public List<UserDto> get() {
        return repository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }
}