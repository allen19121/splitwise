package com.ispan.demo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ispan.demo.model.Cart;
import com.ispan.demo.model.Photos;
import com.ispan.demo.service.CartService;
import com.ispan.demo.service.PhotoService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CartController {

	@Autowired
	private CartService cartService;

	@Autowired
	private PhotoService photoService;

	@GetMapping("/cart/add")
	public String addToCart(@RequestParam Integer photoId, HttpSession httpSession, Model model) {

		UUID loginUserId = (UUID) httpSession.getAttribute("loginUserId");

		if (loginUserId == null) {
			return "users/loginPage";
		}

		cartService.addToCart(loginUserId, photoId);

		model.addAttribute("addToCartOK", "新增到購物車!!");

		List<Photos> photoList = photoService.findAllPhotos();

		model.addAttribute("photoList", photoList);

		return "photos/showPhotosPage";
	}

	@GetMapping("/cart/list")
	public String listCart(Model model, HttpSession session) {

		UUID loginUserId = (UUID) session.getAttribute("loginUserId");

		if (loginUserId == null) {
			return "users/loginPage";
		}

		List<Cart> cartList = cartService.findUsersCartService(loginUserId);

		model.addAttribute("cartList", cartList);

		return "cart/listCart";
	}

	@GetMapping("/cart/addOne")
	public String addOneVol(Integer photoId, HttpSession session, Model model) {

		UUID loginUserId = (UUID) session.getAttribute("loginUserId");

		if (loginUserId == null) {
			return "users/loginPage";
		}

		cartService.addOneVolumn(loginUserId, photoId);

		List<Cart> cartList = cartService.findUsersCartService(loginUserId);

		model.addAttribute("cartList", cartList);

		return "cart/listCart";
	}

	@GetMapping("/cart/minusOne")
	public String minusOneVol(Integer photoId, HttpSession session, Model model) {

		UUID loginUserId = (UUID) session.getAttribute("loginUserId");

		if (loginUserId == null) {
			return "users/loginPage";
		}

		cartService.minusOneVolumn(loginUserId, photoId);

		List<Cart> cartList = cartService.findUsersCartService(loginUserId);

		model.addAttribute("cartList", cartList);

		return "cart/listCart";
	}

}
