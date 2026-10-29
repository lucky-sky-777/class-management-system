package com.mezon.classmanagement.backend.domain_document.main.directory_document.repository;

import com.mezon.classmanagement.backend.domain_document.main.directory_document.entity.DirectoryDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DirectoryDocumentRepository extends JpaRepository<DirectoryDocument, Long> {

	boolean existsByDirectoryIdAndDocumentId(Long directoryId, Long documentId);

	@Query("""
		select dd.document.id
		from DirectoryDocument dd
		where dd.directory.id = :directoryId
	""")
	List<Long> getDocumentIdListByDirectoryId(
			@Param("directoryId") Long directoryId
	);

}