package com.mezon.classmanagement.backend.domain_document.main.directory_document.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.domain_document.main.directory.entity.Directory;
import com.mezon.classmanagement.backend.domain_document.main.directory_document.entity.DirectoryDocument;
import com.mezon.classmanagement.backend.domain_document.main.directory_document.repository.DirectoryDocumentRepository;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class DirectoryDocumentService {

	DirectoryDocumentRepository directoryDocumentRepository;

	@Transactional
	public void create(Long directoryId, Long documentId) {
		if (existsByDirectoryIdAndDocumentId(directoryId, documentId)) {
			return;
		}

		DirectoryDocument directoryDocument = new DirectoryDocument();
		directoryDocument.setDirectory(
				Directory.create(directoryId)
		);
		directoryDocument.setDocument(
				Document.create(documentId)
		);

		directoryDocumentRepository.save(directoryDocument);
	}

	@Transactional
	public List<Long> getDocumentIdListByDirectoryId(Long directoryId) {
		return directoryDocumentRepository.getDocumentIdListByDirectoryId(directoryId);
	}

	@Transactional(readOnly = true)
	public boolean existsByDirectoryIdAndDocumentId(Long directoryId, Long documentId) {
		return directoryDocumentRepository.existsByDirectoryIdAndDocumentId(directoryId, documentId);
	}

	@Transactional(readOnly = true)
	public void throwIfExistsByDirectoryIdAndDocumentId(Long directoryId, Long documentId) {
		if (existsByDirectoryIdAndDocumentId(directoryId, documentId)) {
			throw new GlobalException(
					GlobalException.Type.ALREADY_EXISTS,
					"DirectoryDocument already exists"
			);
		}
	}

}