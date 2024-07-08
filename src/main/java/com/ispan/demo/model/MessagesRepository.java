package com.ispan.demo.model;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessagesRepository extends JpaRepository<Messages, Integer> {
	
	@Query(value="select top(1) * from messages order by added desc", nativeQuery = true)
	Messages findLatest();
	
	@Query(value="from Messages")
    List<Messages> findLatest2(Pageable pgb);
	
	Messages findFirstByOrderByAddedDesc();
}
