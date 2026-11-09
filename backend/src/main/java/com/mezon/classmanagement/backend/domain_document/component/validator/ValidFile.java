package com.mezon.classmanagement.backend.domain_document.component.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({
		ElementType.FIELD,
		ElementType.PARAMETER,
		ElementType.TYPE_USE
})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidFileValidator.class)
@Documented
public @interface ValidFile {

	String message() default "Invalid file";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String[] mime() default {};

	long maxSize() default Long.MAX_VALUE;
}