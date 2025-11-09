package com.innowise.userservice.service.impl;

import com.innowise.userservice.dto.response.PaymentCardResponseDto;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.exception.PaymentCardNotFoundException;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.service.api.PaymentCardQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentCardQueryServiceImpl implements PaymentCardQueryService {

    private final PaymentCardRepository paymentCardRepo;
    private final PaymentCardMapper mapper;

    @Override
    public PaymentCard findById(long id) {
        return paymentCardRepo.findById(id).
                orElseThrow(() -> new PaymentCardNotFoundException("id", String.valueOf(id)));
    }

    @Override
    public Page<PaymentCardResponseDto> findAll(Specification<PaymentCard> spec, Pageable pageable) {
        Page<PaymentCard> paymentCards = paymentCardRepo.findAll(spec, pageable);

        return mapper.toDto(paymentCards);
    }

    @Override
    public List<PaymentCardResponseDto> findAllByUserId(long userId) {
        List<PaymentCard> paymentCards = paymentCardRepo.findAllByUserId(userId);

        return mapper.toDto(paymentCards);
    }
}
