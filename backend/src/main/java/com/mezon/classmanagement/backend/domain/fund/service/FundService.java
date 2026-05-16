package com.mezon.classmanagement.backend.domain.fund.service;

import com.mezon.classmanagement.backend.common.api.bank.util.BankQrCodeUrlGenerator;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.security.annotation.RequireClassPermission;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain.clazz.entity.Class;
import com.mezon.classmanagement.backend.domain.fund.dto.CreateFundRequestDto;
import com.mezon.classmanagement.backend.domain.fund.dto.FundIdResponseDto;
import com.mezon.classmanagement.backend.domain.fund.dto.FundResponseDto;
import com.mezon.classmanagement.backend.domain.fund.dto.FundSummaryResponseDto;
import com.mezon.classmanagement.backend.domain.fund.entity.Fund;
import com.mezon.classmanagement.backend.domain.fund.mapper.FundMapper;
import com.mezon.classmanagement.backend.domain.fund.paymentaccount.entity.PaymentAccount;
import com.mezon.classmanagement.backend.domain.fund.paymentaccount.service.PaymentAccountService;
import com.mezon.classmanagement.backend.domain.fund.repository.FundRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class FundService {

	/**
	 * Repository
	 */

	FundRepository fundRepository;

	/**
	 * Mapper
	 */

	FundMapper fundMapper;

	/**
	 * Other services
	 */

	PaymentAccountService paymentAccountService;

	@RequireClassPermission
	@Transactional
	public FundResponseDto create(
			Long classId,
			Long creatorUserId,
			CreateFundRequestDto request
	) {
		if (isExpense(request.getType())) {
			request.setAmount(-request.getAmount());
		}

		throwIfTypeAndAmountNotCompatible(request.getType(), request.getAmount());

		Class clazz = Class.builder()
				.id(classId)
				.build();
		User creator = User.builder()
				.id(creatorUserId)
				.build();

		Fund newFund = fundMapper.toFund(request);
		newFund.setClazz(clazz);
		newFund.setCreator(creator);

		if (newFund.getDescription() != null) {
			newFund.setDescription(newFund.getDescription().replaceAll("[^a-zA-Z0-9]", ""));
		}

		Fund responseFund = save(newFund);

		return fundMapper.toFundResponseDto(responseFund);
	}

	@RequireClassPermission
	@Transactional
	public FundIdResponseDto delete(Long classId, Long fundId) {
		Fund currentFund = findByClassIdAndFundIdOrThrow(classId, fundId);

		delete(currentFund);

		return FundIdResponseDto.builder()
				.fundId(currentFund.getId())
				.build();
	}

	@RequireClassPermission
	@Transactional(readOnly = true)
	public List<FundResponseDto> getByClass(Long classId) {
		List<FundResponseDto> fundResponseDtoList = getByClassId(classId);
		PaymentAccount paymentAccount = paymentAccountService.findByClassIdOrNew(classId);
		fundResponseDtoList.stream()
				.filter(item -> item.getType() == Fund.Type.INCOME)
				.forEach(item ->
						item.setQrCodeUrl(
								BankQrCodeUrlGenerator.generate(
										BankQrCodeUrlGenerator.ImageType.FULL_INFO,
										paymentAccount.getBankCode(),
										paymentAccount.getNumber(),
										paymentAccount.getName(),
										item.getAmount(),
										item.getDescription()
								)
						)
				);
		return fundResponseDtoList;
	}

	@RequireClassPermission
	@Transactional
	public FundSummaryResponseDto getSummaryByClass(Long classId) {
		return getSummaryByClassIdOrThrow(classId);
	}

	/**
	 * Action
	 */

	@Transactional
	public Fund save(Fund fund) {
		return fundRepository.save(fund);
	}

	@Transactional
	public void delete(Fund fund) {
		fundRepository.delete(fund);
	}

	/**
	 * Find
	 */

	@Transactional(readOnly = true)
	public Fund findByClassIdAndFundIdOrThrow(Long classId, Long fundId) {
		return fundRepository
				.findByClazz_IdAndId(classId, fundId)
				.orElseThrow(() ->
						new GlobalException(GlobalException.Type.NOT_FOUND, "Fund not found")
				);
	}

	@Transactional(readOnly = true)
	public List<FundResponseDto> getByClassId(Long classId) {
		return fundRepository
				.getByClazz_IdOrderByCreatedAtDesc(classId);
	}

	@Transactional(readOnly = true)
	public FundSummaryResponseDto getSummaryByClassIdOrThrow(Long classId) {
		return fundRepository
				.getSummaryByClassId(classId)
				.orElseThrow(() ->
						new GlobalException(GlobalException.Type.INTERNAL_SERVER_ERROR, "Internal server error")
				);
	}

	/**
	 * Validate
	 */

	public void throwIfTypeAndAmountNotCompatible(Fund.Type type, Long amount) {
		boolean isValidIncome = isIncome(type) && amount > 0;
		boolean isValidExpense = isExpense(type) && amount < 0;

		if (isValidIncome || isValidExpense) {
			return;
		}

		throw new GlobalException(GlobalException.Type.INVALID_REQUEST, "Invalid request");
	}

	public boolean isIncome(Fund.Type type) {
		return Fund.Type.INCOME == type;
	}

	public boolean isExpense(Fund.Type type) {
		return Fund.Type.EXPENSE == type;
	}

}