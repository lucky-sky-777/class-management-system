package com.mezon.classmanagement.backend.domain.absencerequest.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.security.annotation.RequireClassSecurity;
import com.mezon.classmanagement.backend.domain.absencerequest.dto.request.CreateAbsenceRequestRequestDto;
import com.mezon.classmanagement.backend.domain.absencerequest.dto.response.AbsenceRequestIdResponseDto;
import com.mezon.classmanagement.backend.domain.absencerequest.dto.response.AbsenceRequestResponseDto;
import com.mezon.classmanagement.backend.domain.absencerequest.entity.AbsenceRequest;
import com.mezon.classmanagement.backend.domain.absencerequest.mapper.AbsenceRequestMapper;
import com.mezon.classmanagement.backend.domain.absencerequest.repository.AbsenceRequestRepository;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain.clazz.entity.Class;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class AbsenceRequestService {

	/**
	 * Repository
	 */

	AbsenceRequestRepository absenceRequestRepository;

	/**
	 * Mapper
	 */

	AbsenceRequestMapper absenceRequestMapper;

	@RequireClassSecurity
	@Transactional
	public AbsenceRequestResponseDto create(
			Long classId,
			Long creatorUserId,
			CreateAbsenceRequestRequestDto request
	) {
		throwIfExistsByClassIdAndCreatorUserIdAndStatus(classId, creatorUserId, AbsenceRequest.Status.PENDING);

		Class clazz = Class.create(classId);
		User creator = User.create(creatorUserId);

		AbsenceRequest newAbsenceRequest = absenceRequestMapper.toAbsenceRequest(request);
		newAbsenceRequest.setClazz(clazz);
		newAbsenceRequest.setCreator(creator);

		AbsenceRequest responseAbsenceRequest = save(newAbsenceRequest);

		return absenceRequestMapper.toAbsenceRequestResponseDto(responseAbsenceRequest);
	}

	@RequireClassSecurity
	@Transactional
	public AbsenceRequestIdResponseDto approve(
			Long classId,
			Long actorUserId,
			Long absenceRequestId
	) {
		AbsenceRequest currentAbsenceRequest = findByClassIdAndAbsenceRequestIdOrThrow(classId, absenceRequestId);

		User actor = User.create(actorUserId);

		currentAbsenceRequest.setStatus(AbsenceRequest.Status.APPROVED);
		currentAbsenceRequest.setActor(actor);
		currentAbsenceRequest.setActedAt(Instant.now());

		AbsenceRequest responseAbsenceRequest = save(currentAbsenceRequest);

		return new AbsenceRequestIdResponseDto(
				responseAbsenceRequest.getId()
		);
	}

	@RequireClassSecurity
	@Transactional
	public AbsenceRequestIdResponseDto reject(
			Long classId,
			Long actorUserId,
			Long absenceRequestId
	) {
		AbsenceRequest currentAbsenceRequest = findByClassIdAndAbsenceRequestIdOrThrow(classId, absenceRequestId);

		User actor = User.create(actorUserId);

		currentAbsenceRequest.setStatus(AbsenceRequest.Status.REJECTED);
		currentAbsenceRequest.setActor(actor);
		currentAbsenceRequest.setActedAt(Instant.now());

		AbsenceRequest responseAbsenceRequest = save(currentAbsenceRequest);

		return new AbsenceRequestIdResponseDto(
				responseAbsenceRequest.getId()
		);
	}

	@RequireClassSecurity
	@Transactional
	public AbsenceRequestIdResponseDto cancel(
			Long classId,
			Long actorUserId,
			Long absenceRequestId
	) {
		AbsenceRequest currentAbsenceRequest = findByClassIdAndCreatorUserIdAndAbsenceRequestIdOrThrow(classId, actorUserId, absenceRequestId);

		User actor = User.create(actorUserId);

		currentAbsenceRequest.setStatus(AbsenceRequest.Status.CANCELLED);
		currentAbsenceRequest.setActor(actor);
		currentAbsenceRequest.setActedAt(Instant.now());

		AbsenceRequest responseAbsenceRequest = save(currentAbsenceRequest);

		return new AbsenceRequestIdResponseDto(
				responseAbsenceRequest.getId()
		);
	}

	@RequireClassSecurity
	@Transactional(readOnly = true)
	public List<AbsenceRequestResponseDto> getListByClass(Long classId) {
		return getListByClassId(classId);
	}

	@RequireClassSecurity
	@Transactional(readOnly = true)
	public List<AbsenceRequestResponseDto> getListByClassAndCreator(
			Long classId,
			Long creatorUserId
	) {
		return getListByClassIdAndCreatorUserId(classId, creatorUserId);
	}

	/**
	 * Action
	 */

	@Transactional
	public AbsenceRequest save(AbsenceRequest absenceRequest) {
		return absenceRequestRepository.save(absenceRequest);
	}

	/**
	 * Find
	 */

	@Transactional(readOnly = true)
	public List<AbsenceRequestResponseDto> getListByClassId(Long classId) {
		return absenceRequestRepository
				.getByClazz_IdOrderByCreatedAtDesc(classId);
	}

	@Transactional(readOnly = true)
	public List<AbsenceRequestResponseDto> getListByClassIdAndCreatorUserId(
			Long classId,
			Long creatorUserId
	) {
		return absenceRequestRepository
				.getByClazz_IdAndCreator_IdOrderByCreatedAtDesc(classId, creatorUserId);
	}

	@Transactional(readOnly = true)
	public AbsenceRequest findByClassIdAndAbsenceRequestIdOrThrow(
			Long classId,
			Long absenceRequestId
	) {
		List<AbsenceRequest> absenceRequestList = absenceRequestRepository
				.findByClazz_IdAndId(classId, absenceRequestId);
		throwIfEmptyList(absenceRequestList);

		AbsenceRequest absenceRequest = absenceRequestList.getFirst();
		throwIfNotPending(absenceRequest);

		return absenceRequest;
	}

	@Transactional(readOnly = true)
	public AbsenceRequest findByClassIdAndCreatorUserIdAndAbsenceRequestIdOrThrow(
			Long classId,
			Long creatorUserId,
			Long absenceRequestId
	) {
		List<AbsenceRequest> absenceRequestList = absenceRequestRepository
				.findByClazz_IdAndCreator_IdAndId(classId, creatorUserId, absenceRequestId);
		throwIfEmptyList(absenceRequestList);

		AbsenceRequest absenceRequest = absenceRequestList.getFirst();
		throwIfNotPending(absenceRequest);

		return absenceRequest;
	}

	private void throwIfEmptyList(List<AbsenceRequest> absenceRequestList) {
		if (absenceRequestList.isEmpty()) {
			throw new GlobalException(GlobalException.Type.NOT_FOUND, "Absence request not found");
		}
	}

	private void throwIfNotPending(AbsenceRequest absenceRequest) {
		if (!isPending(absenceRequest.getStatus())) {
			throw new GlobalException(GlobalException.Type.INVALID_REQUEST, "Not a pending request");
		}
	}

	/**
	 * Exists
	 */

	@Transactional(readOnly = true)
	public boolean existsByClassIdAndCreatorUserIdAndStatus(
			Long classId,
			Long creatorUserId,
			AbsenceRequest.Status status
	) {
		return absenceRequestRepository.existsByClazz_IdAndCreator_IdAndStatus(classId, creatorUserId, status);
	}

	@Transactional(readOnly = true)
	public void throwIfExistsByClassIdAndCreatorUserIdAndStatus(
			Long classId,
			Long creatorUserId,
			AbsenceRequest.Status status
	) {
		if (existsByClassIdAndCreatorUserIdAndStatus(classId, creatorUserId, status)) {
			throw new GlobalException(GlobalException.Type.ALREADY_EXISTS, "Absence request exists");
		}
	}

	/**
	 * Validate
	 */

	public boolean isPending(AbsenceRequest.Status status) {
		return AbsenceRequest.Status.PENDING == status;
	}

}