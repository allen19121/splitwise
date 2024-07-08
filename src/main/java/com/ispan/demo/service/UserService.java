package com.ispan.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ispan.demo.model.Bill;

import com.ispan.demo.model.Users;
import com.ispan.demo.model.UsersRepository;

@Service
public class UserService {

    @Autowired
    private UsersRepository usersRepo;
    


    @Autowired
    private PasswordEncoder pwdEncoder;
    
    @Autowired
    private BillService billService;



    public boolean checkIfUserNameExist(String username) {
        Users result = usersRepo.findByUsername(username);
        return result != null;
    }

    public Users addUsers(String username, String password, String nickname) {
        String encodePassword = pwdEncoder.encode(password);

        Users newUser = new Users();
        newUser.setUsername(username);
        newUser.setPassword(encodePassword);
        newUser.setNickname(nickname);

        return usersRepo.save(newUser);
    }

    public Users checkLogin(String username, String password) {
        Users dbUser = usersRepo.findByUsername(username);
        if (dbUser == null) {
            return null;
        }
        boolean result = pwdEncoder.matches(password, dbUser.getPassword());
        if (result) {
            return dbUser;
        }
        return null;
    }

    public Users findUsersById(UUID id) {
        Optional<Users> optional = usersRepo.findById(id);
        return optional.orElse(null);
    }

    public List<Users> findAllUsers() {
        return usersRepo.findAll();
    }

    public List<Users> findUsersByIds(List<UUID> ids) {
        return usersRepo.findAllById(ids);
    }

    public List<Users> findAllPayers() {
        List<Bill> bills = billService.findAllBills();
        List<Users> payers = new ArrayList<>();
        for (Bill bill : bills) {
            if (!payers.contains(bill.getPayer())) {
                payers.add(bill.getPayer());
            }
        }
        return payers;
    }
}
