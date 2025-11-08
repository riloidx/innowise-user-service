package com.innowise.userservice.service.impl;

import com.innowise.userservice.dto.request.UserCreateDto;
import com.innowise.userservice.dto.request.UserUpdateDto;
import com.innowise.userservice.dto.response.UserResponseDto;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.UserAlreadyExistsException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.api.UserCommandService;
import com.innowise.userservice.service.api.UserQueryService;
import com.innowise.userservice.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepo;
    private final UserQueryService userQueryService;
    private final UserMapper mapper;
    private final ValidationUtil  validationUtil;

    @Override
    public UserResponseDto create(UserCreateDto userCreateDto) {
        User user = mapper.toEntity(userCreateDto);

        User savedUser = userRepo.save(user);

        return mapper.toDto(savedUser);
    }

    @Override
    public UserResponseDto update(long id, UserUpdateDto userUpdateDto) {
        validationUtil.validateMatchingIds(id, userUpdateDto.getId());

        User curUser = userQueryService.findById(id);

        return mapper.toDto(curUser);
    }

    @Override
    public void delete(long id) {
        userRepo.deleteById(id);
    }

    @Override
    public UserResponseDto changeStatus(long id, boolean status) {
        User curUser = userQueryService.findById(id);
        curUser.setActive(status);

        curUser = userRepo.save(curUser);

        return mapper.toDto(curUser);
    }

    public void isUserExistsByEmail(String email) {
        if (userRepo.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("email", email);
        }
    }
}
