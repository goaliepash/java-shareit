package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.user.data.User;
import ru.practicum.shareit.user.data.dto.UserDto;
import ru.practicum.shareit.user.data.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        User user = repository.save(UserMapper.fromUserDto(userDto));
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto update(long userId, UserDto userDto) {
        User updatedUser = repository.getReferenceById(userId);
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            updatedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            updatedUser.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public UserDto get(long userId) {
        if (repository.existsById(userId)) {
            return UserMapper.toUserDto(repository.getReferenceById(userId));
        } else {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден.", userId));
        }
    }

    @Transactional
    @Override
    public void delete(long userId) {
        repository.deleteById(userId);
    }

    @Override
    public List<UserDto> get() {
        return repository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }
}