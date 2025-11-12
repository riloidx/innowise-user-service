package com.innowise.userservice.service.api;

import com.innowise.userservice.entity.PaymentCard;

public interface PaymentCardDataAccessService {

    PaymentCard findById(long id);
}
