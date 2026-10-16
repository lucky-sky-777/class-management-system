package com.mezon.classmanagement.backend.config.s3.env;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class S3Env {

	@Value(value = "${s3.endpoint}")
	public String endpoint;

	@Value(value = "${s3.access-key}")
	public String accessKey;

	@Value(value = "${s3.secret-key}")
	public String secretKey;

	@Value(value = "${s3.region}")
	public String region;

}