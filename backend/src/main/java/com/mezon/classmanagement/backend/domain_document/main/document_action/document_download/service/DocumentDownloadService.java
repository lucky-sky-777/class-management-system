package com.mezon.classmanagement.backend.domain_document.main.document_action.document_download.service;

import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import com.mezon.classmanagement.backend.domain_document.main.document.service.DocumentService;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_download.dto.DocumentDownloadResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_download.entity.DocumentDownload;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_download.mapper.DocumentDownloadMapper;
import com.mezon.classmanagement.backend.domain_document.main.document_action.document_download.repository.DocumentDownloadRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class DocumentDownloadService {

	DocumentDownloadRepository documentDownloadRepository;
	DocumentService documentService;
	DocumentDownloadMapper documentDownloadMapper;

	@Transactional
	public void create(
			Long clientUserId,
			Long documentId
	) {
		documentService.throwIfNotExistsById(documentId);

		documentService.throwIfNotIsPublicOrInAnyClassJoinedByUser(documentId, clientUserId);

		save(
				DocumentDownload.builder()
						.document(Document.create(documentId))
						.creator(User.create(clientUserId))
						.build()
		);

	}

	@Transactional(readOnly = true)
	public List<DocumentDownloadResponseDto> getListByUser(Long clientUserId) {
		return documentDownloadMapper
				.toDocumentDownloadResponseDtoList(
						documentDownloadRepository
								.findAllByCreator_IdOrderByCreatedAtDesc(clientUserId)
				);
	}

	@Transactional(readOnly = true)
	public List<DocumentDownload> getRawListByUser(Long clientUserId) {
		return documentDownloadRepository
				.findAllByCreator_IdOrderByCreatedAtDesc(clientUserId);
	}

	@Transactional(readOnly = true)
	public List<DocumentDownloadResponseDto> getListByDocument(Long documentId) {
		return documentDownloadMapper
				.toDocumentDownloadResponseDtoList(
						documentDownloadRepository
								.findAllByDocument_IdAndClassIsPublic(documentId)
				);
	}

	@Transactional
	public DocumentDownload findByDocumentAndCreatorOrNull(
			Long documentId,
			Long creatorUserId
	) {
		return documentDownloadRepository
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
	public DocumentDownload save(DocumentDownload documentDownload) {
		return documentDownloadRepository.save(documentDownload);
	}

	@Transactional
	public void delete(DocumentDownload documentDownload) {
		documentDownloadRepository.delete(documentDownload);
	}

}