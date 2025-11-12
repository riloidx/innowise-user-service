package com.innowise.userservice.service;

import com.innowise.userservice.dto.response.PaymentCardResponseDto;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.service.api.PaymentCardDataAccessService;
import com.innowise.userservice.service.impl.PaymentCardQueryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCardQueryServiceImplTest {

    @Mock
    private PaymentCardRepository paymentCardRepo;

    @Mock
    private PaymentCardDataAccessService paymentCardDataAccessService;

    @Mock
    private PaymentCardMapper mapper;

    @InjectMocks
    private PaymentCardQueryServiceImpl service;

    @Mock
    private Pageable pageable;

    @Test
    void findDtoById_shouldReturnDto_whenCardExists() {
        long id = 1L;
        PaymentCard card = PaymentCard.builder().id(id).build();
        PaymentCardResponseDto dto = PaymentCardResponseDto.builder().id(id).build();

        when(paymentCardDataAccessService.findById(id)).thenReturn(card);
        when(mapper.toDto(card)).thenReturn(dto);

        PaymentCardResponseDto result = service.findDtoById(id);

        assertEquals(dto, result);
        verify(paymentCardDataAccessService).findById(id);
        verify(mapper).toDto(card);
    }

    @Test
    void findAll_shouldReturnPage_whenSpecProvided() {
        Boolean active = true;
        LocalDate expiresAfter = LocalDate.of(2025, 1, 1);
        LocalDate expiresBefore = LocalDate.of(2025, 12, 31);

        PaymentCard card = PaymentCard.builder().id(1L).build();
        Page<PaymentCard> cardPage = new PageImpl<>(List.of(card));
        Page<PaymentCardResponseDto> dtoPage = new PageImpl<>(List.of(PaymentCardResponseDto.builder().id(1L).build()));

        when(paymentCardRepo.findAll(any(Specification.class), eq(pageable))).thenReturn(cardPage);
        when(mapper.toDto(cardPage)).thenReturn(dtoPage);

        Page<PaymentCardResponseDto> result = service.findAll(active, expiresAfter, expiresBefore, pageable);

        assertEquals(dtoPage, result);
        verify(paymentCardRepo).findAll(any(Specification.class), eq(pageable));
        verify(mapper).toDto(cardPage);
    }

    @Test
    void findAllByUserId_shouldReturnList_whenCardsExist() {
        long userId = 1L;
        PaymentCard card = PaymentCard.builder().id(1L).build();
        List<PaymentCard> cards = List.of(card);
        List<PaymentCardResponseDto> dtos = List.of(PaymentCardResponseDto.builder().id(1L).build());

        when(paymentCardRepo.findAllByUserId(userId)).thenReturn(cards);
        when(mapper.toDto(cards)).thenReturn(dtos);

        List<PaymentCardResponseDto> result = service.findAllByUserId(userId);

        assertEquals(dtos, result);
        verify(paymentCardRepo).findAllByUserId(userId);
        verify(mapper).toDto(cards);
    }
}
