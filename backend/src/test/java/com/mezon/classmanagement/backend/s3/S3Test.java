package com.mezon.classmanagement.backend.s3;

import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.config.s3.S3Config;
import com.mezon.classmanagement.backend.domain_document.component.s3.service.S3Service;
import com.mezon.classmanagement.backend.domain_document.main.directory.dto.CreateDirectoryRequestDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.InputStream;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class S3Test {

	S3Service s3Service;

	@Test
	public void uploadTest() throws Exception {
		String fileName = "triethocmaclenin.docx";
		ClassPathResource classPathResource = new ClassPathResource(fileName);
		InputStream inputStream = classPathResource.getInputStream();

		MockMultipartFile multipartFile = new MockMultipartFile(
				"upload",
				fileName,
				FileUtils.detectMimeTypeFromInputStream(inputStream, fileName),
				inputStream
		);

		String storagePathName = "test-upload/";
		String storageFileName = UUID.randomUUID() + "." + FileUtils.getExtension(multipartFile);
		String pathWithName = s3Service.uploadFileToPath(
				multipartFile,
				S3Config.Bucket.ROOT,
				storagePathName,
				storageFileName
		);

		System.out.println(pathWithName);
		assert (storagePathName + storageFileName)
				.equals(pathWithName) : "Upload failed";
	}

	//@Test
	public void createClassDirectoryTest() throws Exception {
		long classId = 1L;
		long userId = 1L;
		String directoryName = UUID.randomUUID().toString();

		CreateDirectoryRequestDto request = new CreateDirectoryRequestDto();
		request.setName(directoryName);

		s3Service.createDirectory(
				S3Config.Bucket.ROOT,
				FileUtils.buildPath(
						S3Config.Directory.UPLOAD_USER,
						Long.toString(classId),
						directoryName
				)
		);
	}

}