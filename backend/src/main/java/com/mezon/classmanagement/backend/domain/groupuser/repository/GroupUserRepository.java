package com.mezon.classmanagement.backend.domain.groupuser.repository;

import com.mezon.classmanagement.backend.domain.groupuser.dto.response.GroupUserResponseDto;
import com.mezon.classmanagement.backend.domain.groupuser.entity.GroupUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, Long> {
	long countByClazz_Id(Long classId);

	boolean existsByClazz_IdAndGroup_IdAndUser_Id(Long classId, Long groupId, Long userId);
	boolean existsByClazz_IdAndGroup_IdAndDeskAndDeskPosition(Long classId, Long groupId, Short desk, Short deskPosition);

	Optional<GroupUser> findByClazz_IdAndGroup_IdAndUser_Id(Long classId, Long groupId, Long userId);
	Optional<GroupUser> findByClazz_IdAndGroup_IdAndDeskAndDeskPosition(Long classId, Long groupId, Short desk, Short deskPosition);

	List<GroupUser> findByClazz_IdAndGroup_Id(Long classId, Long groupId);
	List<GroupUser> findByClazz_IdOrderByGroup_IdAscDeskAscDeskPositionAsc(Long classId);

	@Query(value = """
	select new com.mezon.classmanagement.backend.domain.groupuser.dto.response.GroupUserResponseDto(
		groupUser.id,
		clazz.id,
		group.id,
		group.name,
		user.id,
		user.displayName,
		groupUser.role,
		groupUser.desk,
		groupUser.deskPosition,
		groupUser.joinedAt
	)
	from GroupUser groupUser
	join groupUser.clazz clazz
	join groupUser.group group
	join groupUser.user user
	where clazz.id = :classId
	order by group.id asc, groupUser.desk asc, groupUser.deskPosition asc
	""")
	List<GroupUserResponseDto> getByClazz_IdOrderByGroup_IdAscDeskAscDeskPositionAsc(Long classId);
}