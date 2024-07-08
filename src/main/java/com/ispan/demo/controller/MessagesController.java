package com.ispan.demo.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ispan.demo.model.Messages;
import com.ispan.demo.model.Photos;
import com.ispan.demo.model.Users;
import com.ispan.demo.service.MessageService;
import com.ispan.demo.service.PhotoService;
import com.ispan.demo.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MessagesController {

	@Autowired
	private UserService userService;

	@Autowired
	private MessageService msgService;

	@Autowired
	private PhotoService photoService;

	@GetMapping("/messages/add")
	public String addMsg(Model model) {
		Messages latestMsg = msgService.latestMsg2();
		model.addAttribute("latestMsg", latestMsg);
		return "messages/addMsgPage";
	}

	@PostMapping("/messages/addPost")
	public String postMethodName(@RequestParam String text,
	                             @RequestParam("photo") MultipartFile photoFile,
	                             Model model,
	                             HttpSession httpSession) throws IOException {
		UUID loginUserId = (UUID) httpSession.getAttribute("loginUserId");

		Messages msg = new Messages();
		msg.setText(text);

		if (loginUserId != null) {
			Users users = userService.findUsersById(loginUserId);
			msg.setUsers(users);
		}

		if (!photoFile.isEmpty()) {
			Photos photo = new Photos();
			photo.setPhotoName(photoFile.getOriginalFilename());
			photo.setPhotoFile(photoFile.getBytes());
			photoService.insertPhoto(photo);
			msg.setPhoto(photo);  // 設置 Messages 與 Photos 的關聯
		}

		msgService.insertOrUpdateMsg(msg);

		Messages latestMsg = msgService.latestMsg2();
		model.addAttribute("latestMsg", latestMsg);

		return "messages/addMsgPage";
	}

	@GetMapping("/messages/page")
	public String findByPage(@RequestParam(value = "p", defaultValue = "1") Integer pageNumber, Model model) {
		Page<Messages> page = msgService.findByPage(pageNumber);
		model.addAttribute("page", page);
		return "messages/listPage";
	}

	@GetMapping("/messages/update")
	public String updateById(@RequestParam Integer id, Model model, HttpSession httpSession) {
		Messages msg = msgService.findMsgById(id);

		if (msg.getUsers() != null) {
			UUID ownerID = msg.getUsers().getId();
			UUID loginUserId = (UUID) httpSession.getAttribute("loginUserId");

			if (ownerID.equals(loginUserId)) {
				model.addAttribute("messages", msg);

				return "messages/updateMsgPage";
			}

		}
		return "redirect:/users/login";
	}

	@PutMapping("/messages/updatePut")
	public String updatePut(@ModelAttribute Messages messages, HttpSession httpSession) {
		
		UUID loginUserId = (UUID) httpSession.getAttribute("loginUserId");
		
		Users users = userService.findUsersById(loginUserId);
		
		messages.setUsers(users);
		
		msgService.insertOrUpdateMsg(messages);

		return "redirect:/messages/page";
	}

	@DeleteMapping("/messages/delete")
	public String deleteMsg(@RequestParam Integer id,HttpSession httpSession) {
		
		Messages msg = msgService.findMsgById(id);
		
		if (msg.getUsers() != null) {
			UUID ownerID = msg.getUsers().getId();
			UUID loginUserId = (UUID) httpSession.getAttribute("loginUserId");

			if (ownerID.equals(loginUserId)) {
				msgService.deleteMsg(id);

				return "redirect:/messages/page";
			}

		}
		return "redirect:/users/login";
		
	}

}
