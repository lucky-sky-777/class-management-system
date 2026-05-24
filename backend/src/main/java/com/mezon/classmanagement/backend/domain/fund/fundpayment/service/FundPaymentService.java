package com.mezon.classmanagement.backend.domain.fund.fundpayment.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.security.annotation.RequireClassSecurity;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain.clazz.entity.Class;
import com.mezon.classmanagement.backend.domain.fund.entity.Fund;
import com.mezon.classmanagement.backend.domain.fund.fundpayment.dto.CreateFundPaymentRequestDto;
import com.mezon.classmanagement.backend.domain.fund.fundpayment.dto.FundPaymentIdResponseDto;
import com.mezon.classmanagement.backend.domain.fund.fundpayment.dto.FundPaymentResponseDto;
import com.mezon.classmanagement.backend.domain.fund.fundpayment.entity.FundPayment;
import com.mezon.classmanagement.backend.domain.fund.fundpayment.mapper.FundPaymentMapper;
import com.mezon.classmanagement.backend.domain.fund.fundpayment.repository.FundPaymentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class FundPaymentService {

	/**
	 * Repository
	 */

	FundPaymentRepository fundPaymentRepository;

	/**
	 * Mapper
	 */

	FundPaymentMapper fundPaymentMapper;

	@RequireClassSecurity
	@Transactional
	public FundPaymentResponseDto create(
			Long classId,
			Long fundId,
			Long creatorUserId,
			CreateFundPaymentRequestDto request
	) {
		throwIfExistsByClassIdAndFundIdAndCreatorUserIdAndStatus(
				classId,
				fundId,
				creatorUserId,
				FundPayment.Status.PENDING
		);

		Class clazz = Class.builder()
				.id(classId)
				.build();
		Fund fund = Fund.builder()
				.id(fundId)
				.build();
		User creator = User.builder()
				.id(creatorUserId)
				.build();

		FundPayment newFundPayment = fundPaymentMapper.toFundPayment(request);
		newFundPayment.setClazz(clazz);
		newFundPayment.setFund(fund);
		newFundPayment.setCreator(creator);

		FundPayment responseFundPayment = save(newFundPayment);

		return fundPaymentMapper.toFundPaymentResponseDto(responseFundPayment);
	}

	@RequireClassSecurity
	@Transactional
	public FundPaymentIdResponseDto approve(
			Long classId,
			Long fundId,
			Long actorUserId,
			Long fundPaymentId
	) {
		FundPayment currentFundPayment = findByClassIdAndFundIdAndFundPaymentIdOrThrow(classId, fundId, fundPaymentId);

		User actor = User.builder()
				.id(actorUserId)
				.build();

		currentFundPayment.setStatus(FundPayment.Status.APPROVED);
		currentFundPayment.setActor(actor);

		FundPayment responseFundPayment = save(currentFundPayment);

		return FundPaymentIdResponseDto.builder()
				.fundPaymentId(responseFundPayment.getId())
				.build();
	}

	@RequireClassSecurity
	@Transactional
	public FundPaymentIdResponseDto reject(
			Long classId,
			Long fundId,
			Long actorUserId,
			Long fundPaymentId
	) {
		FundPayment currentFundPayment = findByClassIdAndFundIdAndFundPaymentIdOrThrow(classId, fundId, fundPaymentId);

		User actor = User.builder()
				.id(actorUserId)
				.build();

		currentFundPayment.setStatus(FundPayment.Status.REJECTED);
		currentFundPayment.setActor(actor);

		FundPayment responseFundPayment = save(currentFundPayment);

		return FundPaymentIdResponseDto.builder()
				.fundPaymentId(responseFundPayment.getId())
				.build();
	}

	@RequireClassSecurity
	@Transactional
	public FundPaymentIdResponseDto cancel(
			Long classId,
			Long fundId,
			Long creatorUserId,
			Long fundPaymentId
	) {
		FundPayment currentFundPayment = findByClassIdAndFundIdAndCreatorUserIdAndFundPaymentIdOrThrow(classId, fundId, creatorUserId, fundPaymentId);

		User creator = User.builder()
				.id(creatorUserId)
				.build();

		currentFundPayment.setStatus(FundPayment.Status.CANCELLED);
		currentFundPayment.setActor(creator);

		FundPayment responseFundPayment = save(currentFundPayment);

		return FundPaymentIdResponseDto.builder()
				.fundPaymentId(responseFundPayment.getId())
				.build();
	}

	@RequireClassSecurity
	@Transactional(readOnly = true)
	public List<FundPaymentResponseDto> getByClassAndFund(Long classId, Long fundId) {
		return getByClassIdAndFundId(classId, fundId);
	}

	/**
	 * Action
	 */

	@Transactional
	public FundPayment save(FundPayment fundPayment) {
		return fundPaymentRepository.save(fundPayment);
	}

	@Transactional
	public void delete(FundPayment fundPayment) {
		fundPaymentRepository.delete(fundPayment);
	}

	/**
	 * Find
	 */

	@Transactional(readOnly = true)
	public FundPayment findByClassIdAndFundIdAndFundPaymentIdOrThrow(
			Long classId,
			Long fundId,
			Long fundPaymentId
	) {
		List<FundPayment> fundPaymentList = fundPaymentRepository
				.findByClazz_IdAndFund_IdAndId(classId, fundId, fundPaymentId);
		throwIfEmptyList(fundPaymentList);

		FundPayment fundPayment = fundPaymentList.getFirst();
		throwIfNotPending(fundPayment);

		return fundPayment;
	}

	@Transactional(readOnly = true)
	public FundPayment findByClassIdAndFundIdAndCreatorUserIdAndFundPaymentIdOrThrow(
			Long classId,
			Long fundId,
			Long creatorUserId,
			Long fundPaymentId
	) {
		List<FundPayment> fundPaymentList = fundPaymentRepository
				.findByClazz_IdAndFund_IdAndCreator_IdAndId(classId, fundId, creatorUserId, fundPaymentId);
		throwIfEmptyList(fundPaymentList);

		FundPayment fundPayment = fundPaymentList.getFirst();
		throwIfNotPending(fundPayment);

		return fundPayment;
	}

	@Transactional(readOnly = true)
	public List<FundPaymentResponseDto> getByClassIdAndFundId(Long classId, Long fundId) {
		return fundPaymentRepository.getByClazz_IdAndFund_Id(classId, fundId);
	}

	private void throwIfEmptyList(List<FundPayment> fundPaymentList) {
		if (fundPaymentList.isEmpty()) {
			throw new GlobalException(GlobalException.Type.NOT_FOUND, "Fund payment not found");
		}
	}

	private void throwIfNotPending(FundPayment fundPayment) {
		if (!isPending(fundPayment.getStatus())) {
			throw new GlobalException(GlobalException.Type.INVALID_REQUEST, "Not a pending request");
		}
	}

	/**
	 * Exists
	 */

	@Transactional(readOnly = true)
	public boolean existsByClassIdAndFundIdAndCreatorUserIdAndStatus(
			Long classId,
			Long fundId,
			Long creatorUserId,
			FundPayment.Status status
	) {
		return fundPaymentRepository.existsByClazz_IdAndFund_IdAndCreator_IdAndStatus(
				classId,
				fundId,
				creatorUserId,
				status
		);
	}

	@Transactional(readOnly = true)
	public void throwIfExistsByClassIdAndFundIdAndCreatorUserIdAndStatus(
			Long classId,
			Long fundId,
			Long creatorUserId,
			FundPayment.Status status
	) {
		if (existsByClassIdAndFundIdAndCreatorUserIdAndStatus(
				classId,
				fundId,
				creatorUserId,
				status
		)) {
			throw new GlobalException(GlobalException.Type.ALREADY_EXISTS, "Fund payment exists");
		}
	}

	/**
	 * Validate
	 */

	public boolean isPending(FundPayment.Status status) {
		return FundPayment.Status.PENDING == status;
	}

}