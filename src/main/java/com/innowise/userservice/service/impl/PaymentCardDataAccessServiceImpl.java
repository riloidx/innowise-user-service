package com.innowise.userservice.service.impl;

import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.exception.PaymentCardNotFoundException;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.service.api.PaymentCardDataAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentCardDataAccessServiceImpl implements PaymentCardDataAccessService {

    private final PaymentCardRepository paymentCardRepo;

    @Override
    public PaymentCard findById(long id) {
        return paymentCardRepo.findById(id).
                orElseThrow(() -> new PaymentCardNotFoundException("id", String.valueOf(id)));
    }
}
