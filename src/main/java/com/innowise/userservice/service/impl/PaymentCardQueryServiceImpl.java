package com.innowise.userservice.service.impl;

import com.innowise.userservice.dto.response.PaymentCardResponseDto;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.exception.PaymentCardNotFoundException;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.service.api.PaymentCardQueryService;
import com.innowise.userservice.specification.PaymentCardSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    public PaymentCardResponseDto findDtoById(long id) {
        return mapper.toDto(findById(id));
    }

    @Override
    public Page<PaymentCardResponseDto> findAll(Boolean active,
                                                LocalDate expiresAfter,
                                                LocalDate expiresBefore,
                                                Pageable pageable) {
        Specification<PaymentCard> spec = configureSpecification(active,
                expiresAfter,
                expiresBefore);

        Page<PaymentCard> paymentCards = paymentCardRepo.findAll(spec, pageable);

        return mapper.toDto(paymentCards);
    }

    @Override
    public List<PaymentCardResponseDto> findAllByUserId(long userId) {
        List<PaymentCard> paymentCards = paymentCardRepo.findAllByUserId(userId);

        return mapper.toDto(paymentCards);
    }

    private Specification<PaymentCard> configureSpecification(Boolean active,
                                                              LocalDate expiresAfter,
                                                              LocalDate expiresBefore) {

        Specification<PaymentCard> spec = Specification.unrestricted();

        if (active != null) {
            spec = spec.and(PaymentCardSpecification.isActive(active));
        }

        if (expiresAfter != null) {
            spec = spec.and(PaymentCardSpecification.expiresAfter(expiresAfter));
        }

        if (expiresBefore != null) {
            spec = spec.and(PaymentCardSpecification.expiresBefore(expiresBefore));
        }

        return spec;

    }

}