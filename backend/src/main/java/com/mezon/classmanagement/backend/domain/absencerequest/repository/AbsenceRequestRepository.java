package com.mezon.classmanagement.backend.domain.absencerequest.repository;

import com.mezon.classmanagement.backend.domain.absencerequest.dto.response.AbsenceRequestResponseDto;
import com.mezon.classmanagement.backend.domain.absencerequest.entity.AbsenceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AbsenceRequestRepository extends JpaRepository<AbsenceRequest, Long> {

    boolean existsByClazz_IdAndCreator_IdAndStatus(Long classId, Long creatorUserId, AbsenceRequest.Status status);

    List<AbsenceRequest> findByClazz_IdAndId(Long classId, Long absenceRequestId);
    List<AbsenceRequest> findByClazz_IdAndCreator_IdAndId(Long classId, Long creatorUserId, Long absenceRequestId);

    @Query(value = """
    select new com.mezon.classmanagement.backend.domain.absencerequest.dto.response.AbsenceRequestResponseDto(
        absenceRequest.id,
        class.id,
        absenceRequest.reason,
        absenceRequest.from,
        absenceRequest.to,
        absenceRequest.proofUrl,
        creator.id,
        creator.displayName,
        creator.avatarUrl,
        absenceRequest.createdAt,
        absenceRequest.status,
        actor.id,
        actor.displayName,
        actor.avatarUrl,
        absenceRequest.actedAt
    )
    from AbsenceRequest absenceRequest
    join absenceRequest.clazz class
    left join absenceRequest.creator creator
    left join absenceRequest.actor actor
    where class.id = :classId
    order by absenceRequest.createdAt desc
    """)
    List<AbsenceRequestResponseDto> getByClazz_IdOrderByCreatedAtDesc(Long classId);

    @Query(value = """
    select new com.mezon.classmanagement.backend.domain.absencerequest.dto.response.AbsenceRequestResponseDto(
        absenceRequest.id,
        class.id,
        absenceRequest.reason,
        absenceRequest.from,
        absenceRequest.to,
        absenceRequest.proofUrl,
        creator.id,
        creator.displayName,
        creator.avatarUrl,
        absenceRequest.createdAt,
        absenceRequest.status,
        actor.id,
        actor.displayName,
        actor.avatarUrl,
        absenceRequest.actedAt
    )
    from AbsenceRequest absenceRequest
    join absenceRequest.clazz class
    left join absenceRequest.creator creator
    left join absenceRequest.actor actor
    where class.id = :classId and creator.id = :creatorUserId
    order by absenceRequest.createdAt desc
    """)
    List<AbsenceRequestResponseDto> getByClazz_IdAndCreator_IdOrderByCreatedAtDesc(Long classId, Long creatorUserId);

}