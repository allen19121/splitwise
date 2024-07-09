package com.ispan.demo.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ispan.demo.model.Bill;
import com.ispan.demo.model.Payment;
import com.ispan.demo.model.Photos;
import com.ispan.demo.model.Users;
import com.ispan.demo.service.BillService;
import com.ispan.demo.service.PhotoService;
import com.ispan.demo.service.UserService;

@Controller
public class BillController {

    @Autowired
    private BillService billService;

    @Autowired
    private UserService userService;

    @Autowired
    private PhotoService photoService;

    @GetMapping("/bill/add")
    public String add(Model model) {
        model.addAttribute("bill", new Bill());
        model.addAttribute("users", userService.findAllUsers());
        return "bill/addBillPage";
    }

    @GetMapping("/bill/list")
    public String showAllBills(Model model) {
        List<Bill> billList = billService.findAllBills();
        billList.forEach(bill -> {
            bill.setPayments(bill.getPayments().stream().peek(payment -> {
                if (payment.getAmount() != null) {
                    payment.setAmount(Math.ceil(payment.getAmount()));
                }
            }).collect(Collectors.toList()));
        });
        model.addAttribute("allBills", billList);
        return "bill/listPage";
    }

    @GetMapping("/bill/update")
    public String update(@RequestParam("id") Integer id, Model model) {
        Bill bill = billService.findBillById(id);
        model.addAttribute("bill", bill);
        model.addAttribute("users", userService.findAllUsers());
        return "bill/updatePage";
    }

    @GetMapping("/bill/settle")
    public String settle(Model model) {
        List<Users> users = userService.findAllUsers();
        List<Payment> pendingPayments = billService.findPendingPayments();
        List<Bill> allBills = billService.findAllBills();
        model.addAttribute("users", users);
        model.addAttribute("pendingPayments", pendingPayments);
        model.addAttribute("allBills", allBills);
        return "settle/settlePage"; // 返回正確的視圖名稱
    }

    @PostMapping("/bill/addPost")
    public String addPostBill(@ModelAttribute Bill bill, 
                              @RequestParam(required = false) List<UUID> participantIds, 
                              @RequestParam(required = false) List<Double> amounts, 
                              @RequestParam UUID payerId,
                              @RequestParam boolean isManual,
                              @RequestParam("image") MultipartFile imageFile, 
                              Model model) {

        if (participantIds == null || participantIds.isEmpty()) {
            model.addAttribute("errorMsg", "請選擇至少一個參與者");
            model.addAttribute("users", userService.findAllUsers());
            return "bill/addBillPage";
        }

        Users payer = userService.findUsersById(payerId);
        bill.setPayer(payer);
        List<Users> participants = userService.findUsersByIds(participantIds);
        bill.setParticipants(participants);

        if (!imageFile.isEmpty()) {
            Photos photo = new Photos();
            photo.setPhotoName(imageFile.getOriginalFilename());
            try {
                photo.setPhotoFile(imageFile.getBytes());
                photoService.insertPhoto(photo);
                bill.setPhoto(photo);
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("errorMsg", "圖片上傳失敗");
                return "bill/addBillPage";
            }
        }

        if (isManual) {
            double totalAmount = 0.0;
            for (int i = 0; i < participants.size(); i++) {
                Payment payment = new Payment();
                payment.setBill(bill);
                payment.setPayer(payer);
                payment.setPayee(participants.get(i));
                Double amount = amounts != null && i < amounts.size() ? amounts.get(i) : 0.0;
                payment.setAmount(amount);
                payment.setConfirmed(false);
                totalAmount += amount;
                bill.getPayments().add(payment);
            }
            bill.setAmount(totalAmount);
        } else {
            Double perPersonAmount = bill.getAmount() / participants.size();
            for (Users participant : participants) {
                Payment payment = new Payment();
                payment.setBill(bill);
                payment.setPayer(payer);
                payment.setPayee(participant);
                payment.setAmount(perPersonAmount);
                payment.setConfirmed(false);
                bill.getPayments().add(payment);
            }
        }

        billService.insertOrUpdateBill(bill);
        return "redirect:/bill/list";
    }

    @PostMapping("/bill/updateSend")
    public String updateSend(@ModelAttribute Bill bill, @RequestParam(required = false) List<UUID> participantIds, 
                             @RequestParam(required = false) List<Double> amounts, 
                             @RequestParam("image") MultipartFile imageFile, 
                             Model model) {
        Bill existingBill = billService.findBillById(bill.getId());
        if (existingBill == null) {
            return "redirect:/bill/list";
        }

        existingBill.setName(bill.getName());
        existingBill.setDescription(bill.getDescription());

        if (!imageFile.isEmpty()) {
            Photos photo = new Photos();
            photo.setPhotoName(imageFile.getOriginalFilename());
            try {
                photo.setPhotoFile(imageFile.getBytes());
                photoService.insertPhoto(photo);
                existingBill.setPhoto(photo);
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("errorMsg", "圖片上傳失敗");
                return "bill/updatePage";
            }
        }

        if (participantIds != null && !participantIds.isEmpty()) {
            List<Users> participants = userService.findUsersByIds(participantIds);
            existingBill.setParticipants(participants);
            existingBill.getPayments().clear();
            
            Double totalAmount = 0.0;
            if (amounts != null && amounts.size() == participants.size()) {
                for (int i = 0; i < participants.size(); i++) {
                    Payment payment = new Payment();
                    payment.setBill(existingBill);
                    payment.setPayer(existingBill.getPayer());
                    payment.setPayee(participants.get(i));
                    Double amount = amounts.get(i);
                    payment.setAmount(amount);
                    payment.setConfirmed(false);
                    existingBill.getPayments().add(payment);
                    totalAmount += amount;
                }
                existingBill.setAmount(totalAmount);
            } else {
                Double perPersonAmount = bill.getAmount() / participants.size();
                for (Users participant : participants) {
                    Payment payment = new Payment();
                    payment.setBill(existingBill);
                    payment.setPayer(existingBill.getPayer());
                    payment.setPayee(participant);
                    payment.setAmount(perPersonAmount);
                    payment.setConfirmed(false);
                    existingBill.getPayments().add(payment);
                }
                existingBill.setAmount(bill.getAmount());
            }
        }

        billService.insertOrUpdateBill(existingBill);
        return "redirect:/bill/list";
    }

    @PostMapping("/bill/repay")
    public String repay(@RequestParam UUID payerId, 
                        @RequestParam UUID payeeId, 
                        @RequestParam Double amount, 
                        Model model) {
        Users payer = userService.findUsersById(payerId);
        Users payee = userService.findUsersById(payeeId);

        if (payer == null || payee == null) {
            model.addAttribute("errorMsg", "無效的用戶ID");
            return "redirect:/bill/settle";
        }

        if (amount == null || amount <= 0) {
            model.addAttribute("errorMsg", "金額無效");
            return "redirect:/bill/settle";
        }

        Payment payment = new Payment();
        payment.setPayer(payer);
        payment.setPayee(payee);
        payment.setAmount(amount);
        payment.setConfirmed(false);
        billService.savePayment(payment);

        return "redirect:/bill/settle";
    }
    
    @PostMapping("/bill/confirmPayment")
    public ResponseEntity<String> confirmPayment(@RequestParam Integer paymentId) {
        Payment payment = billService.findPaymentById(paymentId);
        if (payment != null) {
            payment.setConfirmed(true);
            billService.savePayment(payment);
            return ResponseEntity.ok("確認成功");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("確認失敗");
        }
    }

    @DeleteMapping("/bill/delete")
    public String deleteBill(@RequestParam("id") Integer id) {
        billService.deleteBill(id);
        return "redirect:/bill/list";
    }
}
