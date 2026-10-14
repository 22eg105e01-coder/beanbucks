package com.beanbucks.beanbucks;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    List<Voucher> findByCustomerId(Long customerId);
}
