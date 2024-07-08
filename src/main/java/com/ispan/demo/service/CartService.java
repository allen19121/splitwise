package com.ispan.demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ispan.demo.model.Cart;
import com.ispan.demo.model.CartId;
import com.ispan.demo.model.CartRepository;
import com.ispan.demo.model.Photos;
import com.ispan.demo.model.Users;

@Service
public class CartService {
	
	@Autowired
	private CartRepository cartRepo;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PhotoService photoService;
	
	@Transactional
	public Cart addToCart(UUID userId, Integer photoId) {
		
		Cart exist = cartRepo.findByUsersIdAndPhotoId(userId, photoId);
		
		if(exist != null) {
			exist.setVol(exist.getVol() + 1);
			return exist;
		}
		
		Users user = userService.findUsersById(userId);
		
		Photos photos = photoService.findPhotosById(photoId);
		
		// id 物件
		CartId cartId = new CartId();
		cartId.setUsersId(userId);
		cartId.setPhotosId(photoId);
		
		// Cart 物件
		Cart cart = new Cart();
		cart.setCartId(cartId);
		cart.setUsers(user);
		cart.setPhotos(photos);
		cart.setVol(1);
		
		return cartRepo.save(cart);
		
	}
	
	public List<Cart> findUsersCartService(UUID userId){

		return cartRepo.findUserCartPhotos(userId);
	}
	
	@Transactional
	public Cart addOneVolumn(UUID userId, Integer photoId){

		Cart cart = cartRepo.findByUsersIdAndPhotoId(userId, photoId);
		cart.setVol(cart.getVol() + 1);

		return cart;
	}
	
	@Transactional
	public void minusOneVolumn(UUID userId, Integer photoId){

		Cart cart = cartRepo.findByUsersIdAndPhotoId(userId, photoId);

		// 若數量本來就是 1 ，則直接刪除 user 和 商品間的紀錄資料，若不是則數量 -1 
		if(cart.getVol() == 1){
			cartRepo.delete(cart);
		}else{
			cart.setVol(cart.getVol() - 1);
		}
	}

}
