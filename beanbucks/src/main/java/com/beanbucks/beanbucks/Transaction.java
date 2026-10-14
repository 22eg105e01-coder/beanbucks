package com.beanbucks.beanbucks;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor
public class Transaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Customer customer;
    private String type; // "TOPUP" or "PURCHASE"
    private BigDecimal amount;
    private LocalDateTime createdAt = LocalDateTime.now();
}