package com.innowise.userservice.service.api;

import com.innowise.userservice.dto.response.UserResponseDto;
import com.innowise.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface UserQueryService {

    User findById(long id);

    UserResponseDto findDtoById(long id);

    UserResponseDto findDtoByEmail(String email);

    Page<UserResponseDto> findAll(String name,
                                  String surname,
                                  LocalDate birthDate,
                                  Boolean active,
                                  Pageable pageable);
}