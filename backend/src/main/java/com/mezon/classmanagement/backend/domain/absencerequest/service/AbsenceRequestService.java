package com.mezon.classmanagement.backend.domain.absencerequest.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.security.annotation.RequireClassPermission;
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

    @RequireClassPermission
    @Transactional
    public AbsenceRequestResponseDto create(
            Long classId,
            Long userId,
            CreateAbsenceRequestRequestDto request
    ) {
        throwIfExistsByClassIdAndUserIdAndStatus(classId, userId, AbsenceRequest.Status.PENDING);

        Class clazz = Class.create(classId);
        User user = User.create(userId);

        AbsenceRequest newAbsenceRequest = absenceRequestMapper.toAbsenceRequest(request);
        newAbsenceRequest.setClazz(clazz);
        newAbsenceRequest.setUser(user);

        AbsenceRequest responseAbsenceRequest = save(newAbsenceRequest);

        return absenceRequestMapper.toAbsenceRequestResponseDto(responseAbsenceRequest);
    }

    @RequireClassPermission
    @Transactional
    public AbsenceRequestIdResponseDto approve(
            Long classId,
            Long absenceRequestId
    ) {
        AbsenceRequest currentAbsenceRequest = findByClassIdAndAbsenceRequestIdOrThrow(classId, absenceRequestId);

        currentAbsenceRequest.setStatus(AbsenceRequest.Status.APPROVED);

        AbsenceRequest responseAbsenceRequest = save(currentAbsenceRequest);

        return new AbsenceRequestIdResponseDto(
                responseAbsenceRequest.getId()
        );
    }

    @RequireClassPermission
    @Transactional
    public AbsenceRequestIdResponseDto reject(
            Long classId,
            Long absenceRequestId
    ) {
        AbsenceRequest currentAbsenceRequest = findByClassIdAndAbsenceRequestIdOrThrow(classId, absenceRequestId);

        currentAbsenceRequest.setStatus(AbsenceRequest.Status.REJECTED);

        AbsenceRequest responseAbsenceRequest = save(currentAbsenceRequest);

        return new AbsenceRequestIdResponseDto(
                responseAbsenceRequest.getId()
        );
    }

    @RequireClassPermission
    @Transactional
    public AbsenceRequestIdResponseDto cancel(
            Long classId,
            Long userId,
            Long absenceRequestId
    ) {
        AbsenceRequest currentAbsenceRequest = findByClassIdAndUserIdAndAbsenceRequestIdOrThrow(classId, userId, absenceRequestId);

        currentAbsenceRequest.setStatus(AbsenceRequest.Status.CANCELLED);

        AbsenceRequest responseAbsenceRequest = save(currentAbsenceRequest);

        return new AbsenceRequestIdResponseDto(
                responseAbsenceRequest.getId()
        );
    }

    @RequireClassPermission
    @Transactional(readOnly = true)
    public List<AbsenceRequestResponseDto> getByClass(
            Long classId
    ) {
        return getByClassId(classId);
    }

    @RequireClassPermission
    @Transactional(readOnly = true)
    public List<AbsenceRequestResponseDto> getByClassAndUser(
            Long classId,
            Long userId
    ) {
        return getByClassIdAndUserId(classId, userId);
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
    public List<AbsenceRequestResponseDto> getByClassId(Long classId) {
        return absenceRequestRepository
                .getByClazz_IdOrderByCreatedAtDesc(classId);
    }

    @Transactional(readOnly = true)
    public List<AbsenceRequestResponseDto> getByClassIdAndUserId(Long classId, Long userId) {
        return absenceRequestRepository
                .getByClazz_IdAndUser_IdOrderByCreatedAtDesc(classId, userId);
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
    public AbsenceRequest findByClassIdAndUserIdAndAbsenceRequestIdOrThrow(
            Long classId,
            Long userId,
            Long absenceRequestId
    ) {
        List<AbsenceRequest> absenceRequestList = absenceRequestRepository
                .findByClazz_IdAndUser_IdAndId(classId, userId, absenceRequestId);
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
    public boolean existsByClassIdAndUserIdAndStatus(Long classId, Long userId, AbsenceRequest.Status status) {
        return absenceRequestRepository.existsByClazz_IdAndUser_IdAndStatus(classId, userId, status);
    }

    @Transactional(readOnly = true)
    public void throwIfExistsByClassIdAndUserIdAndStatus(Long classId, Long userId, AbsenceRequest.Status status) {
        if (existsByClassIdAndUserIdAndStatus(classId, userId, status)) {
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