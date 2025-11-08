package com.innowise.userservice.service.api;

import com.innowise.userservice.dto.response.UserResponseDto;
import com.innowise.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UserQueryService {

    User findById(long id);

    User findByEmail(String email);

    Page<UserResponseDto> findAll(Specification<User> spec, Pageable pageable);
}