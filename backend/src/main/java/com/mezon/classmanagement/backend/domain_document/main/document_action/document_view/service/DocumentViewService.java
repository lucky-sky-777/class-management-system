package com.mezon.classmanagement.backend.domain_document.main.document_action.document_view.service;

import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import com.mezon.classmanagement.backend.domain_document.main.document.service.DocumentService;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_view.dto.DocumentViewResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_view.entity.DocumentView;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_view.mapper.DocumentViewMapper;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_view.repository.DocumentViewRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class DocumentViewService {

	DocumentViewRepository documentViewRepository;
	DocumentService documentService;
	DocumentViewMapper documentViewMapper;

	@Transactional
	public void create(
			Long clientUserId,
			Long documentId
	) {
		documentService.throwIfNotExistsById(documentId);

		documentService.throwIfNotIsPublicOrInAnyClassJoinedByUser(documentId, clientUserId);

		save(
				DocumentView.builder()
						.document(Document.create(documentId))
						.creator(User.create(clientUserId))
						.build()
		);

	}

	@Transactional(readOnly = true)
	public List<DocumentViewResponseDto> getListByUser(Long clientUserId) {
		return documentViewMapper
				.toDocumentViewResponseDtoList(
						documentViewRepository
								.findAllByCreator_IdOrderByCreatedAtDesc(clientUserId)
				);
	}

	@Transactional(readOnly = true)
	public List<DocumentView> getRawListByUser(Long clientUserId) {
		return documentViewRepository
				.findAllByCreator_IdOrderByCreatedAtDesc(clientUserId);
	}

	@Transactional(readOnly = true)
	public List<DocumentViewResponseDto> getListByDocument(Long documentId) {
		return documentViewMapper
				.toDocumentViewResponseDtoList(
						documentViewRepository
								.findAllByDocument_IdAndClassIsPublic(documentId)
				);
	}

	@Transactional
	public DocumentView findByDocumentAndCreatorOrNull(
			Long documentId,
			Long creatorUserId
	) {
		return documentViewRepository
				.findByDocument_IdAndCreator_Id(
						documentId,
						creatorUserId
				)
				.orElseGet(() -> null
//						DocumentDownload.builder()
//								.document(Document.create(documentId))
//								.creator(User.create(creatorUserId))
//								.build()
				);
	}

	@Transactional
	public DocumentView save(DocumentView documentBookmark) {
		return documentViewRepository.save(documentBookmark);
	}

	@Transactional
	public void delete(DocumentView documentBookmark) {
		documentViewRepository.delete(documentBookmark);
	}

}