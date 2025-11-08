package com.innowise.userservice.service.api;

import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;

public interface UserCommandService {

    User create(User user);

    User update(long id, User user);

    void delete(long id);

    User activate(long id);

    User deactivate(long id);
}
