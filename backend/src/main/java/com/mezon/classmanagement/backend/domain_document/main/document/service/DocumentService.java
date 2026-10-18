package com.mezon.classmanagement.backend.domain_document.main.document.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.util.FileUtils;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain_document.component.embedding.service.EmbeddingService;
import com.mezon.classmanagement.backend.domain_document.component.vector.entity.impl.Vector1536;
import com.mezon.classmanagement.backend.domain_document.main.directory.service.DirectoryService;
import com.mezon.classmanagement.backend.domain_document.main.directory_document.service.DirectoryDocumentService;
import com.mezon.classmanagement.backend.domain_document.main.document.dto.request.CreateDocumentRequestDto;
import com.mezon.classmanagement.backend.domain_document.main.document.dto.request.InternalUpdateDocumentRequestDto;
import com.mezon.classmanagement.backend.domain_document.main.document.dto.response.DocumentResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import com.mezon.classmanagement.backend.domain_document.main.document.mapper.DocumentMapper;
import com.mezon.classmanagement.backend.domain_document.main.document.repository.DocumentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class DocumentService {

	ApplicationEventPublisher applicationEventPublisher;

	public record DocumentCreatedEvent(Long directoryId, Long documentId) {
	}

	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	@RequiredArgsConstructor
	@Component
	public static class DirectoryEventListener {

		DirectoryDocumentService directoryDocumentService;

		@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
		public void handleDirectoryCreated(DocumentCreatedEvent event) {
			directoryDocumentService.create(event.directoryId(), event.documentId());
		}

	}

	DirectoryService directoryService;
	EmbeddingService embeddingService;
	DocumentRepository documentRepository;
	DocumentMapper documentMapper;

	@Transactional
	public DocumentResponseDto create(
			Long creatorUserId,
			Long directoryId,
			CreateDocumentRequestDto requestDto,
			MultipartFile multipartFile
	) {
		directoryService.throwIfNotExistsById(directoryId);

		Document newDocument = documentMapper.toDocument(requestDto);

		newDocument.setKey(UUID.randomUUID().toString());
		newDocument.setFileName(multipartFile.getOriginalFilename());
		newDocument.setFileExtension(FileUtils.getExtension(multipartFile));
		newDocument.setCreator(User.create(creatorUserId));
		newDocument.setStatus(Document.Status.UPLOADING);

		Document responseDocument = save(newDocument);

		applicationEventPublisher.publishEvent(
				new DocumentCreatedEvent(
						directoryId, responseDocument.getId()
				)
		);

		return documentMapper.toDocumentResponseDto(responseDocument);
	}

	@Transactional
	public void updateInternal(Long id, InternalUpdateDocumentRequestDto request) {
		Document currentDocument = findByIdOrThrow(id);

		documentMapper.updateDocumentFromRequestDto(request, currentDocument);
		try {
			if (currentDocument.getDescription() != null && !currentDocument.getDescription().isBlank()) {
				currentDocument.setEmbedding(
						new Vector1536(
								embeddingService.embedSingleDocumentText(
										currentDocument.getTitle(),
										currentDocument.getDescription()
								).getValues()
						)
				);
			}
		} catch (Exception e) {}
	}

	@Transactional
	public Document save(Document document) {
		return documentRepository.save(document);
	}

	@Transactional(readOnly = true)
	public Document findByKeyOrThrow(String key) {
		return documentRepository.findByKey(key).orElseThrow(() ->
				new GlobalException(
						GlobalException.Type.NOT_EXISTS,
						"Document not exists"
				)
		);
	}

	@Transactional(readOnly = true)
	public Document findByIdOrThrow(Long id) {
		return documentRepository.findById(id).orElseThrow(() ->
				new GlobalException(
						GlobalException.Type.NOT_EXISTS,
						"Document not exists"
				)
		);
	}

}