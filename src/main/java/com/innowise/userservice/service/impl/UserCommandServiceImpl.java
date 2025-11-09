package com.innowise.userservice.service.impl;

import com.innowise.userservice.dto.request.UserCreateDto;
import com.innowise.userservice.dto.request.UserUpdateDto;
import com.innowise.userservice.dto.response.UserResponseDto;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.BadRequestException;
import com.innowise.userservice.exception.UserAlreadyExistsException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.api.UserCommandService;
import com.innowise.userservice.service.api.UserQueryService;
import com.innowise.userservice.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepo;
    private final UserQueryService userQueryService;
    private final UserMapper mapper;
    private final ValidationUtil  validationUtil;

    @Override
    public UserResponseDto create(UserCreateDto userCreateDto) {
        checkEmailNotTaken(userCreateDto.getEmail());
        User user = mapper.toEntity(userCreateDto);

        User savedUser = userRepo.save(user);

        return mapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public UserResponseDto update(long id, UserUpdateDto userUpdateDto) {
        User curUser = getValidatedUserForUpdate(id, userUpdateDto);
        mapper.updateEntityFromDto(userUpdateDto, curUser);

        return mapper.toDto(userRepo.save(curUser));
    }

    @Override
    @Transactional
    public void delete(long id) {
        userQueryService.findById(id);

        userRepo.deleteById(id);
    }

    @Override
    @Transactional
    public UserResponseDto changeStatus(long id, boolean status) {
        User curUser = userQueryService.findById(id);
        curUser.setActive(status);

        curUser = userRepo.save(curUser);

        return mapper.toDto(curUser);
    }

    private void checkEmailNotTaken(String email) {
        userRepo.findByEmail(email)
                .ifPresent(u -> { throw new UserAlreadyExistsException("email", email); });
    }

    private User getValidatedUserForUpdate(long id, UserUpdateDto userUpdateDto) {
        validationUtil.validateMatchingIds(id, userUpdateDto.getId());

        User curUser = userQueryService.findById(id);

        if (!curUser.getEmail().equals(userUpdateDto.getEmail())) {
            checkEmailNotTaken(userUpdateDto.getEmail());
        }

        return curUser;
    }

    private User getValidatedUserForChangingStatus(long id, boolean active) {
        User user = userQueryService.findById(id);

        if (active == user.getActive()) {
            throw new BadRequestException("Card with id=" + id + " have status=" + (active ? "active" : "inactive"));
        }

        user.setActive(active);

        return user;
    }

}
