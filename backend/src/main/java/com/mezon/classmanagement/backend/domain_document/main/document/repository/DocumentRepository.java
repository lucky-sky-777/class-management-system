package com.mezon.classmanagement.backend.domain_document.main.document.repository;

import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

	@Query("""
    SELECT CASE WHEN COUNT(dd) > 0 THEN true ELSE false END
    FROM DirectoryDocument dd
    JOIN dd.directory directory
    WHERE dd.document.id = :documentId
        AND directory.clazz.privacy = 'PRIVATE'
    """)
	boolean isPrivate(@Param("documentId") Long documentId);

	@Query("""
    SELECT CASE WHEN COUNT(dd) > 0 THEN true ELSE false END
    FROM DirectoryDocument dd
    JOIN dd.directory directory
    WHERE dd.document.id = :documentId
        AND EXISTS (
            SELECT cu.id
            FROM ClassUser cu
            WHERE cu.clazz.id = directory.clazz.id
                AND cu.user.id = :userId
        )
    """)
	boolean existsInAnyClassJoinedByUser(
			@Param("documentId") Long documentId,
			@Param("userId") Long userId
	);

	@Query("""
    SELECT CASE WHEN COUNT(dd) > 0 THEN true ELSE false END
    FROM DirectoryDocument dd
    JOIN dd.directory directory
    WHERE dd.document.id = :documentId
        AND (
            directory.clazz.privacy = 'PUBLIC'
            OR EXISTS (
                SELECT cu.id
                FROM ClassUser cu
                WHERE cu.clazz.id = directory.clazz.id
                    AND cu.user.id = :userId
            )
        )
    """)
	boolean isPublicOrInAnyClassJoinedByUser(
			@Param("documentId") Long documentId,
			@Param("userId") Long userId
	);

	@Query("""
    SELECT CASE WHEN COUNT(dd) > 0 THEN true ELSE false END
    FROM DirectoryDocument dd
    JOIN dd.directory directory
    WHERE dd.document.id = :documentId
        AND directory.clazz.privacy = 'PUBLIC'
    """)
	boolean isPublic(@Param("documentId") Long documentId);

    Optional<Document> findByKey(String key);

	List<Document> findAllByStatus(Document.Status status);

	@Query(value = """
		SELECT id, embedding
		FROM documents
	""", nativeQuery = true)
	List<Document> getAll();

	@Query(value = """
    SELECT d.*
    FROM documents d
    WHERE d.id IN (
        SELECT dd.document_id
        FROM directory_documents dd
        WHERE dd.directory_id = :directoryId
    )
    order by d.created_at desc
    """, nativeQuery = true)
	List<Document> findByDirectoryId(
			@Param("directoryId") Long directoryId
	);

	@Query("""
    SELECT DISTINCT dd.document.id
    FROM DirectoryDocument dd
    JOIN dd.directory directory
    JOIN dd.document document
    WHERE document.creator.id = :creatorUserId
        AND directory.clazz.id = :classId
        AND directory.id = :directoryId
    """)
	List<Long> getDocumentIdListByCreatorAndClassAndDirectory(
			@Param("creatorUserId") Long creatorUserId,
			@Param("classId") Long classId,
			@Param("directoryId") Long directoryId
	);

	@Query("""
	select d.key
	from Document d
	where d.id in :documentIdList
	""")
	List<String> getDocumentKeyListByDocumentIdList(
			@Param("documentIdList") List<Long> documentIdList
	);

	@Query(value = """
	select document
	from DirectoryDocument dd
	join dd.directory directory
	join dd.document document
	where document.id in :documentIdList
		and directory.clazz.privacy <> 'PRIVATE'
	""")
	List<Document> findAllByIdInAndClassNotPrivate(List<Long> documentIdList);

	@Query("""
    select document
    from DocumentClusterMembership dcm
    join dcm.document document
    where dcm.cluster.id in :clusterIdList
        and exists (
            select dd.id
            from DirectoryDocument dd
            join dd.directory directory
            where dd.document.id = document.id
                and directory.clazz.privacy = 'PUBLIC'
        )
    order by dcm.membership desc
    """)
	List<Document> findAllByClusterIdInAndPublicClass(List<Long> clusterIdList, Pageable pageable);

	@Query("""
    select document
    from DocumentClusterMembership dcm
    join dcm.document document
    where dcm.cluster.model.id = :modelId and dcm.cluster.clusterIndex = :clusterIndex
        and exists (
            select dd.id
            from DirectoryDocument dd
            join dd.directory directory
            where dd.document.id = document.id
                and directory.clazz.privacy = 'PUBLIC'
        )
    order by dcm.membership desc
    """)
	List<Document> findAllInClusterIndexByModelIdAndPublicClass(Integer clusterIndex, Long modelId, Pageable pageable);

	@Query(value = """
    SELECT d.*
    FROM documents d
    WHERE EXISTS (
        SELECT 1
        FROM directory_documents dd
        JOIN directories dir ON dir.id = dd.directory_id
        JOIN classes c ON c.id = dir.class_id
        WHERE dd.document_id = d.id
            AND (
                c.privacy = 'PUBLIC'
                OR EXISTS (
                    SELECT 1
                    FROM class_users cu
                    WHERE cu.class_id = c.id
                        AND cu.user_id = :userId
                )
            )
    )
    ORDER BY d.embedding <=> CAST(:embedding AS vector)
    LIMIT :limit
    """, nativeQuery = true)
	List<Document> findAllByEmbeddingCompare(
			@Param("userId") Long userId,
			@Param("embedding") float[] embedding,
			@Param("limit") int limit
	);

}