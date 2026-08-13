package com.mezon.classmanagement.backend_document.common.exeption.controller;

import com.mezon.classmanagement.backend_document.common.constant.WarningConstant;
import com.mezon.classmanagement.backend_document.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend_document.common.exeption.entity.GlobalException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@SuppressWarnings(value = {WarningConstant.UNUSED})
@RestControllerAdvice
public class GlobalExceptionController {

	@ExceptionHandler(value = GlobalException.class)
	public ResponseEntity<ResponseDTO<Void>> handleGlobalException(GlobalException globalException) {
		return ResponseEntity
				.status(globalException.getCode())
				.body(
						ResponseDTO.fail(
								globalException.getCode(),
								globalException.getMessage()
						)
				);
	}

}