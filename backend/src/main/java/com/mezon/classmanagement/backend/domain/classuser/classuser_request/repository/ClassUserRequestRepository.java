package com.mezon.classmanagement.backend.domain.classuser.classuser_request.repository;

import com.mezon.classmanagement.backend.domain.classuser.classuser_request.entity.ClassUserRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassUserRequestRepository extends JpaRepository<ClassUserRequest, Long> {
	boolean existsByClazz_IdAndUser_IdAndStatus(Long classId, Long userId, ClassUserRequest.Status status);

	List<ClassUserRequest> findByClazz_IdAndId(Long classId, Long classUserRequestId);
	List<ClassUserRequest> findByClazz_IdAndUser_IdAndId(Long classId, Long userId, Long classUserRequestId);

	List<ClassUserRequest> findByClazz_IdOrderByCreatedAtDesc(Long classId);
	List<ClassUserRequest> findByUser_IdOrderByCreatedAtDesc(Long userId);
}