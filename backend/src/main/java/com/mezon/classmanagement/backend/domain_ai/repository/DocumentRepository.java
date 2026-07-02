package com.mezon.classmanagement.backend.domain_ai.repository;


import com.mezon.classmanagement.backend.domain_ai.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}