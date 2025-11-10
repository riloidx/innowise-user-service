package com.innowise.userservice.service.api;

import com.innowise.userservice.dto.request.UserCreateDto;
import com.innowise.userservice.dto.request.UserUpdateDto;
import com.innowise.userservice.dto.response.UserResponseDto;

public interface UserCommandService {

    UserResponseDto create(UserCreateDto userCreateDto);

    UserResponseDto update(long id, UserUpdateDto userUpdateDto);

    void delete(long id);

    UserResponseDto changeStatus(long id, boolean status);
}