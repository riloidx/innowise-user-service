package com.innowise.userservice.service;

import com.innowise.userservice.config.CardProperties;
import com.innowise.userservice.dto.request.PaymentCardCreateDto;
import com.innowise.userservice.dto.request.PaymentCardUpdateDto;
import com.innowise.userservice.dto.response.PaymentCardResponseDto;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.BadRequestException;
import com.innowise.userservice.exception.PaymentCardAlreadyExistsException;
import com.innowise.userservice.exception.PaymentCardLimitExceededException;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.service.api.PaymentCardDataAccessService;
import com.innowise.userservice.service.api.UserDataAccessService;
import com.innowise.userservice.service.impl.PaymentCardCommandServiceImpl;
import com.innowise.userservice.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCardCommandServiceImplTest {

    @Mock
    private PaymentCardRepository paymentCardRepo;

    @Mock
    private PaymentCardDataAccessService paymentCardDataAccessService;

    @Mock
    private PaymentCardMapper mapper;

    @Mock
    private ValidationUtil validationUtil;

    @Mock
    private UserDataAccessService userDataAccessService;

    @Mock
    private CardProperties cardProperties;

    @InjectMocks
    private PaymentCardCommandServiceImpl service;

    @BeforeEach
    void setUp() {
        lenient().when(cardProperties.getMaxLimit()).thenReturn(5);
    }

    @Test
    void create_shouldSaveAndReturnDto_whenValid() {
        PaymentCardCreateDto createDto = new PaymentCardCreateDto();
        createDto.setUserId(1L);
        createDto.setNumber("12341234");

        User user = User.builder()
                .id(1L)
                .name("Test")
                .surname("Test")
                .paymentCards(new ArrayList<>())
                .build();

        PaymentCard cardEntity = PaymentCard.builder().id(1L).number("12341234").build();
        PaymentCardResponseDto cardDto = PaymentCardResponseDto.builder().id(1L).number("12341234").build();

        when(paymentCardRepo.findByNumber("12341234")).thenReturn(Optional.empty());
        when(userDataAccessService.findById(1L)).thenReturn(user);
        when(mapper.toEntity(createDto)).thenReturn(cardEntity);
        when(paymentCardRepo.save(cardEntity)).thenReturn(cardEntity);
        when(mapper.toDto(cardEntity)).thenReturn(cardDto);

        PaymentCardResponseDto result = service.create(createDto);

        assertEquals(cardDto, result);
        verify(paymentCardRepo).findByNumber("12341234");
        verify(userDataAccessService).findById(1L);
        verify(paymentCardRepo).save(cardEntity);
        verify(mapper).toDto(cardEntity);
    }

    @Test
    void create_shouldThrow_whenNumberAlreadyExists() {
        PaymentCardCreateDto createDto = new PaymentCardCreateDto();
        createDto.setNumber("12341234");

        PaymentCard existing = PaymentCard.builder().id(1L).number("12341234").build();
        when(paymentCardRepo.findByNumber("12341234")).thenReturn(Optional.of(existing));

        assertThrows(PaymentCardAlreadyExistsException.class, () -> service.create(createDto));
        verify(paymentCardRepo).findByNumber("12341234");
        verifyNoMoreInteractions(paymentCardRepo, userDataAccessService, mapper);
    }

    @Test
    void create_shouldThrow_whenUserExceedsCardLimit() {
        PaymentCardCreateDto createDto = new PaymentCardCreateDto();
        createDto.setUserId(1L);
        createDto.setNumber("12341234");

        List<PaymentCard> cards = new ArrayList<>();
        for (int i = 0; i < 5; i++) cards.add(PaymentCard.builder().id((long)i).build()); // уже 5 карт

        User user = User.builder().id(1L).paymentCards(cards).build();

        when(paymentCardRepo.findByNumber("12341234")).thenReturn(Optional.empty());
        when(userDataAccessService.findById(1L)).thenReturn(user);

        assertThrows(PaymentCardLimitExceededException.class, () -> service.create(createDto));
    }

    @Test
    void update_shouldSaveAndReturnDto_whenValid() {
        long id = 1L;
        PaymentCardUpdateDto updateDto = new PaymentCardUpdateDto();
        updateDto.setId(id);
        updateDto.setNumber("12341234");

        PaymentCard existingCard = PaymentCard.builder().id(id).number("12341234").build();
        PaymentCard updatedCard = PaymentCard.builder().id(id).number("12341234").build();
        PaymentCardResponseDto dto = PaymentCardResponseDto.builder().id(id).number("12341234").build();

        doNothing().when(validationUtil).validateMatchingIds(id, updateDto.getId());
        when(paymentCardDataAccessService.findById(id)).thenReturn(existingCard);
        doNothing().when(mapper).updateEntityFromDto(updateDto, existingCard);
        when(paymentCardRepo.save(existingCard)).thenReturn(updatedCard);
        when(mapper.toDto(updatedCard)).thenReturn(dto);

        PaymentCardResponseDto result = service.update(id, updateDto);

        assertEquals(dto, result);
        verify(paymentCardDataAccessService).findById(id);
        verify(paymentCardRepo).save(existingCard);
        verify(mapper).updateEntityFromDto(updateDto, existingCard);
        verify(mapper).toDto(updatedCard);
    }

    @Test
    void delete_shouldCallRepo() {
        long id = 1L;

        service.delete(id);

        verify(paymentCardRepo).deleteById(id);
    }

    @Test
    void changeStatus_shouldSaveAndReturnDto_whenStatusChanges() {
        long id = 1L;
        PaymentCard card = PaymentCard.builder().id(id).active(false).build();
        PaymentCard savedCard = PaymentCard.builder().id(id).active(true).build();
        PaymentCardResponseDto dto = PaymentCardResponseDto.builder().id(id).active(true).build();

        when(paymentCardDataAccessService.findById(id)).thenReturn(card);
        when(paymentCardRepo.save(card)).thenReturn(savedCard);
        when(mapper.toDto(savedCard)).thenReturn(dto);

        PaymentCardResponseDto result = service.changeStatus(id, true);

        assertEquals(dto, result);
        verify(paymentCardDataAccessService).findById(id);
        verify(paymentCardRepo).save(card);
        verify(mapper).toDto(savedCard);
    }

    @Test
    void changeStatus_shouldThrow_whenStatusAlreadySet() {
        long id = 1L;
        PaymentCard card = PaymentCard.builder().id(id).active(true).build();

        when(paymentCardDataAccessService.findById(id)).thenReturn(card);

        assertThrows(BadRequestException.class, () -> service.changeStatus(id, true));
        verify(paymentCardDataAccessService).findById(id);
        verifyNoMoreInteractions(paymentCardRepo, mapper);
    }
}
