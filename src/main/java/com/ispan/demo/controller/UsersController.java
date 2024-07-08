package com.ispan.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ispan.demo.model.Users;
import com.ispan.demo.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UsersController {

    @Autowired
    private UserService userService;

    @GetMapping("/users/register")
    public String register() {
        return "/users/registerPage";
    }

    @GetMapping("/users/login")
    public String login() {
        return "/users/loginPage";
    }

    @PostMapping("/users/registerPost")
    public String postUser(@RequestParam String username, @RequestParam String password, @RequestParam String nickname, Model model) {

        boolean result = userService.checkIfUserNameExist(username);

        if (!result) {
            userService.addUsers(username, password, nickname);
            model.addAttribute("success", "註冊成功");
        } else {
            model.addAttribute("error", "此使用者名稱已被使用");
        }

        return "/users/registerPage";
    }

    @PostMapping("/users/loginPost")
    public String postUserLogin(
            @RequestParam String username, 
            @RequestParam String password, 
            HttpSession httpSession,
            Model model) {

        Users result = userService.checkLogin(username, password);

        if (result != null) {
            httpSession.setAttribute("loginUserId", result.getId());
            httpSession.setAttribute("loginUsername", result.getUsername());
            httpSession.setAttribute("loginNickname", result.getNickname());
        } else {
            model.addAttribute("error", "登入失敗");
            return "/users/loginPage";
        }

        return "redirect:/";
    }

    @GetMapping("/users/logout")
    public String logout(HttpSession httpSession) {

        httpSession.invalidate();

        return "redirect:/";
    }
}
