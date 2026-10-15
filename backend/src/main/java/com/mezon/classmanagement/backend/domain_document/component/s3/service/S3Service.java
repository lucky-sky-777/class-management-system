package com.mezon.classmanagement.backend.domain_document.component.s3.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.config.s3.S3Config;
import dev.langchain4j.data.document.Metadata;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.awt.im.InputContext;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class S3Service {

	S3Client s3Client;

	private boolean bucketNotExists(String bucketName) {
		return !bucketExists(bucketName);
	}

	public boolean bucketExists(String bucketName) {
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
			InputStream inputStream,
			Metadata metadata,
			String bucketName,
			String targetPath,
			String storageFileName
	) throws Exception {
		throwIfBucketNotExists(bucketName);

		if (StringUtils.hasText(targetPath)) {
			targetPath = ensureForwardSlash(targetPath);
		} else {
			targetPath = "";
		}

		String objectName = targetPath + storageFileName;

		createFile(bucketName, objectName, inputStream, metadata);

		return objectName;
	}

	public boolean exists() {
		return false;
	}

	public InputStream getTempFile(String fileName) {
		return s3Client.getObject(
				GetObjectRequest.builder()
						.bucket(S3Config.Bucket.ROOT)
						.key(
								FileUtils
										.buildPath(
												S3Config.Directory.UPLOAD_TEMP,
												fileName
										)
						)
						.build()
		);
	}

	public InputStream getFile(String filePath) {
		return s3Client.getObject(
				GetObjectRequest.builder()
						.bucket(S3Config.Bucket.ROOT)
						.key(filePath)
						.build()
		);
	}

	public void deleteTempFile(String fileName) {
		deleteFile(S3Config.Bucket.ROOT, S3Config.Directory.UPLOAD_TEMP, fileName);
	}

	public void deleteFile(String bucketName, String directoryName, String fileName) {
		s3Client.deleteObject(
				DeleteObjectRequest.builder()
						.bucket(bucketName)
						.key(
								FileUtils.buildPath(
										directoryName,
										fileName
								)
						)
						.build()
		);
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
	) {
		directoryName = ensureForwardSlash(directoryName);
		try {
			createEmptyFile(bucketName, directoryName);
		} catch (Exception e) {
			throw new GlobalException(
					GlobalException.Type.INTERNAL_SERVER_ERROR,
					"Can not create Directory"
			);
		}
	}

	public void createFile(
			String bucketName,
			String directoryName,
			InputStream inputStream,
			Metadata metadata
	) throws Exception {
		try (inputStream) {
			s3Client.putObject(
					PutObjectRequest.builder()
							.bucket(bucketName)
							.key(directoryName)
							.contentType(metadata.getString("fileMimeType"))
							.build(),
					RequestBody.fromInputStream(
							inputStream,
							metadata.getLong("fileSize")
					)
			);
		}
	}

	private void createEmptyFile(
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