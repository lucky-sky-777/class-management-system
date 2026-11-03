package com.mezon.classmanagement.backend.domain_document.main.document_bookmark.service;

import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import com.mezon.classmanagement.backend.domain_document.main.document.service.DocumentService;
import com.mezon.classmanagement.backend.domain_document.main.document_bookmark.dto.DocumentBookmarkResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.document_bookmark.entity.DocumentBookmark;
import com.mezon.classmanagement.backend.domain_document.main.document_bookmark.mapper.DocumentBookmarkMapper;
import com.mezon.classmanagement.backend.domain_document.main.document_bookmark.repository.DocumentBookmarkRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class DocumentBookmarkService {

	DocumentBookmarkRepository documentBookmarkRepository;
	DocumentService documentService;
	DocumentBookmarkMapper documentBookmarkMapper;

	@Transactional
	public void saveOrUnsave(
			Long clientUserId,
			Long documentId
	) {
		documentService.throwIfNotExistsById(documentId);

		documentService.throwIfNotIsPublicOrInAnyClassJoinedByUser(documentId, clientUserId);

		DocumentBookmark documentBookmark = findByDocumentAndCreatorOrNull(
				documentId, clientUserId
		);

		if (documentBookmark == null) {
			save(
					DocumentBookmark.builder()
							.document(Document.create(documentId))
							.creator(User.create(clientUserId))
							.build()
			);
		} else {
			delete(documentBookmark);
		}

	}

	@Transactional(readOnly = true)
	public List<DocumentBookmarkResponseDto> getListByUser(Long clientUserId) {
		return documentBookmarkMapper
				.toDocumentBookmarkResponseDtoList(
						documentBookmarkRepository
								.findAllByCreator_IdOrderByCreatedAtDesc(clientUserId)
				);
	}

	@Transactional(readOnly = true)
	public List<DocumentBookmarkResponseDto> getListByDocument(Long documentId) {
		return documentBookmarkMapper
				.toDocumentBookmarkResponseDtoList(
						documentBookmarkRepository
								.findAllByDocument_IdAndClassIsPublic(documentId)
				);
	}

	@Transactional(readOnly = true)
	public boolean existsByDocumentAndCreator(
			Long documentId,
			Long creatorUserId
	) {
		return documentBookmarkRepository.existsByDocument_IdAndCreator_Id(documentId, creatorUserId);
	}

	@Transactional
	public DocumentBookmark findByDocumentAndCreatorOrNull(
			Long documentId,
			Long creatorUserId
	) {
		return documentBookmarkRepository
				.findByDocument_IdAndCreator_Id(
						documentId,
						creatorUserId
				)
				.orElseGet(() -> null
//						DocumentBookmark.builder()
//								.document(Document.create(documentId))
//								.creator(User.create(creatorUserId))
//								.build()
				);
	}

	@Transactional
	public DocumentBookmark save(DocumentBookmark documentBookmark) {
		return documentBookmarkRepository.save(documentBookmark);
	}

	@Transactional
	public void delete(DocumentBookmark documentBookmark) {
		documentBookmarkRepository.delete(documentBookmark);
	}

}