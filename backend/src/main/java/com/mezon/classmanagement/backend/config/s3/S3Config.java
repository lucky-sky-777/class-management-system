package com.mezon.classmanagement.backend.config.s3;

import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.config.s3.env.S3Env;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Configuration
public class S3Config {

	S3Env s3Env;

	public static class Bucket {
		public static final String ROOT = "nckh-ktpm-k46";
	}

	public static class Directory {
		public static final String UPLOAD = FileUtils.buildPath("upload");
		public static final String UPLOAD_USER = FileUtils.buildPath(UPLOAD, "user");
		public static final String UPLOAD_CLASS = FileUtils.buildPath(UPLOAD, "class");
	}

	@Bean
	public S3Client s3Client(
	) {
		return S3Client.builder()
				.endpointOverride(URI.create(s3Env.endpoint))
				.region(Region.of(s3Env.region))
				.credentialsProvider(
						StaticCredentialsProvider.create(
								AwsBasicCredentials.create(
										s3Env.accessKey,
										s3Env.secretKey
								)
						)
				)
				.forcePathStyle(true)
				.build();
	}

}