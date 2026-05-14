package com.mezon.classmanagement.backend.domain.fund.fundpayment.repository;

import com.mezon.classmanagement.backend.domain.fund.fundpayment.entity.FundPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FundPaymentRepository extends JpaRepository<FundPayment, Long> {
	boolean existsByClazz_IdAndFund_IdAndCreator_IdAndStatus(
			Long classId,
			Long fundId,
			Long creatorUserId,
			FundPayment.Status status
	);

	List<FundPayment> findByClazz_IdAndId(Long classId, Long fundPaymentId);
}