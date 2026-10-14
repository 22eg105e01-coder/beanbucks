package com.beanbucks.beanbucks;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CoffeeController {

    private final LedgerService service;
    private final CustomerRepository customers;

    private final VoucherRepository vouchers;
    record NewCustomer(String name) {}
    record AmountRequest(BigDecimal amount) {}

    @PostMapping
    public Customer create(@RequestBody NewCustomer body) {
        return service.createCustomer(body.name());
    }

    @PostMapping("/{id}/topup")
    public Customer topUp(@PathVariable Long id, @RequestBody AmountRequest body) {
        return service.topUp(id, body.amount());
    }

    @PostMapping("/{id}/purchase")
    public Customer purchase(@PathVariable Long id, @RequestBody AmountRequest body) {
        return service.purchase(id, body.amount());
    }

    @GetMapping("/{id}")
    public Customer get(@PathVariable Long id) {
        return customers.findById(id).orElseThrow();
    }
        @GetMapping("/{id}/vouchers")
    public List<Voucher> vouchers(@PathVariable Long id) {
        return vouchers.findByCustomerId(id);
    }
}