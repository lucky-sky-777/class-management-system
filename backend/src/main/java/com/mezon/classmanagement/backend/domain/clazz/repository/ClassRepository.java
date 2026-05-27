package com.mezon.classmanagement.backend.domain.clazz.repository;

import com.mezon.classmanagement.backend.domain.clazz.dto.ClassResponseDto;
import com.mezon.classmanagement.backend.domain.clazz.entity.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {

	Optional<Class> findByCode(String code);

	/*
	@Query(value = """
		SELECT new com.mezon.classmanagement.backend.dto.clazz.ClassResponseDtoponseDto(
			class.id,
			owner.id,
			class.name,
			class.description,
			class.code,
			class.avatarUrl,
			class.privacy,
			class.createdAt
		)
		FROM Class class
		JOIN class.owner owner
		WHERE owner.id = :userId
	""")
	List<ClassResponseDto> getJoinedClasses(Long userId);
	*/

//	@Query(value = """
//		SELECT new com.mezon.classmanagement.backend.domain.clazz.dto.ClassResponseDto(
//			clazz.id,
//			clazz.owner.id,
//			clazz.owner.displayName,
//			clazz.owner.avatarUrl,
//			clazz.name,
//			clazz.description,
//			clazz.code,
//			clazz.avatarUrl,
//			clazz.privacy,
//			clazz.createdAt
//		)
//		FROM ClassUser classUser
//		JOIN classUser.clazz clazz
//		WHERE classUser.user.id = :userId
//	""")
//	List<ClassResponseDto> getJoinedClasses2(Long userId);
//
//	@Query(value = """
//		SELECT new com.mezon.classmanagement.backend.domain.clazz.dto.ClassResponseDto(
//			class.id,
//			class.owner.id,
//			class.owner.displayName,
//			class.owner.avatarUrl,
//			class.name,
//			class.description,
//			class.code,
//			class.avatarUrl,
//			class.privacy,
//			class.createdAt
//		)
//		FROM ClassUserRequest classUserRequest
//		JOIN classUserRequest.clazz class
//		join classUserRequest.user user
//		WHERE user.id = :userId
//	""")
//	List<ClassResponseDto> getJoinedClasses3(Long userId);
//
//	@Query(value = """
//		SELECT new com.mezon.classmanagement.backend.domain.clazz.dto.ClassResponseDto(
//			clazz.id,
//			clazz.owner.id,
//			clazz.owner.displayName,
//			clazz.owner.avatarUrl,
//			clazz.name,
//			clazz.description,
//			clazz.code,
//			clazz.avatarUrl,
//			clazz.privacy,
//			clazz.createdAt
//		)
//		FROM Class clazz
//		JOIN ClassUser classUser
//			on classUser.user.id = :userId
//		join ClassUserRequest classUserRequest
//			on classUserRequest.user.id = :userId and classUserRequest.status = 'PENDING'
//	""")
//	List<ClassResponseDto> getJoinedClasses4(Long userId);

	@Query("""
	SELECT DISTINCT new com.mezon.classmanagement.backend.domain.clazz.dto.ClassResponseDto(
		clazz.id,
		clazz.owner.id,
		clazz.owner.displayName,
		clazz.owner.avatarUrl,
		clazz.name,
		clazz.description,
		clazz.code,
		clazz.avatarUrl,
		clazz.privacy,
		CASE
			WHEN classUser.user.id IS NOT NULL
				THEN 'JOINED'
			ELSE 'PENDING_REQUEST'
		END,
		clazz.createdAt
	)
	FROM Class clazz
	LEFT JOIN ClassUser classUser
		ON classUser.clazz.id = clazz.id
		AND classUser.user.id = :userId
	WHERE
		classUser.id IS NOT NULL
		OR EXISTS (
			SELECT 1
			FROM ClassUserRequest request
			WHERE request.clazz.id = clazz.id
				AND request.creator.id = :userId
				AND request.status = 'PENDING'
		)
	""")
	List<ClassResponseDto> getJoinedClasses2(Long userId);

	@Query("""
	SELECT new com.mezon.classmanagement.backend.domain.clazz.dto.ClassResponseDto(
		clazz.id,
		clazz.owner.id,
		clazz.owner.displayName,
		clazz.owner.avatarUrl,
		clazz.name,
		clazz.description,
		clazz.code,
		clazz.avatarUrl,
		clazz.privacy,
		CASE
			WHEN classUser.id IS NOT NULL
				THEN 'JOINED'
			ELSE 'PENDING_REQUEST'
		END,
		clazz.createdAt
	)
	FROM Class clazz

	LEFT JOIN ClassUser classUser
		ON classUser.clazz.id = clazz.id
		AND classUser.user.id = :userId

	LEFT JOIN ClassUserRequest request
		ON request.clazz.id = clazz.id
		AND request.creator.id = :userId
		AND request.status = 'PENDING'

	WHERE
		classUser.id IS NOT NULL
		OR request.id IS NOT NULL
	ORDER BY
		CASE
			WHEN classUser.joinedAt IS NULL THEN request.createdAt
			WHEN request.createdAt IS NULL THEN classUser.joinedAt
			WHEN classUser.joinedAt > request.createdAt THEN classUser.joinedAt
			ELSE request.createdAt
		END
	DESC
	""")
	List<ClassResponseDto> getJoinedClasses(Long userId);

}