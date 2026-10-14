package com.mezon.classmanagement.backend.domain_document.component.document_tag.tag.repository;

import com.mezon.classmanagement.backend.domain_document.component.document_tag.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Tag> findByIdIn(List<Long> ids);
}
