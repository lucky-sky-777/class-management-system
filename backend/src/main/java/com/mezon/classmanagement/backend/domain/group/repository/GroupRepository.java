package com.mezon.classmanagement.backend.domain.group.repository;

import com.mezon.classmanagement.backend.domain.group.dto.GroupResponseDto;
import com.mezon.classmanagement.backend.domain.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
	List<Group> findByClazz_Id(Long classId);
	Optional<Group> findByClazz_IdAndId(Long classId, Long groupId);

	@Query(value = """
	select new com.mezon.classmanagement.backend.domain.group.dto.GroupResponseDto(
		group.id,
		class.id,
		group.name,
		group.createdAt
	)
	from Group group
	join group.clazz class
	where class.id = :classId
	order by group.createdAt asc
	""")
	List<GroupResponseDto> getByClazz_IdOrderByCreatedAtAsc(Long classId);

	boolean existsByClazz_IdAndId(Long classId, Long groupId);
}