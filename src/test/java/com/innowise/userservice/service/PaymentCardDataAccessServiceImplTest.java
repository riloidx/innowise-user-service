package com.innowise.userservice.service;

import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.exception.PaymentCardNotFoundException;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.service.impl.PaymentCardDataAccessServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentCardDataAccessServiceImplTest {

    @Mock
    private PaymentCardRepository paymentCardRepo;

    @InjectMocks
    private PaymentCardDataAccessServiceImpl service;

    @Test
    void findById_returnCard_whenCardExists() {
        long id = 1L;

        PaymentCard paymentCard = PaymentCard.builder().
                id(id).
                number("12341234").
                build();

        when(paymentCardRepo.findById(id)).thenReturn(Optional.of(paymentCard));

        PaymentCard result = service.findById(id);

        assertEquals(paymentCard, result);
        verify(paymentCardRepo).findById(id);
    }

    @Test
    void findById_ThrowException_whenCardDoesNotExist() {
        long id = 1L;
        when(paymentCardRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(PaymentCardNotFoundException.class, () -> service.findById(id));
        verify(paymentCardRepo).findById(id);
    }
}