package com.mezon.classmanagement.backend.domain_document.main.interest.topic.repository;

import com.mezon.classmanagement.backend.domain_document.main.interest.topic.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    boolean existsBySlug(String slug);

    Optional<Topic> findBySlug(String slug);
    long countByIdIn(List<Long> ids);
}