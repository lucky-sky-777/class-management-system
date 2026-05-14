package com.mezon.classmanagement.backend.domain.fund.repository;

import com.mezon.classmanagement.backend.domain.fund.dto.FundResponseDto;
import com.mezon.classmanagement.backend.domain.fund.dto.FundSummaryResponseDto;
import com.mezon.classmanagement.backend.domain.fund.entity.Fund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FundRepository extends JpaRepository<Fund, Long> {
	@Query(value = """
	select new com.mezon.classmanagement.backend.domain.fund.dto.FundResponseDto(
		fund.id,
		class.id,
		fund.type,
		fund.amount,
		fund.title,
		fund.description,
		fund.createdAt,
		creator.id
	)
	from Fund fund
	join fund.clazz class
	join fund.creator creator
	where class.id = :classId
	order by fund.createdAt desc
	""")
	List<FundResponseDto> getByClazz_IdOrderByCreatedAtDesc(Long classId);

	@Query("""
	SELECT new com.mezon.classmanagement.backend.domain.fund.dto.FundSummaryResponseDto(
		CAST(
			COALESCE(SUM(
				CASE
					WHEN fund.type = com.mezon.classmanagement.backend.domain.fund.entity.Fund.Type.INCOME
					THEN fund.amount
					ELSE -fund.amount
				END
			), 0)
			AS long
		),

		CAST(
			COALESCE(SUM(
				CASE
					WHEN fund.type = com.mezon.classmanagement.backend.domain.fund.entity.Fund.Type.INCOME
					THEN fund.amount
					ELSE 0
				END
			), 0)
			AS long
		),

		CAST(
			COALESCE(SUM(
				CASE
					WHEN fund.type = com.mezon.classmanagement.backend.domain.fund.entity.Fund.Type.EXPENSE
					THEN fund.amount
					ELSE 0
				END
			), 0)
			AS long
		)
	)
	FROM Fund fund
	WHERE fund.clazz.id = :classId
	""")
	Optional<FundSummaryResponseDto> getSummaryByClassId(Long classId);

	Optional<Fund> findByClazz_IdAndId(Long classId, Long fundId);
}