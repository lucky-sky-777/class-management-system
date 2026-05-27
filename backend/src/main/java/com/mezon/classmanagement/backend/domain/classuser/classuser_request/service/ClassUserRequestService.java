package com.mezon.classmanagement.backend.domain.classuser.classuser_request.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.security.annotation.RequireClassSecurity;
import com.mezon.classmanagement.backend.common.security.permission.ClassRole;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain.classuser.classuser_request.dto.ClassUserRequestIdResponseDto;
import com.mezon.classmanagement.backend.domain.classuser.classuser_request.dto.CreateClassUserRequestRequestDto;
import com.mezon.classmanagement.backend.domain.classuser.classuser_request.dto.response.ClassUserRequestResponseDto;
import com.mezon.classmanagement.backend.domain.classuser.classuser_request.entity.ClassUserRequest;
import com.mezon.classmanagement.backend.domain.classuser.classuser_request.mapper.ClassUserRequestMapper;
import com.mezon.classmanagement.backend.domain.classuser.classuser_request.repository.ClassUserRequestRepository;
import com.mezon.classmanagement.backend.domain.classuser.dto.CreateClassUserRequestDto;
import com.mezon.classmanagement.backend.domain.classuser.service.ClassUserService;
import com.mezon.classmanagement.backend.domain.clazz.entity.Class;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class ClassUserRequestService {

	/**
	 * Repository
	 */

	ClassUserRequestRepository classUserRequestRepository;

	/**
	 * Mapper
	 */

	ClassUserRequestMapper classUserRequestMapper;

	/**
	 * Service
	 */

	ApplicationEventPublisher applicationEventPublisher;

	@Transactional
	public void create(
			Long classId,
			Long creatorUserId,
			CreateClassUserRequestRequestDto request
	) {
		throwIfExistsByClassIdAndCreatorUserIdAndStatus(classId, creatorUserId, ClassUserRequest.Status.PENDING);

		Class clazz = Class.create(classId);
		User creator = User.create(creatorUserId);

		ClassUserRequest newClassUserRequest = classUserRequestMapper.toClassUserRequest(request);
		newClassUserRequest.setClazz(clazz);
		newClassUserRequest.setCreator(creator);

		save(newClassUserRequest);
	}

	@RequireClassSecurity
	@Transactional
	public ClassUserRequestIdResponseDto approve(
			Long classId,
			Long actorUserId,
			Long classUserRequestId
	) {
		ClassUserRequest currentClassUserRequest = findByClassIdAndClassUserRequestIdOrThrow(classId, classUserRequestId);

		User actor = User.create(actorUserId);

		currentClassUserRequest.setStatus(ClassUserRequest.Status.APPROVED);
		currentClassUserRequest.setActor(actor);
		currentClassUserRequest.setActedAt(Instant.now());

		ClassUserRequest responseClassUserRequest = save(currentClassUserRequest);

		applicationEventPublisher.publishEvent(
				new RequestApprovedEvent(
						responseClassUserRequest.getClazz().getId(),
						responseClassUserRequest.getCreator().getId()
				)
		);

		return new ClassUserRequestIdResponseDto(
				responseClassUserRequest.getId()
		);
	}

	@RequireClassSecurity
	@Transactional
	public ClassUserRequestIdResponseDto reject(
			Long classId,
			Long actorUserId,
			Long classUserRequestId
	) {
		ClassUserRequest currentClassUserRequest = findByClassIdAndClassUserRequestIdOrThrow(classId, classUserRequestId);

		User actor = User.create(actorUserId);

		currentClassUserRequest.setStatus(ClassUserRequest.Status.REJECTED);
		currentClassUserRequest.setActor(actor);
		currentClassUserRequest.setActedAt(Instant.now());

		ClassUserRequest responseClassUserRequest = save(currentClassUserRequest);

		return new ClassUserRequestIdResponseDto(
				responseClassUserRequest.getId()
		);
	}

	@Transactional
	public ClassUserRequestIdResponseDto cancel(
			Long classId,
			Long actorUserId,
			Long classUserRequestId
	) {
		ClassUserRequest currentClassUserRequest = findByClassIdAndCreatorUserIdAndClassUserRequestIdOrThrow(classId, actorUserId, classUserRequestId);

		User actor = User.create(actorUserId);

		currentClassUserRequest.setStatus(ClassUserRequest.Status.CANCELLED);
		currentClassUserRequest.setActor(actor);
		currentClassUserRequest.setActedAt(Instant.now());

		ClassUserRequest responseClassUserRequest = save(currentClassUserRequest);

		return new ClassUserRequestIdResponseDto(
				responseClassUserRequest.getId()
		);
	}

	@RequireClassSecurity
	@Transactional(readOnly = true)
	public List<ClassUserRequestResponseDto> getListByClass(Long classId) {
		return getListByClassId(classId);
	}

	@Transactional(readOnly = true)
	public List<ClassUserRequestResponseDto> getListByCreator(Long creatorUserId) {
		return getListByCreatorUserId(creatorUserId);
	}

	/**
	 * Action
	 */

	@Transactional
	public ClassUserRequest save(ClassUserRequest classUserRequest) {
		return classUserRequestRepository.save(classUserRequest);
	}

	/**
	 * Find
	 */

	@Transactional(readOnly = true)
	public List<ClassUserRequestResponseDto> getListByClassId(Long classId) {
		return classUserRequestRepository
				.getByClazz_IdOrderByCreatedAtDesc(classId);
	}

	@Transactional(readOnly = true)
	public List<ClassUserRequestResponseDto> getListByCreatorUserId(Long creatorUserId) {
		return classUserRequestRepository
				.getByCreator_IdOrderByCreatedAtDesc(creatorUserId);
	}

	@Transactional(readOnly = true)
	public ClassUserRequest findByClassIdAndClassUserRequestIdOrThrow(
			Long classId,
			Long classUserRequestId
	) {
		List<ClassUserRequest> classUserRequestList = classUserRequestRepository
				.findByClazz_IdAndId(classId, classUserRequestId);
		throwIfEmptyList(classUserRequestList);

		ClassUserRequest classUserRequest = classUserRequestList.getFirst();
		throwIfNotPending(classUserRequest);

		return classUserRequest;
	}

	@Transactional(readOnly = true)
	public ClassUserRequest findByClassIdAndCreatorUserIdAndClassUserRequestIdOrThrow(
			Long classId,
			Long creatorUserId,
			Long classUserRequestId
	) {
		List<ClassUserRequest> classUserRequestList = classUserRequestRepository
				.findByClazz_IdAndCreator_IdAndId(classId, creatorUserId, classUserRequestId);
		throwIfEmptyList(classUserRequestList);

		ClassUserRequest classUserRequest = classUserRequestList.getFirst();
		throwIfNotPending(classUserRequest);

		return classUserRequest;
	}

	private void throwIfEmptyList(List<ClassUserRequest> classUserRequestList) {
		if (classUserRequestList.isEmpty()) {
			throw new GlobalException(GlobalException.Type.NOT_FOUND, "Join class request not found");
		}
	}

	private void throwIfNotPending(ClassUserRequest classUserRequest) {
		if (!isPending(classUserRequest.getStatus())) {
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
			ClassUserRequest.Status status
	) {
		return classUserRequestRepository.existsByClazz_IdAndCreator_IdAndStatus(classId, creatorUserId, status);
	}

	@Transactional(readOnly = true)
	public void throwIfExistsByClassIdAndCreatorUserIdAndStatus(
			Long classId,
			Long creatorUserId,
			ClassUserRequest.Status status
	) {
		if (existsByClassIdAndCreatorUserIdAndStatus(classId, creatorUserId, status)) {
			throw new GlobalException(GlobalException.Type.ALREADY_EXISTS, "Join class request exists");
		}
	}

	/**
	 * Validate
	 */

	public boolean isPending(ClassUserRequest.Status status) {
		return ClassUserRequest.Status.PENDING == status;
	}

	public record RequestApprovedEvent(Long classId, Long userId) {
	}

	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	@RequiredArgsConstructor
	@Component
	public static class RequestEventListener {

		ClassUserService classUserService;

		@Transactional(propagation = Propagation.REQUIRES_NEW)
		@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
		public void handleRequestApproved(RequestApprovedEvent event) {
			classUserService.createClassUser(
					event.classId(),
					CreateClassUserRequestDto.builder()
							.userId(event.userId())
							.build(),
					ClassRole.CLASS_MEMBER
			);
		}
	}

}