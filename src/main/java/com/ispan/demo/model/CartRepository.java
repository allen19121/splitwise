package com.ispan.demo.model;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartRepository extends JpaRepository<Cart, CartId> {
	
	@Query(value="select c from Cart c where c.cartId.usersId = :userId and c.cartId.photosId = :photoId")
	Cart findByUsersIdAndPhotoId(@Param("userId") UUID userId, @Param("photoId") Integer photoId);
	
	@Query("select c from Cart c where c.cartId.usersId = :userId ")
	public List<Cart> findUserCartPhotos(@Param("userId") UUID userId);
	

}
