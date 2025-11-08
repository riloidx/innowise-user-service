package com.innowise.userservice.service.impl;

import com.innowise.userservice.dto.response.UserResponseDto;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.api.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepo;
    private final UserMapper mapper;

    @Override
    public User findById(long id) {
        return userRepo.findById(id).
                orElseThrow(() -> new UserNotFoundException("id", String.valueOf(id)));
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email).
                orElseThrow(() -> new UserNotFoundException("email", email));
    }

    @Override
    public Page<UserResponseDto> findAll(Specification<User> spec, Pageable pageable) {
        Page<User> users = userRepo.findAll(spec, pageable);

        return mapper.toDto(users);
    }
}
