package com.mezon.classmanagement.backend.domain.fund.fundpayment.repository;

import com.mezon.classmanagement.backend.domain.fund.fundpayment.dto.FundPaymentResponseDto;
import com.mezon.classmanagement.backend.domain.fund.fundpayment.entity.FundPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

	@Query(value = """
	select new com.mezon.classmanagement.backend.domain.fund.fundpayment.dto.FundPaymentResponseDto(
		fundPayment.id,
		class.id,
		fund.id,
		fundPayment.proofUrl,
		fundPayment.createdAt,
		creator.id,
		creator.displayName,
		creator.avatarUrl,
		fundPayment.status,
		actor.id,
		actor.displayName,
		actor.avatarUrl
	)
	from FundPayment fundPayment
	join fundPayment.clazz class
	join fundPayment.fund fund
	join fundPayment.creator creator
	left join fundPayment.actor actor
	where class.id = :classId and fund.id = :fundId
	""")
	List<FundPaymentResponseDto> getByClazz_IdAndFund_Id(Long classId, Long fundId);

	List<FundPayment> findByClazz_IdAndFund_IdAndId(Long classId, Long fundId, Long fundPaymentId);
	List<FundPayment> findByClazz_IdAndFund_IdAndCreator_IdAndId(Long classId, Long fundId, Long creatorUserId, Long fundPaymentId);
}