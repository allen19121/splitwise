package com.ispan.demo.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class CartId implements Serializable {
	
//	https://docs.jboss.org/hibernate/orm/6.5/userguide/html_single/Hibernate_User_Guide.html#identifiers-composite
	
	private UUID usersId;
	
	private Integer photosId;

	public CartId() {
	}

	@Override
	public int hashCode() {
		return Objects.hash(photosId, usersId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CartId other = (CartId) obj;
		return Objects.equals(photosId, other.photosId) && Objects.equals(usersId, other.usersId);
	}
	
	
	
	
	

}
