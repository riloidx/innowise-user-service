package com.innowise.userservice.service.api;

import com.innowise.userservice.dto.response.PaymentCardResponseDto;
import com.innowise.userservice.entity.PaymentCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface PaymentCardQueryService {

    PaymentCard findById(long id);

    Page<PaymentCardResponseDto> findAll(Specification<PaymentCard> spec, Pageable pageable);

    List<PaymentCardResponseDto> findAllByUserId(long userId);

}
