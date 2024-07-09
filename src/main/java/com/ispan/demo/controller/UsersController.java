package com.ispan.demo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ispan.demo.model.Group;
import com.ispan.demo.model.Users;
import com.ispan.demo.service.GroupService;
import com.ispan.demo.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UsersController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private GroupService groupService;

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
    
    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        UUID loginUserId = (UUID) session.getAttribute("loginUserId");
        if (loginUserId != null) {
            Users loginUser = userService.findUsersById(loginUserId);
            if (loginUser != null) {
                model.addAttribute("session", session);
                model.addAttribute("loginNickname", loginUser.getNickname());
                List<Group> userGroups = groupService.findAllGroups(); // 您可能需要添加篩選條件來查找特定用戶的群組
                model.addAttribute("userGroups", userGroups);
                model.addAttribute("users", userService.findAllUsers());
            }
        }
        List<Group> groups = groupService.findAllGroups();
        model.addAttribute("groups", groups);
        model.addAttribute("group", new Group()); // 确保添加 group 对象
        return "index";
    }
}
