package com.ispan.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.demo.model.Bill;
import com.ispan.demo.model.BillRepository;
import com.ispan.demo.model.Payment;
import com.ispan.demo.model.PaymentRepository;
import com.ispan.demo.model.Users;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepo;

    @Autowired
    private PaymentRepository paymentRepo;

    public Bill insertOrUpdateBill(Bill bill) {
        // 確保 amount 不為空
        if (bill.getAmount() == null) {
            bill.setAmount(0.0);
        }
        Bill savedBill = billRepo.save(bill);
        System.out.println("Saved bill: " + savedBill);
        return savedBill;
    }

    public Bill findBillById(Integer id) {
        Optional<Bill> optional = billRepo.findById(id);
        return optional.orElse(null);
    }

    public void deleteBill(Integer id) {
        billRepo.deleteById(id);
    }

    public List<Bill> findAllBills() {
        List<Bill> bills = billRepo.findAll();
        System.out.println("Retrieved all bills: " + bills);
        return bills;
    }

    public void savePayment(Payment payment) {
        paymentRepo.save(payment);
    }

    public List<Payment> findAllPayments() {
        return paymentRepo.findAll();
    }

    public Map<Users, Double> calculateBalances() {
        List<Bill> bills = findAllBills();
        Map<Users, Double> balances = new HashMap<>();

        for (Bill bill : bills) {
            Users payer = bill.getPayer();
            Double billAmount = bill.getAmount() != null ? bill.getAmount() : 0.0;
            balances.put(payer, balances.getOrDefault(payer, 0.0) - billAmount);

            for (Payment payment : bill.getPayments()) {
                Users payee = payment.getPayee();
                Double paymentAmount = payment.getAmount();
                if (paymentAmount != null) {
                    balances.put(payee, balances.getOrDefault(payee, 0.0) + paymentAmount);
                }
            }
        }

        return balances;
    }

    public Map<Users, Double> calculateBalancesForBill(Bill bill) {
        Map<Users, Double> balances = new HashMap<>();
        Users payer = bill.getPayer();
        Double billAmount = bill.getAmount() != null ? bill.getAmount() : 0.0;
        balances.put(payer, balances.getOrDefault(payer, 0.0) - billAmount);

        for (Payment payment : bill.getPayments()) {
            Users payee = payment.getPayee();
            Double paymentAmount = payment.getAmount();
            if (paymentAmount != null) {
                balances.put(payee, balances.getOrDefault(payee, 0.0) + paymentAmount);
            }
        }
        return balances;
    }

    public List<Payment> findPendingPayments() {
        return paymentRepo.findByConfirmedFalse();
    }

    public List<Payment> findPendingPaymentsForBill(Bill bill) {
        return paymentRepo.findByBillIdAndConfirmedFalse(bill.getId());
    }

    public Payment findPaymentById(Integer paymentId) {
        Optional<Payment> payment = paymentRepo.findById(paymentId);
        return payment.orElse(null);
    }
}
