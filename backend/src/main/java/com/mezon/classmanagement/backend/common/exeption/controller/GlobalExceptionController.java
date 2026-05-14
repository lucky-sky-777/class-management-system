package com.mezon.classmanagement.backend.common.exeption.controller;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@SuppressWarnings({WarningConstant.UNUSED})
@RestControllerAdvice
public class GlobalExceptionController {

	@ExceptionHandler(value = GlobalException.class)
	public ResponseDTO<Void> handleGlobalException(GlobalException globalException) {
		return ResponseDTO.<Void>builder()
				.success(false)
				.code(globalException.getCode())
				.message(globalException.getMessage())
				.build();
	}

	@ExceptionHandler(value = DataIntegrityViolationException.class)
	public ResponseDTO<Void> handleDataIntegrityViolationException(DataIntegrityViolationException dataIntegrityViolationException) {
		return ResponseDTO.<Void>builder()
				.success(false)
				.code(GlobalException.Type.INVALID_REQUEST.getCode())
				.message("Invalid request")
				.build();
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseDTO<Void> handleValidationException(MethodArgumentNotValidException methodArgumentNotValidException) {
		return ResponseDTO.<Void>builder()
				.success(false)
				.code(GlobalException.Type.INVALID_REQUEST.getCode())
				.message("Invalid request")
				.build();
	}

}