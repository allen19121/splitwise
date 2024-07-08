package com.ispan.demo.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="cart")
public class Cart {
	
	@EmbeddedId
	private CartId cartId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("usersId")
	private Users users;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("photosId")
	private Photos photos;
	
	private Integer vol;

}
