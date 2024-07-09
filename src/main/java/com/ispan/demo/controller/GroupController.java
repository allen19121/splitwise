package com.ispan.demo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ispan.demo.model.Group;
import com.ispan.demo.model.Users;
import com.ispan.demo.service.GroupService;
import com.ispan.demo.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @GetMapping("/group/add")
    public String addGroup(HttpSession session, Model model) {
        UUID loginUserId = (UUID) session.getAttribute("loginUserId");
        if (loginUserId == null) {
            return "redirect:/error/unauthorized";
        }
        model.addAttribute("group", new Group());
        model.addAttribute("users", userService.findAllUsers());
        return "group/addGroupPage";
    }

    @PostMapping("/group/addPost")
    public String addPostGroup(@ModelAttribute Group group, @RequestParam List<UUID> userIds, Model model) {
        List<Users> members = userService.findUsersByIds(userIds);
        group.setMembers(members);
        groupService.createOrUpdateGroup(group);
        return "redirect:/group/list";
    }

    @GetMapping("/group/list")
    public String listGroups(Model model) {
        model.addAttribute("groups", groupService.findAllGroups());
        return "group/listPage";
    }

    @GetMapping("/group/view")
    public String viewGroup(@RequestParam("groupId") Integer groupId, Model model) {
        Group group = groupService.findGroupById(groupId);
        if (group != null) {
            model.addAttribute("group", group);
            return "group/viewGroupPage";
        } else {
            return "redirect:/group/list";
        }
    }

    @GetMapping("/group/edit")
    public String editGroup(@RequestParam("groupId") Integer groupId, Model model) {
        Group group = groupService.findGroupById(groupId);
        if (group != null) {
            model.addAttribute("group", group);
            model.addAttribute("users", userService.findAllUsers());
            return "group/editGroupPage";
        } else {
            return "redirect:/group/list";
        }
    }

    @PostMapping("/group/update")
    public String updateGroup(@ModelAttribute Group group, @RequestParam List<UUID> userIds, Model model) {
        Group existingGroup = groupService.findGroupById(group.getId());
        if (existingGroup != null) {
            existingGroup.setName(group.getName());
            List<Users> members = userService.findUsersByIds(userIds);
            existingGroup.setMembers(members);
            groupService.createOrUpdateGroup(existingGroup);
        }
        return "redirect:/group/list";
    }

    @PostMapping("/group/delete")
    public String deleteGroup(@RequestParam("groupId") Integer groupId) {
        groupService.deleteGroup(groupId);
        return "redirect:/group/list";
    }
}
