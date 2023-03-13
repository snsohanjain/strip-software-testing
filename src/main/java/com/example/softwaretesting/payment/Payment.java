package com.example.softwaretesting.payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import java.util.UUID;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    private Long paymentId;

    private UUID customerId;

    private BigDecimal amount;

    private Currency currency;

    private String source;

    private String description;
}

