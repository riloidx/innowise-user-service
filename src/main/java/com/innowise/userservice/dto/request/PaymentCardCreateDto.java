package com.innowise.userservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentCardCreateDto {

    @NotBlank(message = "Card number is required")
    @Size(min = 12, max = 64, message = "Card number length must be between 12 and 64 characters")
    private String number;

    @NotBlank(message = "Card holder name is required")
    @Size(max = 128, message = "Holder name must not exceed 128 characters")
    private String holder;

    @NotNull(message = "Expiration date is required")
    @Future(message = "Expiration date must be in the future")
    private LocalDate expirationDate;

    @NotNull(message = "User ID is required")
    private Long userId;
}
