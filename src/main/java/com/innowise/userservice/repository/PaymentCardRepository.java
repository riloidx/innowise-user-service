package com.innowise.userservice.repository;

import com.innowise.userservice.entity.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long>,
        JpaSpecificationExecutor<PaymentCard> {

    List<PaymentCard> findAllByUserId(long id);

    Optional<PaymentCard> findByNumber(String number);
}
