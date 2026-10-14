package com.beanbucks.beanbucks;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private static final BigDecimal SPEND_PER_STAR = new BigDecimal("10");
    private static final int STARS_PER_VOUCHER = 20;

    private final CustomerRepository customers;
    private final TransactionRepository transactions;
    private final VoucherRepository vouchers;

    public Customer createCustomer(String name) {
        Customer c = new Customer();
        c.setName(name);
        return customers.save(c);
    }

    @Transactional
    public Customer topUp(Long customerId, BigDecimal amount) {
        checkPositive(amount);
        Customer c = customers.findById(customerId).orElseThrow();

        c.setBalance(c.getBalance().add(amount));
        record(c, "TOPUP", amount);
        return customers.save(c);
    }

    @Transactional
    public Customer purchase(Long customerId, BigDecimal amount) {
        checkPositive(amount);
        Customer c = customers.findById(customerId).orElseThrow();

        if (c.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Not enough balance");
        }

        // 1. take the money
        c.setBalance(c.getBalance().subtract(amount));
        record(c, "PURCHASE", amount);

        // 2. loyalty: add stars
        int earned = amount.divideToIntegralValue(SPEND_PER_STAR).intValue();
        c.setStars(c.getStars() + earned);

        // 3. milestone: every 20 stars = 1 voucher
        while (c.getStars() >= STARS_PER_VOUCHER) {
            c.setStars(c.getStars() - STARS_PER_VOUCHER);
            Voucher v = new Voucher();
            v.setCustomer(c);
            v.setCode("BEAN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            vouchers.save(v);
        }

        return customers.save(c);
    }

    private void record(Customer c, String type, BigDecimal amount) {
        Transaction t = new Transaction();
        t.setCustomer(c);
        t.setType(type);
        t.setAmount(amount);
        transactions.save(t);
    }

    private void checkPositive(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}