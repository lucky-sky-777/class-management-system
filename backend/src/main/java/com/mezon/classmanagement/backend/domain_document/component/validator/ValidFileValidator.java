package com.mezon.classmanagement.backend.domain_document.component.validator;

import com.mezon.classmanagement.backend.common.constant.FileTypeUtils;
import com.mezon.classmanagement.backend.common.util.FileUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public class ValidFileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

	private Set<String> allowedMime;
	private long maxSize;

	@Override
	public void initialize(ValidFile annotation) {
		allowedMime = FileTypeUtils.ALLOWED_MIME_TYPE_SET;
		maxSize = annotation.maxSize();
	}

	@Override
	public boolean isValid(
			MultipartFile file,
			ConstraintValidatorContext context
	) {
		if (file == null || file.isEmpty()) {
			return false;
		}

		if (file.getSize() > maxSize) {
			return false;
		}

		try {
			return allowedMime.isEmpty()
					|| allowedMime.contains(FileUtils.detectMimeTypeFromMultipartFile(file));
		} catch (Exception e) {
			return false;
		}
	}
}