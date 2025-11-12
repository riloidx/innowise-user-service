package com.innowise.userservice.service.impl;

import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.api.UserDataAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDataAccessServiceImpl implements UserDataAccessService {

    private final UserRepository userRepo;

    @Override
    @Cacheable(value = "user", key = "#id")
    public User findById(long id) {
        return userRepo.findById(id).
                orElseThrow(() -> new UserNotFoundException("id", String.valueOf(id)));
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email).
                orElseThrow(() -> new UserNotFoundException("email", email));
    }
}
