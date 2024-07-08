package com.ispan.demo.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByBillId(Integer billId);
    List<Payment> findByConfirmedFalse();
    List<Payment> findByBillIdAndConfirmedFalse(Integer billId);  // 添加这行
}
