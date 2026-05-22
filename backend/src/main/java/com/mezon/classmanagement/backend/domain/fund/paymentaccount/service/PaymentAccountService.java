package com.mezon.classmanagement.backend.domain.fund.paymentaccount.service;

import com.mezon.classmanagement.backend.domain_public.bank.util.BankQrCodeUrlGenerator;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain.clazz.entity.Class;
import com.mezon.classmanagement.backend.domain.fund.paymentaccount.dto.CreateOrUpdatePaymentAccountRequestDto;
import com.mezon.classmanagement.backend.domain.fund.paymentaccount.dto.PaymentAccountResponseDto;
import com.mezon.classmanagement.backend.domain.fund.paymentaccount.entity.PaymentAccount;
import com.mezon.classmanagement.backend.domain.fund.paymentaccount.mapper.PaymentAccountMapper;
import com.mezon.classmanagement.backend.domain.fund.paymentaccount.repository.PaymentAccountRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class PaymentAccountService {

	/**
	 * Repository
	 */

	PaymentAccountRepository paymentAccountRepository;

	/**
	 * Mapper
	 */

	PaymentAccountMapper paymentAccountMapper;

	@Transactional
	public PaymentAccountResponseDto creatorOrUpdate(
			Long classId,
			Long creatorUserId,
			CreateOrUpdatePaymentAccountRequestDto request
	) {
		PaymentAccount paymentAccount = findByClassIdOrNew(classId);

		Class clazz = Class.builder()
				.id(classId)
				.build();
		User creator = User.builder()
				.id(creatorUserId)
				.build();

		paymentAccountMapper.updatePaymentAccountFromRequestDto(request, paymentAccount);
		paymentAccount.setQrCodeUrl(
				BankQrCodeUrlGenerator.generate(
						BankQrCodeUrlGenerator.ImageType.NO_INFO,
						paymentAccount.getBankCode(),
						paymentAccount.getNumber(),
						paymentAccount.getName(),
						null,
						null
				)
		);

		if (paymentAccount.getId() == null) {
			paymentAccount.setClazz(clazz);
		}
		paymentAccount.setCreator(creator);
		paymentAccount.setCreatedAt(Instant.now());

		PaymentAccount responsePaymentAccount = save(paymentAccount);

		return getByClassIdOrNew(responsePaymentAccount.getClazz().getId());
	}

	@Transactional(readOnly = true)
	public PaymentAccountResponseDto getByClass(Long classId) {
		return getByClassIdOrNew(classId);
	}

	/**
	 * Action
	 */

	@Transactional
	public PaymentAccount save(PaymentAccount paymentAccount) {
		return paymentAccountRepository.save(paymentAccount);
	}

	/**
	 * Find
	 */

	@Transactional(readOnly = true)
	public PaymentAccount findByClassIdOrNew(Long classId) {
		return paymentAccountRepository.findByClazz_Id(classId).orElseGet(PaymentAccount::new);
	}

	@Transactional(readOnly = true)
	public PaymentAccountResponseDto getByClassIdOrNew(Long classId) {
		return paymentAccountRepository.getByClazz_Id(classId).orElseGet(PaymentAccountResponseDto::new);
	}

}