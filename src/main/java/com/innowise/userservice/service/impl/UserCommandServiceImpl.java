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
import com.innowise.userservice.service.api.UserDataAccessService;
import com.innowise.userservice.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepo;
    private final UserDataAccessService userDataAccessService;
    private final UserMapper mapper;
    private final ValidationUtil validationUtil;

    @Override
    @CachePut(value = "user", key = "#result.id")
    public UserResponseDto create(UserCreateDto userCreateDto) {
        checkEmailNotTaken(userCreateDto.getEmail());
        User user = mapper.toEntity(userCreateDto);

        User savedUser = userRepo.save(user);

        return mapper.toDto(savedUser);
    }

    @Override
    @Transactional
    @CachePut(value = "user", key = "#result.id")
    public UserResponseDto update(long id, UserUpdateDto userUpdateDto) {
        User curUser = getValidatedUserForUpdate(id, userUpdateDto);
        mapper.updateEntityFromDto(userUpdateDto, curUser);

        return mapper.toDto(userRepo.save(curUser));
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "user", key = "#id", beforeInvocation = true),
                    @CacheEvict(value = "cards", key = "#id")
            })
    public void delete(long id) {
        userDataAccessService.findById(id);

        userRepo.deleteById(id);
    }

    @Override
    @Transactional
    @CachePut(value = "user", key = "#result.id")
    public UserResponseDto changeStatus(long id, boolean status) {
        User curUser = getValidatedUserForChangingStatus(id, status);

        curUser = userRepo.save(curUser);

        return mapper.toDto(curUser);
    }

    private void checkEmailNotTaken(String email) {
        userRepo.findByEmail(email)
                .ifPresent(u -> {
                    throw new UserAlreadyExistsException("email", email);
                });
    }

    private User getValidatedUserForUpdate(long id, UserUpdateDto userUpdateDto) {
        validationUtil.validateMatchingIds(id, userUpdateDto.getId());

        User curUser = userDataAccessService.findById(id);

        if (!curUser.getEmail().equals(userUpdateDto.getEmail())) {
            checkEmailNotTaken(userUpdateDto.getEmail());
        }

        return curUser;
    }

    private User getValidatedUserForChangingStatus(long id, boolean active) {
        User user = userDataAccessService.findById(id);

        if (active == user.getActive()) {
            throw new BadRequestException("User with id=" + id + " have status=" + (active ? "active" : "inactive"));
        }

        user.setActive(active);

        return user;
    }

}
