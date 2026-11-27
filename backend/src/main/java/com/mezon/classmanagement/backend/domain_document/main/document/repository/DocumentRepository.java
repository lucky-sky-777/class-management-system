package com.mezon.classmanagement.backend.domain_document.main.document.repository;

import com.mezon.classmanagement.backend.domain.clazz.entity.Class;
import com.mezon.classmanagement.backend.domain_document.main.directory.entity.Directory;
import com.mezon.classmanagement.backend.domain_document.main.document.dto.response.DocumentResponseDto;
import com.mezon.classmanagement.backend.domain_document.main.document.dto.response.RecommendationProjection;
import com.mezon.classmanagement.backend.domain_document.main.document.entity.Document;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("SqlRedundantOrderingDirection")
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

	@Query("""
    SELECT new com.mezon.classmanagement.backend.domain_document.main.document.dto.response.DocumentResponseDto(
        d.id,
        d.title,
        d.description,
        d.fileName,
        COUNT(DISTINCT dv.id),
        COUNT(DISTINCT dl.id),
        COUNT(DISTINCT db2.id),
        CASE
            WHEN db.id IS NOT NULL THEN true
            ELSE false
        END,
        d.creator.id,
        d.creator.displayName,
        d.creator.avatarUrl,
        d.createdAt,
        d.status
    )
    FROM Document d
    LEFT JOIN DocumentBookmark db
        ON db.document.id = d.id
        AND db.creator.id = :clientUserId
    LEFT JOIN DocumentView dv
        ON dv.document.id = d.id
    LEFT JOIN DocumentDownload dl
        ON dl.document.id = d.id
    LEFT JOIN DocumentBookmark db2
        ON db2.document.id = d.id
    WHERE EXISTS (
        SELECT dd.id
        FROM DirectoryDocument dd
        WHERE dd.document.id = d.id
            AND dd.directory.id = :directoryId
    )
    GROUP BY
        d.id,
        d.title,
        d.description,
        d.fileName,
        db.id,
        d.creator.id,
        d.creator.displayName,
        d.creator.avatarUrl,
        d.createdAt,
        d.status
    ORDER BY d.createdAt DESC
    """)
	List<DocumentResponseDto> findByDirectoryId(
			@Param("clientUserId") Long clientUserId,
			@Param("directoryId") Long directoryId
	);

	@Query("""
    SELECT new com.mezon.classmanagement.backend.domain_document.main.document.dto.response.DocumentResponseDto(
        d.id,
        d.title,
        d.description,
        d.fileName,
        COUNT(DISTINCT dv.id),
        COUNT(DISTINCT dl.id),
        COUNT(DISTINCT db2.id),
        CASE
            WHEN db.id IS NOT NULL THEN true
            ELSE false
        END,
        d.creator.id,
        d.creator.displayName,
        d.creator.avatarUrl,
        d.createdAt,
        d.status
    )
    FROM Document d
    LEFT JOIN DocumentBookmark db
        ON db.document.id = d.id
        AND db.creator.id = :clientUserId
    LEFT JOIN DocumentView dv
        ON dv.document.id = d.id
    LEFT JOIN DocumentDownload dl
        ON dl.document.id = d.id
    LEFT JOIN DocumentBookmark db2
        ON db2.document.id = d.id
    WHERE d.id = :documentId
    GROUP BY
        d.id,
        d.title,
        d.description,
        d.fileName,
        db.id,
        d.creator.id,
        d.creator.displayName,
        d.creator.avatarUrl,
        d.createdAt,
        d.status
    """)
	Optional<DocumentResponseDto> getById(
			@Param("documentId") Long documentId,
			@Param("clientUserId") Long clientUserId
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
    SELECT
        d.id AS id,
        d.title AS title,
        d.description AS description,
        d.file_name AS fileName,
        COUNT (DISTINCT dv.id) AS viewCount,
        COUNT (DISTINCT dd.id) AS downloadCount,
        COUNT (DISTINCT db_all.id) AS bookmarkCount,
    	CASE
        	WHEN db_user.id IS NOT NULL THEN true
        	ELSE false
        END,
        u.id AS creatorUserId,
        u.display_name AS creatorDisplayName,
        u.avatar_url AS creatorAvatarUrl,
        d.created_at AS createdAt,
        d.status AS status,
        dcm.membership AS membership
    FROM document_cluster_memberships dcm
    JOIN recommendation_clusters rc ON rc.id = dcm.cluster_id
    JOIN documents d ON d.id = dcm.document_id
    JOIN users u ON d.creator_user_id = u.id
    LEFT JOIN document_views dv on d.id = dv.document_id
    LEFT JOIN document_downloads dd on d.id = dd.document_id
    LEFT JOIN document_bookmarks db_all on d.id = db_all.document_id
    LEFT JOIN document_bookmarks db_user ON d.id = db_user.document_id AND :userId = db_user.creator_user_id
    WHERE rc.model_id = :modelId
        AND rc.cluster_index = :clusterIndex
        AND EXISTS (
            SELECT 1
            FROM directory_documents dd
            JOIN directories dir ON dir.id = dd.directory_id
            JOIN classes c ON c.id = dir.class_id
            WHERE dd.document_id = d.id
                AND c.privacy = 'PUBLIC'
        )
    GROUP BY
        d.id,
        d.title,
        d.description,
        d.file_name,
        db_user.id,
        u.id,
        u.display_name,
        u.avatar_url,
        d.created_at,
        d.status,
        dcm.membership
    ORDER BY dcm.membership DESC
    """, nativeQuery = true)
	List<RecommendationProjection> findAllInClusterIndexByModelIdAndPublicClass_native(
			@Param("clusterIndex") Integer clusterIndex,
			@Param("modelId") Long modelId,
			Pageable pageable
	);

	@Query(value = """
    SELECT
    	d.id AS id,
        d.title AS title,
        d.description AS description,
        d.file_name AS fileName,
        COUNT (DISTINCT dv.id) AS viewCount,
        COUNT (DISTINCT dd.id) AS downloadCount,
        COUNT (DISTINCT db_all.id) AS bookmarkCount,
    	CASE
        	WHEN db_user.id IS NOT NULL THEN true
        	ELSE false
        END,
        u.id AS creatorUserId,
        u.display_name AS creatorDisplayName,
        u.avatar_url AS creatorAvatarUrl,
        d.created_at AS createdAt,
        d.status AS status,
        d.embedding <=> CAST(:embedding AS vector) AS cosineDistance,
        (1 - (d.embedding <=> CAST(:embedding AS vector))) AS cosineSimilarity
    FROM documents d
    JOIN users u ON d.creator_user_id = u.id
    LEFT JOIN document_views dv on d.id = dv.document_id
    LEFT JOIN document_downloads dd on d.id = dd.document_id
    LEFT JOIN document_bookmarks db_all on d.id = db_all.document_id
    LEFT JOIN document_bookmarks db_user ON d.id = db_user.document_id AND :userId = db_user.creator_user_id
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
    GROUP BY
        d.id,
        d.title,
        d.description,
        d.file_name,
        db_user.id,
        u.id,
        u.display_name,
        u.avatar_url,
        d.created_at,
        d.status,
        cosineDistance,
        cosineSimilarity
    ORDER BY cosineDistance ASC
    LIMIT :limit
    """, nativeQuery = true)
	List<RecommendationProjection> findAllByEmbeddingCompare(
			@Param("userId") Long userId,
			@Param("embedding") float[] embedding,
			@Param("limit") int limit
	);

	@Query("""
    SELECT dir
    FROM DirectoryDocument dd
    JOIN dd.directory dir
    WHERE dd.document.id = :documentId
    """)
	Optional<Directory> findDirectoryByDocumentId(
			@Param("documentId") Long documentId
	);

	@Query("""
    SELECT dir.clazz
    FROM DirectoryDocument dd
    JOIN dd.directory dir
    WHERE dd.document.id = :documentId
    """)
	Optional<Class> findClassByDocumentId(
			@Param("documentId") Long documentId
	);

}