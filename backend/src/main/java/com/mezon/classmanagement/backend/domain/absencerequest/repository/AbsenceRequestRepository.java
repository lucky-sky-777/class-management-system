package com.mezon.classmanagement.backend.domain.absencerequest.repository;

import com.mezon.classmanagement.backend.domain.absencerequest.entity.AbsenceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AbsenceRequestRepository extends JpaRepository<AbsenceRequest, Long> {
    boolean existsByClazz_IdAndUser_IdAndStatus(Long classId, Long userId, AbsenceRequest.Status status);

    List<AbsenceRequest> findByUser_Id(Long userId);
    List<AbsenceRequest> findByClazz_Id(Long classId);

    List<AbsenceRequest> findByClazz_IdAndId(Long classId, Long absenceRequestId);
    List<AbsenceRequest> findByClazz_IdAndUser_IdAndId(Long classId, Long userId, Long absenceRequestId);

    List<AbsenceRequest> findByClazz_IdOrderByCreatedAtDesc(Long classId);
    List<AbsenceRequest> findByUser_IdOrderByCreatedAtDesc(Long userId);
}