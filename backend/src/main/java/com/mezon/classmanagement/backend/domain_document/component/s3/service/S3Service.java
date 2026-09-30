package com.mezon.classmanagement.backend.domain_document.component.s3.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.config.s3.S3Config;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class S3Service {

	S3Client s3Client;

	private boolean bucketNotExists(String bucketName) {
		return !bucketExists(bucketName);
	}

	private boolean bucketExists(String bucketName) {
		try {
			s3Client.headBucket(
					HeadBucketRequest.builder()
							.bucket(bucketName)
							.build()
			);

			return true;
		} catch (S3Exception e) {
			if (e.statusCode() == 404) {
				return false;
			}

			throw e;
		}
	}

	private void makeBucket(String bucketName) {
		s3Client.createBucket(
				CreateBucketRequest.builder()
						.bucket(bucketName)
						.build()
		);
	}

	private void makeIfBucketNotExists(String bucketName) {
		if (bucketNotExists(bucketName)) {
			makeBucket(bucketName);
		}
	}

	private void throwIfBucketNotExists(String bucketName) {
		if (bucketNotExists(bucketName)) {
			throw new GlobalException(GlobalException.Type.INTERNAL_SERVER_ERROR, "Internal server error");
		}
	}

	public String uploadFileToPath(
			MultipartFile file,
			String bucketName,
			String targetPath
	) throws Exception {
		throwIfBucketNotExists(bucketName);

		String originalFilename = file.getOriginalFilename();
		String storageFileName = UUID.randomUUID().toString();

		if (StringUtils.hasText(targetPath)) {
			targetPath = ensureForwardSlash(targetPath);
		} else {
			targetPath = "";
		}

		String objectName = targetPath + storageFileName;

		createFile(bucketName, objectName, file);

		return objectName;
	}

	public void createUserDirectory(
			Long userId,
			String directoryName
	) throws Exception {
		createDirectory(
				S3Config.Bucket.ROOT,
				FileUtils.buildPath(
						S3Config.Directory.UPLOAD_USER,
						userId.toString(),
						directoryName
				)
		);
	}

	private String ensureForwardSlash(String directoryName) {
		return directoryName.endsWith("/") ? directoryName : directoryName + "/";
	}

	public void createDirectory(
			String bucketName,
			String directoryName
	) throws Exception {
		directoryName = ensureForwardSlash(directoryName);
		createEmptyFile(bucketName, directoryName);
	}

	public void createFile(
			String bucketName,
			String directoryName,
			MultipartFile multipartFile
	) throws Exception {
		try (InputStream inputStream = multipartFile.getInputStream()) {
			s3Client.putObject(
					PutObjectRequest.builder()
							.bucket(bucketName)
							.key(directoryName)
							.contentType(FileUtils.getMimeType(multipartFile))
							.build(),
					RequestBody.fromInputStream(
							inputStream,
							multipartFile.getSize()
					)
			);
		}
	}

	public void createEmptyFile(
			String bucketName,
			String directoryName
	) throws Exception {
		try (InputStream inputStream = new ByteArrayInputStream(new byte[0])) {
			s3Client.putObject(
					PutObjectRequest.builder()
							.bucket(bucketName)
							.key(directoryName)
							.build(),
					RequestBody.fromInputStream(
							inputStream,
							0L
					)
			);
		}
	}

}