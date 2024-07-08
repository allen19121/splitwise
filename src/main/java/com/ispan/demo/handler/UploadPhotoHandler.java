package com.ispan.demo.handler;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class UploadPhotoHandler {
	
	@ExceptionHandler(value = MaxUploadSizeExceededException.class)
	public String handleUploadSize(Model model) {
		model.addAttribute("error", "檔案太大");
		
		return "photos/uploadPage";
	}

}
