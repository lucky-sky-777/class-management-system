package com.mezon.classmanagement.backend.domain_document.main.interest.repository;

import com.mezon.classmanagement.backend.domain_document.main.interest.interest.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterestRepository extends JpaRepository<Interest, Long> {
    Optional<Interest> findByUserId(Long userId);
};
