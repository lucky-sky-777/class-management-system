package com.mezon.classmanagement.backend.domain.fund.paymentaccount.repository;

import com.mezon.classmanagement.backend.domain.fund.paymentaccount.dto.PaymentAccountResponseDto;
import com.mezon.classmanagement.backend.domain.fund.paymentaccount.entity.PaymentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentAccountRepository extends JpaRepository<PaymentAccount, Long> {
	@Query(value = """
	select new com.mezon.classmanagement.backend.domain.fund.paymentaccount.dto.PaymentAccountResponseDto(
		paymentAccount.id,
		class.id,
		paymentAccount.bankCode,
		paymentAccount.number,
		paymentAccount.name,
		paymentAccount.qrCodeUrl,
		creator.id,
		creator.displayName,
		creator.avatarUrl,
		paymentAccount.createdAt
	)
	from PaymentAccount paymentAccount
	join paymentAccount.clazz class
	join paymentAccount.creator creator
	where class.id = :classId
	""")
	Optional<PaymentAccountResponseDto> getByClazz_Id(Long classId);
	Optional<PaymentAccount> findByClazz_Id(Long classId);
}