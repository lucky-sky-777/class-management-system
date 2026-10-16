package com.mezon.classmanagement.backend.domain_document.component.document_tag.tag.repository;

import com.mezon.classmanagement.backend.domain_document.component.document_tag.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Tag> findByIdIn(List<Long> ids);

    @Query(value = """
    SELECT pg_advisory_xact_lock(
    	hashtextextended('tags:slug:' || :slug, 0)
    )
    """, nativeQuery = true
	)
	void lockSlug(@Param("slug") String slug);
}
