package com.mezon.classmanagement.backend.domain.activity.activityregistration.repository;

import com.mezon.classmanagement.backend.domain.activity.activityregistration.dto.response.ActivityRegistrationResponseDto;
import com.mezon.classmanagement.backend.domain.activity.activityregistration.entity.ActivityRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRegistrationRepository extends JpaRepository<ActivityRegistration, Long> {

	boolean existsByClazz_IdAndActivity_IdAndCreator_IdAndStatus(
			Long classId,
			Long activityId,
			Long creatorUserId,
			ActivityRegistration.Status status
	);

	List<ActivityRegistration> findByClazz_IdAndActivity_IdAndId(
			Long classId,
			Long activityId,
			Long activityRegistrationId
	);
	List<ActivityRegistration> findByClazz_IdAndActivity_IdAndCreator_IdAndId(
			Long classId,
			Long activityId,
			Long creatorUserId,
			Long activityRegistrationId
	);

	@Query(value = """
	select new com.mezon.classmanagement.backend.domain.activity.activityregistration.dto.response.ActivityRegistrationResponseDto(
		activityRegistration.id,
		class.id,
		activity.id,
		activityRegistration.proofUrl,
		activityRegistration.createdAt,
		creator.id,
		creator.displayName,
		creator.avatarUrl,
		activityRegistration.status,
		actor.id,
		actor.displayName,
		actor.avatarUrl
	)
	from ActivityRegistration activityRegistration
	join activityRegistration.clazz class
	join activityRegistration.activity activity
	left join activityRegistration.creator creator
	left join activityRegistration.actor actor
	where class.id = :classId and activity.id = :activityId
	""")
	List<ActivityRegistrationResponseDto> getByClazz_IdAndActivity_Id(
			Long classId,
			Long activityId
	);

}