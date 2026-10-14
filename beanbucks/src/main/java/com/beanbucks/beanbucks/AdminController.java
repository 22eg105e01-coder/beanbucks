package com.beanbucks.beanbucks;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CustomerRepository customers;
    private final TransactionRepository transactions;
    private final VoucherRepository vouchers;

    @GetMapping("/summary")
    public Map<String, Object> summary() {
        List<Transaction> all = transactions.findAll();
        BigDecimal topups = sum(all, "TOPUP");
        BigDecimal sales = sum(all, "PURCHASE");
        List<Voucher> v = vouchers.findAll();
        return Map.of(
            "customers", customers.count(),
            "totalTopUps", topups,
            "totalSales", sales,
            "vouchersIssued", v.size(),
            "vouchersRedeemed", v.stream().filter(Voucher::isRedeemed).count()
        );
    }

    @GetMapping("/customers")
    public List<Customer> customers() { return customers.findAll(); }

    @GetMapping("/transactions")
    public List<Transaction> transactions() {
        List<Transaction> all = transactions.findAll();
        all.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return all;
    }

    @GetMapping("/vouchers")
    public List<Voucher> vouchers() { return vouchers.findAll(); }

    private BigDecimal sum(List<Transaction> list, String type) {
        return list.stream()
            .filter(t -> type.equals(t.getType()))
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}