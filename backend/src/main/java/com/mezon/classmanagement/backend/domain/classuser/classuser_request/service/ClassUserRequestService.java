package com.mezon.classmanagement.backend.domain.classuser.classuser_request.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.security.annotation.RequireClassPermission;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain.classuser.classuser_request.dto.ClassUserRequestIdResponseDto;
import com.mezon.classmanagement.backend.domain.classuser.classuser_request.dto.CreateClassUserRequestRequestDto;
import com.mezon.classmanagement.backend.domain.classuser.classuser_request.dto.response.ClassUserRequestResponseDto;
import com.mezon.classmanagement.backend.domain.classuser.classuser_request.entity.ClassUserRequest;
import com.mezon.classmanagement.backend.domain.classuser.classuser_request.mapper.ClassUserRequestMapper;
import com.mezon.classmanagement.backend.domain.classuser.classuser_request.repository.ClassUserRequestRepository;
import com.mezon.classmanagement.backend.domain.classuser.dto.CreateClassUserRequestDto;
import com.mezon.classmanagement.backend.domain.classuser.entity.ClassUser;
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

	public record RequestApprovedEvent(Long classId, Long userId) {}

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
					ClassUser.Role.CLASS_MEMBER
			);
		}
	}

	@Transactional
	public void create(
			Long classId,
			Long userId,
			CreateClassUserRequestRequestDto request
	) {
		throwIfExistsByClassIdAndUserIdAndStatus(classId, userId, ClassUserRequest.Status.PENDING);

		Class clazz = Class.builder()
				.id(classId)
				.build();
		User user = User.builder()
				.id(userId)
				.build();

		ClassUserRequest newClassUserRequest = classUserRequestMapper.toClassUserRequest(request);
		newClassUserRequest.setClazz(clazz);
		newClassUserRequest.setUser(user);

		save(newClassUserRequest);
	}

	@RequireClassPermission
	@Transactional
	public ClassUserRequestIdResponseDto approve(
			Long classId,
			Long actorUserId,
			Long classUserRequestId
	) {
		ClassUserRequest currentClassUserRequest = findByClassIdAndClassUserRequestIdOrThrow(classId, classUserRequestId);

		User actor = User.builder()
				.id(actorUserId)
				.build();

		currentClassUserRequest.setStatus(ClassUserRequest.Status.APPROVED);
		currentClassUserRequest.setActor(actor);
		currentClassUserRequest.setActedAt(Instant.now());

		ClassUserRequest responseClassUserRequest = save(currentClassUserRequest);

		applicationEventPublisher.publishEvent(
				new RequestApprovedEvent(
						responseClassUserRequest.getClazz().getId(),
						responseClassUserRequest.getUser().getId()
				)
		);

		return ClassUserRequestIdResponseDto.builder()
				.classUserRequestId(responseClassUserRequest.getId())
				.build();
	}

	@RequireClassPermission
	@Transactional
	public ClassUserRequestIdResponseDto reject(
			Long classId,
			Long classUserRequestId
	) {
		ClassUserRequest currentClassUserRequest = findByClassIdAndClassUserRequestIdOrThrow(classId, classUserRequestId);

		currentClassUserRequest.setStatus(ClassUserRequest.Status.REJECTED);

		ClassUserRequest responseClassUserRequest = save(currentClassUserRequest);

		return ClassUserRequestIdResponseDto.builder()
				.classUserRequestId(responseClassUserRequest.getId())
				.build();
	}

	@Transactional
	public ClassUserRequestIdResponseDto cancel(
			Long classId,
			Long userId,
			Long classUserRequestId
	) {
		ClassUserRequest currentClassUserRequest = findByClassIdAndUserIdAndClassUserRequestIdOrThrow(classId, userId, classUserRequestId);

		currentClassUserRequest.setStatus(ClassUserRequest.Status.CANCELLED);

		ClassUserRequest responseClassUserRequest = save(currentClassUserRequest);

		return ClassUserRequestIdResponseDto.builder()
				.classUserRequestId(responseClassUserRequest.getId())
				.build();
	}

	@RequireClassPermission
	@Transactional(readOnly = true)
	public List<ClassUserRequestResponseDto> getByClass(Long classId) {
		List<ClassUserRequest> response = findByClassId(classId);

		return response.stream()
				.map(classUserRequestMapper::toClassUserRequestResponseDto)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<ClassUserRequestResponseDto> getByUser(Long userId) {
		List<ClassUserRequest> response = findByUserId(userId);

		return response.stream()
				.map(classUserRequestMapper::toClassUserRequestResponseDto)
				.toList();
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
	public List<ClassUserRequest> findByClassId(Long classId) {
		return classUserRequestRepository
				.findByClazz_IdOrderByCreatedAtDesc(classId);
	}

	@Transactional(readOnly = true)
	public List<ClassUserRequest> findByUserId(Long userId) {
		return classUserRequestRepository
				.findByUser_IdOrderByCreatedAtDesc(userId);
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
	public ClassUserRequest findByClassIdAndUserIdAndClassUserRequestIdOrThrow(
			Long classId,
			Long userId,
			Long classUserRequestId
	) {
		List<ClassUserRequest> classUserRequestList = classUserRequestRepository
				.findByClazz_IdAndUser_IdAndId(classId, userId, classUserRequestId);
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
	public boolean existsByClassIdAndUserIdAndStatus(Long classId, Long userId, ClassUserRequest.Status status) {
		return classUserRequestRepository.existsByClazz_IdAndUser_IdAndStatus(classId, userId, status);
	}

	@Transactional(readOnly = true)
	public void throwIfExistsByClassIdAndUserIdAndStatus(Long classId, Long userId, ClassUserRequest.Status status) {
		if (existsByClassIdAndUserIdAndStatus(classId, userId, status)) {
			throw new GlobalException(GlobalException.Type.ALREADY_EXISTS, "Join class request exists");
		}
	}

	/**
	 * Validate
	 */

	public boolean isPending(ClassUserRequest.Status status) {
		return ClassUserRequest.Status.PENDING == status;
	}

}