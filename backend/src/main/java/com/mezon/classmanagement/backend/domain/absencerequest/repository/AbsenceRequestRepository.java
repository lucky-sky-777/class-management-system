package com.mezon.classmanagement.backend.domain.absencerequest.repository;

import com.mezon.classmanagement.backend.domain.absencerequest.dto.response.AbsenceRequestResponseDto;
import com.mezon.classmanagement.backend.domain.absencerequest.entity.AbsenceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AbsenceRequestRepository extends JpaRepository<AbsenceRequest, Long> {

    boolean existsByClazz_IdAndUser_IdAndStatus(Long classId, Long userId, AbsenceRequest.Status status);

    List<AbsenceRequest> findByClazz_IdAndId(Long classId, Long absenceRequestId);
    List<AbsenceRequest> findByClazz_IdAndUser_IdAndId(Long classId, Long userId, Long absenceRequestId);

    @Query(value = """
    select new com.mezon.classmanagement.backend.domain.absencerequest.dto.response.AbsenceRequestResponseDto(
        absenceRequest.id,
        class.id,
        user.id,
        user.displayName,
        user.avatarUrl,
        absenceRequest.reason,
        absenceRequest.from,
        absenceRequest.to,
        absenceRequest.proofUrl,
        absenceRequest.status,
        absenceRequest.createdAt
    )
    from AbsenceRequest absenceRequest
    join absenceRequest.clazz class
    join absenceRequest.user user
    where class.id = :classId
    order by absenceRequest.createdAt desc
    """)
    List<AbsenceRequestResponseDto> getByClazz_IdOrderByCreatedAtDesc(Long classId);
    @Query(value = """
    select new com.mezon.classmanagement.backend.domain.absencerequest.dto.response.AbsenceRequestResponseDto(
        absenceRequest.id,
        class.id,
        user.id,
        user.displayName,
        user.avatarUrl,
        absenceRequest.reason,
        absenceRequest.from,
        absenceRequest.to,
        absenceRequest.proofUrl,
        absenceRequest.status,
        absenceRequest.createdAt
    )
    from AbsenceRequest absenceRequest
    join absenceRequest.clazz class
    join absenceRequest.user user
    where class.id = :classId and user.id = :userId
    order by absenceRequest.createdAt desc
    """)
    List<AbsenceRequestResponseDto> getByClazz_IdAndUser_IdOrderByCreatedAtDesc(Long classId, Long userId);

}