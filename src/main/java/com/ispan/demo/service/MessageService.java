package com.ispan.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ispan.demo.model.Messages;
import com.ispan.demo.model.MessagesRepository;

@Service
public class MessageService {
	
	@Autowired
	private MessagesRepository msgRepo;
	
	public Messages insertOrUpdateMsg(Messages msg) {
		return msgRepo.save(msg);
	}
	
	public Messages findMsgById(Integer id) {
		Optional<Messages> optional = msgRepo.findById(id);
		
		if(optional.isEmpty()) {
			return null;
		}
		
		return optional.get();
	}
	
	public void deleteMsg(Integer id) {
		msgRepo.deleteById(id);
	}
	
	public Messages latestMsg() {
		return msgRepo.findLatest();
	}
	
	public Messages latestMsg2() {
		Pageable pgb = PageRequest.of(0, 1, Sort.Direction.DESC, "added");
		
		List<Messages> result = msgRepo.findLatest2(pgb);
		
		if(result.isEmpty()) {
			return null;
		}
		
		return result.get(0);
	}
	
	public Messages latestMsg3() {
		return msgRepo.findFirstByOrderByAddedDesc();
	}
	
	public Page<Messages> findByPage(Integer pageNumber){
		Pageable pgb = PageRequest.of(pageNumber-1, 3, Sort.Direction.DESC, "added");
		Page<Messages> page = msgRepo.findAll(pgb);
		return page;
	}
	

}
