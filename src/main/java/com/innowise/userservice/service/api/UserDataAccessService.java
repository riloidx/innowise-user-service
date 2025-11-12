package com.innowise.userservice.service.api;

import com.innowise.userservice.entity.User;

public interface UserDataAccessService  {

    User findById(long id);

    User findByEmail(String email);
}
