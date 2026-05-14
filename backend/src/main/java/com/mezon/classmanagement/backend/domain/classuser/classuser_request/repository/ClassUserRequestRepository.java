package com.mezon.classmanagement.backend.domain.classuser.classuser_request.repository;

import com.mezon.classmanagement.backend.domain.classuser.classuser_request.dto.response.ClassUserRequestResponseDto;
import com.mezon.classmanagement.backend.domain.classuser.classuser_request.entity.ClassUserRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassUserRequestRepository extends JpaRepository<ClassUserRequest, Long> {
	boolean existsByClazz_IdAndUser_IdAndStatus(Long classId, Long userId, ClassUserRequest.Status status);

	List<ClassUserRequest> findByClazz_IdAndId(Long classId, Long classUserRequestId);
	List<ClassUserRequest> findByClazz_IdAndUser_IdAndId(Long classId, Long userId, Long classUserRequestId);

	List<ClassUserRequest> findByClazz_IdOrderByCreatedAtDesc(Long classId);
	List<ClassUserRequest> findByUser_IdOrderByCreatedAtDesc(Long userId);

	@Query("""
	select new com.mezon.classmanagement.backend.domain.classuser.classuser_request.dto.response.ClassUserRequestResponseDto(
		classUserRequest.id,
		class.id,
		user.id,
		user.displayName,
		user.avatarUrl,
		classUserRequest.message,
		classUserRequest.status,
		classUserRequest.createdAt
	)
	from ClassUserRequest classUserRequest
	join classUserRequest.clazz class
	join classUserRequest.user user
	where class.id = :classId
	order by classUserRequest.createdAt desc
	""")
	List<ClassUserRequestResponseDto> getByClazz_IdOrderByCreatedAtDesc(Long classId);
}