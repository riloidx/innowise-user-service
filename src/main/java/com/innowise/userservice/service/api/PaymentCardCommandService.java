package com.innowise.userservice.service.api;

import com.innowise.userservice.dto.request.PaymentCardCreateDto;
import com.innowise.userservice.dto.request.PaymentCardUpdateDto;
import com.innowise.userservice.dto.response.PaymentCardResponseDto;

public interface PaymentCardCommandService {

    PaymentCardResponseDto create(PaymentCardCreateDto paymentCardCreateDto);

    PaymentCardResponseDto update(long id, PaymentCardUpdateDto paymentCardUpdateDto);

    void delete(long id);

    PaymentCardResponseDto changeStatus(long id, boolean status);
}