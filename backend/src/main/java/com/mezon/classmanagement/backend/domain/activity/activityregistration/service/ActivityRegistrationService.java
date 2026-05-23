package com.mezon.classmanagement.backend.domain.activity.activityregistration.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.security.annotation.RequireClassSecurity;
import com.mezon.classmanagement.backend.domain.activity.activityregistration.dto.request.UpdateActivityRegistrationRequestDto;
import com.mezon.classmanagement.backend.domain.activity.activityregistration.dto.response.ActivityRegistrationIdResponseDto;
import com.mezon.classmanagement.backend.domain.activity.activityregistration.dto.response.ActivityRegistrationResponseDto;
import com.mezon.classmanagement.backend.domain.activity.activityregistration.entity.ActivityRegistration;
import com.mezon.classmanagement.backend.domain.activity.activityregistration.mapper.ActivityRegistrationMapper;
import com.mezon.classmanagement.backend.domain.activity.activityregistration.repository.ActivityRegistrationRepository;
import com.mezon.classmanagement.backend.domain.activity.entity.Activity;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain.clazz.entity.Class;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class ActivityRegistrationService {

	/**
	 * Repository
	 */

	ActivityRegistrationRepository activityRegistrationRepository;

	/**
	 * Mapper
	 */

	ActivityRegistrationMapper activityRegistrationMapper;

	@RequireClassSecurity
	@Transactional
	public ActivityRegistrationResponseDto create(
			Long classId,
			Long activityId,
			Long creatorUserId
	) {
		throwIfExistsByClassIdAndActivityIdAndCreatorUserIdAndStatus(
				classId,
				activityId,
				creatorUserId,
				ActivityRegistration.Status.PENDING
		);

		Class clazz = Class.create(classId);
		Activity activity = Activity.create(activityId);
		User creator = User.create(creatorUserId);

		ActivityRegistration newActivityRegistration = ActivityRegistration.builder()
				.clazz(clazz)
				.activity(activity)
				.creator(creator)
				.build();

		ActivityRegistration responseActivityRegistration = save(newActivityRegistration);

		return activityRegistrationMapper.toActivityRegistrationResponseDto(responseActivityRegistration);
	}

	@RequireClassSecurity
	@Transactional
	public ActivityRegistrationResponseDto update(
			Long classId,
			Long activityId,
			Long creatorUserId,
			Long activityRegistrationId,
			UpdateActivityRegistrationRequestDto request
	) {
		ActivityRegistration currentActivityRegistration = findByClassIdAndActivityIdAndCreatorUserIdAndActivityRegistrationIdOrThrow(
				classId,
				activityId,
				creatorUserId,
				activityRegistrationId
		);
		throwIfNotPending(currentActivityRegistration);

		activityRegistrationMapper.updateActivityRegistrationFromRequestDto(request, currentActivityRegistration);

		ActivityRegistration responseActivityRegistration = save(currentActivityRegistration);

		return activityRegistrationMapper.toActivityRegistrationResponseDto(responseActivityRegistration);
	}

	@RequireClassSecurity
	@Transactional
	public ActivityRegistrationIdResponseDto approve(
			Long classId,
			Long activityId,
			Long actorUserId,
			Long activityRegistrationId
	) {
		ActivityRegistration currentActivityRegistration = findByClassIdAndActivityIdAndActivityRegistrationIdOrThrow(
				classId,
				activityId,
				activityRegistrationId
		);

		User actor = User.create(actorUserId);

		currentActivityRegistration.setStatus(ActivityRegistration.Status.APPROVED);
		currentActivityRegistration.setActor(actor);

		ActivityRegistration responseActivityRegistration = save(currentActivityRegistration);

		return ActivityRegistrationIdResponseDto.builder()
				.activityRegistrationId(responseActivityRegistration.getId())
				.build();
	}

	@RequireClassSecurity
	@Transactional
	public ActivityRegistrationIdResponseDto reject(
			Long classId,
			Long activityId,
			Long actorUserId,
			Long activityRegistrationId
	) {
		ActivityRegistration currentActivityRegistration = findByClassIdAndActivityIdAndActivityRegistrationIdOrThrow(
				classId,
				activityId,
				activityRegistrationId
		);

		User actor = User.create(actorUserId);

		currentActivityRegistration.setStatus(ActivityRegistration.Status.REJECTED);
		currentActivityRegistration.setActor(actor);

		ActivityRegistration responseActivityRegistration = save(currentActivityRegistration);

		return ActivityRegistrationIdResponseDto.builder()
				.activityRegistrationId(responseActivityRegistration.getId())
				.build();
	}

	@RequireClassSecurity
	@Transactional
	public ActivityRegistrationIdResponseDto cancel(
			Long classId,
			Long activityId,
			Long creatorUserId,
			Long activityRegistrationId
	) {
		ActivityRegistration currentActivityRegistration = findByClassIdAndActivityIdAndCreatorUserIdAndActivityRegistrationIdOrThrow(
				classId,
				activityId,
				creatorUserId,
				activityRegistrationId
		);

		User creator = User.create(creatorUserId);

		currentActivityRegistration.setStatus(ActivityRegistration.Status.CANCELLED);
		currentActivityRegistration.setActor(creator);

		ActivityRegistration responseActivityRegistration = save(currentActivityRegistration);

		return ActivityRegistrationIdResponseDto.builder()
				.activityRegistrationId(responseActivityRegistration.getId())
				.build();
	}

	@RequireClassSecurity
	@Transactional(readOnly = true)
	public List<ActivityRegistrationResponseDto> getByClassAndActivity(Long classId, Long activityId) {
		return getByClassIdAndActivityId(classId, activityId);
	}

	/**
	 * Action
	 */

	@Transactional
	public ActivityRegistration save(ActivityRegistration activityRegistration) {
		return activityRegistrationRepository.save(activityRegistration);
	}

	/**
	 * Find
	 */

	@Transactional(readOnly = true)
	public ActivityRegistration findByClassIdAndActivityIdAndActivityRegistrationIdOrThrow(
			Long classId,
			Long activityId,
			Long activityRegistrationId
	) {
		List<ActivityRegistration> activityRegistrationList = activityRegistrationRepository
				.findByClazz_IdAndActivity_IdAndId(classId, activityId, activityRegistrationId);
		throwIfEmptyList(activityRegistrationList);

		ActivityRegistration activityRegistration = activityRegistrationList.getFirst();
		throwIfNotPending(activityRegistration);

		return activityRegistration;
	}

	@Transactional(readOnly = true)
	public ActivityRegistration findByClassIdAndActivityIdAndCreatorUserIdAndActivityRegistrationIdOrThrow(
			Long classId,
			Long fundId,
			Long creatorUserId,
			Long activityRegistrationId
	) {
		List<ActivityRegistration> activityRegistrationList = activityRegistrationRepository
				.findByClazz_IdAndActivity_IdAndCreator_IdAndId(classId, fundId, creatorUserId, activityRegistrationId);
		throwIfEmptyList(activityRegistrationList);

		ActivityRegistration activityRegistration = activityRegistrationList.getFirst();
		throwIfNotPending(activityRegistration);

		return activityRegistration;
	}

	@Transactional(readOnly = true)
	public List<ActivityRegistrationResponseDto> getByClassIdAndActivityId(
			Long classId,
			Long activityId
	) {
		return activityRegistrationRepository.getByClazz_IdAndActivity_Id(classId, activityId);
	}

	private void throwIfEmptyList(List<ActivityRegistration> activityRegistrationList) {
		if (activityRegistrationList.isEmpty()) {
			throw new GlobalException(GlobalException.Type.NOT_FOUND, "Activity registration not found");
		}
	}

	private void throwIfNotPending(ActivityRegistration activityRegistration) {
		if (!isPending(activityRegistration.getStatus())) {
			throw new GlobalException(GlobalException.Type.INVALID_REQUEST, "Not a pending request");
		}
	}

	/**
	 * Exists
	 */

	@Transactional(readOnly = true)
	public boolean existsByClassIdAndActivityIdAndCreatorUserIdAndStatus(
			Long classId,
			Long activityId,
			Long creatorUserId,
			ActivityRegistration.Status status
	) {
		return activityRegistrationRepository.existsByClazz_IdAndActivity_IdAndCreator_IdAndStatus(
				classId,
				activityId,
				creatorUserId,
				status
		);
	}

	@Transactional(readOnly = true)
	public void throwIfExistsByClassIdAndActivityIdAndCreatorUserIdAndStatus(
			Long classId,
			Long activityId,
			Long creatorUserId,
			ActivityRegistration.Status status
	) {
		if (existsByClassIdAndActivityIdAndCreatorUserIdAndStatus(
				classId,
				activityId,
				creatorUserId,
				status
		)) {
			throw new GlobalException(GlobalException.Type.ALREADY_EXISTS, "Activity registration exists");
		}
	}

	/**
	 * Validate
	 */

	public boolean isPending(ActivityRegistration.Status status) {
		return ActivityRegistration.Status.PENDING == status;
	}


}