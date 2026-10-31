package com.mezon.classmanagement.backend.common.exeption.controller;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<?> handleMaxUploadSizeExceeded(
			MaxUploadSizeExceededException ex,
			HttpServletRequest request) {

		System.err.println("Upload URI: " + request.getRequestURI());
		System.err.println("Content-Length: " + request.getContentLengthLong());

		Throwable cause = ex;
		while (cause != null) {
			System.err.println(
					"Cause: " + cause.getClass().getName()
							+ " - " + cause.getMessage()
			);
			cause = cause.getCause();
		}

		ex.printStackTrace();

		return ResponseEntity
				.status(HttpStatus.CONTENT_TOO_LARGE)
				.body("Maximum upload size exceeded");
	}

}