package com.mezon.classmanagement.backend.domain_document.main.document.controller;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import com.mezon.classmanagement.backend.domain.auth.service.JwtService;
import com.mezon.classmanagement.backend.domain_document.component.ingest.dto.IngestRequest;
import com.mezon.classmanagement.backend.domain_document.component.ingest.service.IngestService;
import com.mezon.classmanagement.backend.domain_document.component.validator.ValidFile;
import com.mezon.classmanagement.backend.domain_document.main.document.dto.response.DocumentResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import com.mezon.classmanagement.backend.domain_document.main.document.service.DocumentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({WarningConstant.SPELL_CHECKING_INSPECTION})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/classes/{classId}/directories/{directoryId}/documents")
@RestController
public class DocumentController {

	AuthService authService;
	JwtService jwtService;

	DocumentService documentService;
	IngestService ingestService;

	private void ensureFileCountPerRequest(List<MultipartFile> multipartFileList) {
		if (multipartFileList.size() > 100) {
			throw new GlobalException(
					GlobalException.Type.INVALID_REQUEST,
					"Chỉ nhận tối đa 100 file mỗi yêu cầu"
			);
		}
	}

	private void ensureFileSizePerRequest(List<MultipartFile> multipartFileList) {
		long totalFileSize = 0L;
		for (MultipartFile multipartFile : multipartFileList) {
			totalFileSize += multipartFile.getSize();
		}
		if (totalFileSize > 500 * 1014 * 1024) {
			throw new GlobalException(
					GlobalException.Type.INVALID_REQUEST,
					"Chỉ nhận tối đa 500 MB mỗi yêu cầu"
			);
		}
	}

	@GetMapping("/{documentId}/download")
	public ResponseEntity<StreamingResponseBody> download(
			@PathVariable Long classId,
			@PathVariable Long directoryId,
			@PathVariable Long documentId
	) {
		Document document = documentService.findByIdOrThrow(documentId);

		StreamingResponseBody body = outputStream -> {
			try (InputStream inputStream =
						 documentService.openStream(classId, directoryId, document)) {
				inputStream.transferTo(outputStream);
			}
		};

		ContentDisposition disposition =
				ContentDisposition.attachment()
						.filename(
								document.getFileName(),
								StandardCharsets.UTF_8
						)
						.build();

		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(
						HttpHeaders.CONTENT_DISPOSITION,
						disposition.toString()
				)
				.body(body);
	}

	@GetMapping
	public ResponseDTO<List<DocumentResponseDto>> getList(
			@PathVariable Long classId,
			@PathVariable Long directoryId
	) {
		List<DocumentResponseDto> responseList = documentService.getList(classId, directoryId);

		return ResponseDTO.ok(
				"Get document list successful",
				responseList
		);
	}

	@GetMapping("/{documentId}/file")
	public ResponseEntity<StreamingResponseBody> readFile(
			@PathVariable Long classId,
			@PathVariable Long directoryId,
			@PathVariable Long documentId
	) {
		Document document = documentService.findByIdOrThrow(documentId);

		StreamingResponseBody body = outputStream -> {
			try (InputStream inputStream =
						 documentService.openStream(classId, directoryId, document)) {
				inputStream.transferTo(outputStream);
			}
		};

		ContentDisposition disposition =
				ContentDisposition.inline()
						.filename(
								document.getFileName(),
								StandardCharsets.UTF_8
						)
						.build();

		return ResponseEntity.ok()
				.contentType(
						MediaType.parseMediaType(FileUtils.detectMimeType(document.getFileExtension()))
				)
				.header(
						HttpHeaders.CONTENT_DISPOSITION,
						disposition.toString()
				)
				//.contentLength(file.size())
				.body(body);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseDTO<List<DocumentResponseDto>> upload(
			@PathVariable Long classId,
			@PathVariable Long directoryId,

			@RequestPart("files")
			List<@ValidFile MultipartFile> multipartFileList
	) {
		ensureFileCountPerRequest(multipartFileList);
		ensureFileSizePerRequest(multipartFileList);

		Authentication authentication = authService.getAuthentication();
		Long creatorUserId = jwtService.extractUserIdFromAuthentication(authentication);
		List<IngestRequest> ingestRequestList = new ArrayList<>();
		for (MultipartFile multipartFile : multipartFileList) {
			ingestRequestList.add(
					new IngestRequest(creatorUserId, classId, directoryId, multipartFile)
			);
		}
		ingestService.ingestMultiple(ingestRequestList);

		return ResponseDTO.ok("Đã tiếp nhận yêu cầu");
	}

	@GetMapping("/{documentId}")
	public ResponseDTO<Void> delete() {
		//
	}

//	@PostMapping(
//			consumes = MediaType.MULTIPART_FORM_DATA_VALUE
//	)
//	public ResponseDTO<DocumentResponseDto> upload(
//			@PathVariable
//			Long classId,
//
//			@PathVariable
//			Long directoryId,
//
////			@RequestPart("request")
////			CreateDocumentRequestDto request,
//
//			@ValidFile
//			@RequestPart("file")
//			MultipartFile multipartFile
//	) {
//		Authentication authentication = authService.getAuthentication();
//		Long creatorUserId = jwtService.extractUserIdFromAuthentication(authentication);
//
//		ingestService.ingestSingle(creatorUserId, classId, directoryId, multipartFile);
//
//		return ResponseDTO.ok("uploading");
//	}

}