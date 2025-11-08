package com.innowise.userservice.service.api;

import com.innowise.userservice.entity.PaymentCard;

public interface PaymentCardCommandService {

    PaymentCard create(PaymentCard paymentCard);

    PaymentCard update(long id, PaymentCard paymentCard);

    void delete(long id);

    PaymentCard activate(long id);

    PaymentCard deactivate(long id);
}
