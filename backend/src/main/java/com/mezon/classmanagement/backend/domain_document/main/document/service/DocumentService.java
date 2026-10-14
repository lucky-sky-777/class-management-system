package com.mezon.classmanagement.backend.domain_document.main.document.service;

import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain_document.main.document.dto.request.CreateDocumentRequestDto;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import com.mezon.classmanagement.backend.domain_document.main.document.mapper.DocumentMapper;
import com.mezon.classmanagement.backend.domain_document.main.document.repository.DocumentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class DocumentService {

	DocumentRepository documentRepository;
	DocumentMapper documentMapper;

	@Transactional
	public void create(
			Long creatorUserId,
			CreateDocumentRequestDto requestDto,
			MultipartFile multipartFile
	) {
		Document newDocument = documentMapper.toDocument(requestDto);

		User creator = User.create(creatorUserId);

		newDocument.setFileName(multipartFile.getOriginalFilename());
		newDocument.setFileExtension(FileUtils.getExtension(multipartFile));
		newDocument.setCreator(creator);
		newDocument.setStatus(Document.Status.UPLOADING);

		save(newDocument);
	}

	@Transactional
	public Document save(Document document) {
		return documentRepository.save(document);
	}

}