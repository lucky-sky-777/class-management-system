package com.mezon.classmanagement.backend.domain.activity.repository;

import com.mezon.classmanagement.backend.domain.activity.dto.response.ActivityResponseDto;
import com.mezon.classmanagement.backend.domain.activity.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
	List<Activity> findByClazz_Id(Long classId);

	@Query(value = """
	select new com.mezon.classmanagement.backend.domain.activity.dto.response.ActivityResponseDto(
		activity.id,
		class.id,
		activity.name,
		activity.description,
		activity.startAt,
		activity.endAt,
		activity.registrationEndAt,
		activity.location,
		activity.point,
		activity.isMandatory,
		activity.createdAt
	)
	from Activity activity
	join activity.clazz class
	where class.id = :classId
	order by activity.createdAt desc
	""")
	List<ActivityResponseDto> getByClazz_Id(Long classId);

	Optional<Activity> findByClazz_IdAndId(Long classId, Long activityId);
}