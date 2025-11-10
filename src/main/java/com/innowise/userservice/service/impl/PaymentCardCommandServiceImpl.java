package com.innowise.userservice.service.impl;

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
import com.innowise.userservice.service.api.PaymentCardCommandService;
import com.innowise.userservice.service.api.PaymentCardQueryService;
import com.innowise.userservice.service.api.UserQueryService;
import com.innowise.userservice.util.ValidationUtil;
import lombok.RequiredArgsConstructor;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentCardCommandServiceImpl implements PaymentCardCommandService {

    private final PaymentCardRepository paymentCardRepo;
    private final PaymentCardQueryService paymentCardQueryService;
    private final PaymentCardMapper mapper;
    private final ValidationUtil validationUtil;
    private final UserQueryService userQueryService;

    @Value("${user.card.max-limit}")
    private int maxCardLimit;

    @Override
    @Transactional
    public PaymentCardResponseDto create(PaymentCardCreateDto paymentCardCreateDto) {
        PaymentCard paymentCard = getValidatedCardForCreation(paymentCardCreateDto);

        PaymentCard savedCard = paymentCardRepo.save(paymentCard);

        return mapper.toDto(savedCard);
    }

    @Override
    @Transactional
    public PaymentCardResponseDto update(long id, PaymentCardUpdateDto paymentCardUpdateDto) {
        PaymentCard existingCard = getValidatedCardForUpdate(id, paymentCardUpdateDto);

        mapper.updateEntityFromDto(paymentCardUpdateDto, existingCard);
        PaymentCard updatedCard = paymentCardRepo.save(existingCard);

        return mapper.toDto(updatedCard);
    }

    @Override
    @Transactional
    public void delete(long id) {
        paymentCardQueryService.findById(id);

        paymentCardRepo.deleteById(id);
    }

    @Override
    @Transactional
    public PaymentCardResponseDto changeStatus(long id, boolean active) {
        PaymentCard card = getValidatedCardForChangingStatus(id, active);

        card = paymentCardRepo.save(card);
        return mapper.toDto(card);
    }

    private void checkCardNumberNotTaken(String cardNumber) {
        paymentCardRepo.findByNumber(cardNumber)
                .ifPresent(card -> {
                    throw new PaymentCardAlreadyExistsException("number", cardNumber);
                });
    }

    private PaymentCard getValidatedCardForCreation(PaymentCardCreateDto paymentCardCreateDto) {
        checkCardNumberNotTaken(paymentCardCreateDto.getNumber());

        User user = userQueryService.findById(paymentCardCreateDto.getUserId());

        if (user.getPaymentCards().size() >= maxCardLimit) {
            throw new PaymentCardLimitExceededException("Maximum number of cards (" + maxCardLimit + ") exceeded");
        }

        return prepareCard(paymentCardCreateDto, user);
    }

    private PaymentCard prepareCard(PaymentCardCreateDto paymentCardCreateDto, User user) {
        PaymentCard card = mapper.toEntity(paymentCardCreateDto);
        card.setUser(user);
        card.setHolder(user.getName() + " " + user.getSurname());

        return card;
    }

    private PaymentCard getValidatedCardForUpdate(long id, PaymentCardUpdateDto dto) {
        validationUtil.validateMatchingIds(id, dto.getId());

        PaymentCard existingCard = paymentCardQueryService.findById(id);

        if (!existingCard.getNumber().equals(dto.getNumber())) {
            checkCardNumberNotTaken(dto.getNumber());
        }

        return existingCard;
    }

    private PaymentCard getValidatedCardForChangingStatus(long id, boolean active) {
        PaymentCard card = paymentCardQueryService.findById(id);

        if (active == card.getActive()) {
            throw new BadRequestException("Card with id=" + id + " have status=" + (active ? "active" : "inactive"));
        }

        card.setActive(active);

        return card;
    }
}