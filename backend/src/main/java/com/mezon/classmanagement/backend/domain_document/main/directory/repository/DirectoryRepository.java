package com.mezon.classmanagement.backend.domain_document.main.directory.repository;

import com.mezon.classmanagement.backend.domain_document.main.directory.entity.Directory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectoryRepository extends JpaRepository<Directory, Long> {
}