package com.ispan.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.demo.model.Photos;
import com.ispan.demo.model.PhotosRepository;

@Service
public class PhotoService {
	
	@Autowired
	private PhotosRepository photoRepository;
	
	public Photos insertPhoto(Photos photos) {
		return photoRepository.save(photos);
	}
	
	public Photos findPhotosById(Integer id) {
		Optional<Photos> optional = photoRepository.findById(id);
		
		if(optional.isEmpty()) {
			return null;
		}
		
		return optional.get();
		
	}
	
	public List<Photos> findAllPhotos(){
		return photoRepository.findAll();
	}

}
