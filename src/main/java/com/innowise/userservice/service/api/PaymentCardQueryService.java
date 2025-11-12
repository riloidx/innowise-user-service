
package com.innowise.userservice.service.api;

import com.innowise.userservice.dto.response.PaymentCardResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface PaymentCardQueryService {

    PaymentCardResponseDto findDtoById(long id);

    Page<PaymentCardResponseDto> findAll(Boolean active,
                                         LocalDate expiresAfter,
                                         LocalDate expiresBefore,
                                         Pageable pageable);

    List<PaymentCardResponseDto> findAllByUserId(long userId);

}