package com.mezon.classmanagement.backend.s3;

import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.config.s3.S3Config;
import com.mezon.classmanagement.backend.domain_document.component.s3.service.S3Service;
import com.mezon.classmanagement.backend.domain_document.main.directory.dto.CreateDirectoryRequestDto;
import com.mezon.classmanagement.backend.domain_document.main.directory.service.DirectoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class S3Test {

	DirectoryService directoryService;
	S3Service s3Service;

	@Test
	public void uploadTest() throws Exception {
		String fileName = "triethocmaclenin.docx";
		ClassPathResource classPathResource = new ClassPathResource(fileName);

		MockMultipartFile multipartFile = new MockMultipartFile(fileName, fileName, FileUtils.getMimeType(classPathResource.getInputStream().readAllBytes()), classPathResource.getInputStream());

		System.out.println(FileUtils.getMimeType(multipartFile));
		String name = s3Service.uploadFileToPath(multipartFile, "nckh-ktpm-k46", "test-upload");

		System.out.println(name);
	}

	//@Test
	public void createUserDirectoryTest() throws Exception {
		Long id = 1L;
		String directoryName = UUID.randomUUID().toString();

		CreateDirectoryRequestDto request = new CreateDirectoryRequestDto();
		request.setName(directoryName);

		//directoryService.createForUser(id, request);
		s3Service.createDirectory(S3Config.Bucket.ROOT, FileUtils.buildPath(S3Config.Directory.UPLOAD_USER, id.toString(), directoryName));
	}

	//@Test
	public void createClassDirectoryTest() {
		Long classId = 1L;
		Long userId = 1L;
		String directoryName = UUID.randomUUID().toString();

		CreateDirectoryRequestDto request = new CreateDirectoryRequestDto();
		request.setName(directoryName);

		directoryService.createForClass(classId, userId, request);
	}

}