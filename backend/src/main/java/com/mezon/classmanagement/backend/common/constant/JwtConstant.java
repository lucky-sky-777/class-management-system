package com.mezon.classmanagement.backend.common.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class JwtConstant {

	@Value(value = "${jwt.signer-key}")
	public String SIGNER_KEY;

}