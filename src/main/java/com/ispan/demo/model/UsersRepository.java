package com.ispan.demo.model;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, UUID> {

	Users findByUsername(String username);
}
