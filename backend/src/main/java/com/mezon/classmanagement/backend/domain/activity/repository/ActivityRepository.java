package com.mezon.classmanagement.backend.domain.activity.repository;

import com.mezon.classmanagement.backend.domain.activity.dto.response.ActivityResponseDto;
import com.mezon.classmanagement.backend.domain.activity.dto.response.ActivitySummaryResponseDto;
import com.mezon.classmanagement.backend.domain.activity.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

	Optional<Activity> findByClazz_IdAndId(Long classId, Long activityId);

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

	@Query("""
        SELECT new com.mezon.classmanagement.backend.domain.activity.dto.response.ActivitySummaryResponseDto(
            null,
            classUser.user.id,
            classUser.user.displayName,
            classUser.user.avatarUrl,
            cast(COALESCE(SUM(activity.point), 0) as short)
        )
        FROM ClassUser classUser
        LEFT JOIN ActivityRegistration activityRegistration
            ON activityRegistration.creator.id = classUser.user.id
            AND activityRegistration.clazz.id = classUser.clazz.id
            AND activityRegistration.status = 'APPROVED'
        LEFT JOIN Activity activity
            ON activity.id = activityRegistration.activity.id
        WHERE classUser.clazz.id = :classId
        GROUP BY classUser.user.id, classUser.user.displayName, classUser.user.avatarUrl
        ORDER BY COALESCE(SUM(activity.point), 0) DESC
    """)
	List<ActivitySummaryResponseDto> getSummaries(Long classId);

}