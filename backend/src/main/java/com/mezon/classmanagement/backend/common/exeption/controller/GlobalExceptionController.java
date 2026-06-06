package com.mezon.classmanagement.backend.common.exeption.controller;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
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

	@ExceptionHandler(value = DataIntegrityViolationException.class)
	public ResponseEntity<ResponseDTO<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException dataIntegrityViolationException) {
		return ResponseEntity
				.status(GlobalException.Type.INVALID_REQUEST.getCode())
				.body(
						ResponseDTO.fail(
								GlobalException.Type.INVALID_REQUEST.getCode(),
								"Invalid request"
						)
				);
	}

	@ExceptionHandler(value = MissingServletRequestParameterException.class)
	public ResponseEntity<ResponseDTO<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException missingServletRequestParameterException) {
		return ResponseEntity
				.status(GlobalException.Type.INVALID_REQUEST.getCode())
				.body(
						ResponseDTO.fail(
								GlobalException.Type.INVALID_REQUEST.getCode(),
								"Invalid request"
						)
				);
	}

	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseDTO<Void>> handleValidationException(MethodArgumentNotValidException methodArgumentNotValidException) {
		return ResponseEntity
				.status(GlobalException.Type.INVALID_REQUEST.getCode())
				.body(
						ResponseDTO.fail(
								GlobalException.Type.INVALID_REQUEST.getCode(),
								"Invalid request"
						)
				);
	}

}