package com.mezon.classmanagement.backend.domain.classuser.classuser_request.repository;

import com.mezon.classmanagement.backend.domain.classuser.classuser_request.dto.response.ClassUserRequestResponseDto;
import com.mezon.classmanagement.backend.domain.classuser.classuser_request.entity.ClassUserRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassUserRequestRepository extends JpaRepository<ClassUserRequest, Long> {

	boolean existsByClazz_IdAndCreator_IdAndStatus(Long classId, Long creatorUserId, ClassUserRequest.Status status);

	List<ClassUserRequest> findByClazz_IdAndId(Long classId, Long classUserRequestId);
	List<ClassUserRequest> findByClazz_IdAndCreator_IdAndId(Long classId, Long creatorUserId, Long classUserRequestId);

	@Query("""
	select new com.mezon.classmanagement.backend.domain.classuser.classuser_request.dto.response.ClassUserRequestResponseDto(
		classUserRequest.id,
		class.id,
		classUserRequest.message,
		creator.id,
		creator.displayName,
		creator.avatarUrl,
		classUserRequest.createdAt,
		classUserRequest.status,
		actor.id,
		actor.displayName,
		actor.avatarUrl,
		classUserRequest.actedAt
	)
	from ClassUserRequest classUserRequest
	join classUserRequest.clazz class
	left join classUserRequest.creator creator
	left join classUserRequest.actor actor
	where class.id = :classId
	order by classUserRequest.createdAt desc
	""")
	List<ClassUserRequestResponseDto> getByClazz_IdOrderByCreatedAtDesc(Long classId);

	@Query("""
	select new com.mezon.classmanagement.backend.domain.classuser.classuser_request.dto.response.ClassUserRequestResponseDto(
		classUserRequest.id,
		class.id,
		classUserRequest.message,
		creator.id,
		creator.displayName,
		creator.avatarUrl,
		classUserRequest.createdAt,
		classUserRequest.status,
		actor.id,
		actor.displayName,
		actor.avatarUrl,
		classUserRequest.actedAt
	)
	from ClassUserRequest classUserRequest
	join classUserRequest.clazz class
	left join classUserRequest.creator creator
	left join classUserRequest.actor actor
	where creator.id = :creatorUserId
	order by classUserRequest.createdAt desc
	""")
	List<ClassUserRequestResponseDto> getByCreator_IdOrderByCreatedAtDesc(Long creatorUserId);

}