package com.mezon.classmanagement.backend.domain.point.service;

import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.security.annotation.RequireClassSecurity;
import com.mezon.classmanagement.backend.common.util.DateTimeUtils;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain.clazz.entity.Class;
import com.mezon.classmanagement.backend.domain.group.entity.Group;
import com.mezon.classmanagement.backend.domain.point.dto.CreatePointRequestDto;
import com.mezon.classmanagement.backend.domain.point.dto.GetPointRequestDto;
import com.mezon.classmanagement.backend.domain.point.dto.MonthPointRankingResponseDto;
import com.mezon.classmanagement.backend.domain.point.dto.PointIdResponseDto;
import com.mezon.classmanagement.backend.domain.point.dto.PointResponseDto;
import com.mezon.classmanagement.backend.domain.point.dto.WeekPointRankingResponseDto;
import com.mezon.classmanagement.backend.domain.point.entity.Point;
import com.mezon.classmanagement.backend.domain.point.mapper.PointMapper;
import com.mezon.classmanagement.backend.domain.point.repository.PointRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class PointService {

	/**
	 * Repository
	 */

	PointRepository pointRepository;

	/**
	 * Mapper
	 */

	PointMapper pointMapper;

	@RequireClassSecurity
	@Transactional
	public PointResponseDto create(
			Long classId,
			Long groupId,
			Long actorUserId,
			CreatePointRequestDto request
	) {
		Class clazz = Class.builder()
				.id(classId)
				.build();
		Group group = Group.builder()
				.id(groupId)
				.build();
		User actor = User.builder()
				.id(actorUserId)
				.build();

		Point newPoint = pointMapper.toPoint(request);
		newPoint.setClazz(clazz);
		newPoint.setGroup(group);
		newPoint.setActor(actor);

		Point responsePoint = save(newPoint);

		return pointMapper.toPointResponseDto(responsePoint);
	}

	@RequireClassSecurity
	@Transactional
	public PointIdResponseDto delete(
			Long classId,
			Long pointId
	) {
		Point currentPoint = findByClassIdAndPointIdOrThrow(classId, pointId);

		delete(currentPoint);

		return PointIdResponseDto.builder()
				.pointId(currentPoint.getId())
				.build();
	}

	@RequireClassSecurity
	@Transactional(readOnly = true)
	public List<PointResponseDto> getByClass(
			Long classId,
			GetPointRequestDto request
	) {
		if (request == null) {
			return getByClassId(
					classId, DateTimeUtils.getCurrentWeekStartAt(), DateTimeUtils.getCurrentWeekEndAt()
			);
		}

		return getByClassId(classId, request.getStartAt(), request.getEndAt());
	}

	@RequireClassSecurity
	@Transactional(readOnly = true)
	public List<PointResponseDto> getByGroup(
			Long classId,
			Long groupId,
			GetPointRequestDto request
	) {
		if (request == null) {
			return getByClassIdAndGroupId(
					classId, groupId, DateTimeUtils.getCurrentWeekStartAt(), DateTimeUtils.getCurrentWeekEndAt()
			);
		}

		return getByClassIdAndGroupId(classId, groupId, request.getStartAt(), request.getEndAt());
	}

	@RequireClassSecurity
	@Transactional(readOnly = true)
	public List<WeekPointRankingResponseDto> getWeekRanking(
			Long classId,
			GetPointRequestDto request
	) {
		List<WeekPointRankingResponseDto> responseList;

		if (request == null) {
			responseList = pointRepository.getWeekRankingAllGroupByClass(
					classId,
					DateTimeUtils.getCurrentWeekStartAt(),
					Instant.now()
			);
		} else {
			responseList = pointRepository.getWeekRankingAllGroupByClass(
					classId,
					request.getStartAt(),
					request.getEndAt()
			);
		}

		short rank = 1;

		for (WeekPointRankingResponseDto response : responseList) {
			response.setRank(rank++);
		}

		return responseList;
	}

	@RequireClassSecurity
	@Transactional(readOnly = true)
	public List<MonthPointRankingResponseDto> getMonthRanking(
			Long classId,
			GetPointRequestDto request
	) {
		List<MonthPointRankingResponseDto> monthPointRankingList;

		if (request == null) {
			monthPointRankingList = pointRepository.getMonthRankingByClass(
					classId,

					DateTimeUtils.getWeekStartAtBefore(3),
					DateTimeUtils.getWeekEndAtBefore(3),

					DateTimeUtils.getWeekStartAtBefore(2),
					DateTimeUtils.getWeekEndAtBefore(2),

					DateTimeUtils.getWeekStartAtBefore(1),
					DateTimeUtils.getWeekEndAtBefore(1),

					DateTimeUtils.getWeekStartAtBefore(0),
					DateTimeUtils.getWeekEndAtBefore(0)
			);
		} else {
			if (request.getStartAt() == null) {
				request.setStartAt(DateTimeUtils.getCurrentWeekStartAt());
			}
			if (request.getEndAt() == null) {
				request.setEndAt(DateTimeUtils.getCurrentWeekEndAt());
			}

			monthPointRankingList = pointRepository.getMonthRankingByClass(
					classId,

					DateTimeUtils.getWeekStartAtBefore(request.getStartAt(), 3),
					DateTimeUtils.getWeekEndAtBefore(request.getEndAt(), 3),

					DateTimeUtils.getWeekStartAtBefore(request.getStartAt(), 2),
					DateTimeUtils.getWeekEndAtBefore(request.getEndAt(), 2),

					DateTimeUtils.getWeekStartAtBefore(request.getStartAt(), 1),
					DateTimeUtils.getWeekEndAtBefore(request.getEndAt(), 1),

					DateTimeUtils.getWeekStartAtBefore(request.getStartAt(), 0),
					DateTimeUtils.getWeekEndAtBefore(request.getEndAt(), 0)
			);
		}

		short rank = 1;

		for (MonthPointRankingResponseDto monthPointRanking : monthPointRankingList) {
			monthPointRanking.setRank(rank++);
		}

		return monthPointRankingList;
	}

	/**
	 * Action
	 */

	@Transactional
	public Point save(Point point) {
		return pointRepository.save(point);
	}

	@Transactional
	public void delete(Point point) {
		pointRepository.delete(point);
	}

	/**
	 * Find
	 */

	@Transactional(readOnly = true)
	public Point findByClassIdAndPointIdOrThrow(
			Long classId,
			Long pointId
	) {
		return pointRepository
				.findByClazz_IdAndId(classId, pointId)
				.orElseThrow(() ->
						new GlobalException(GlobalException.Type.NOT_FOUND, "Point not found")
				);
	}

	@Transactional(readOnly = true)
	public List<PointResponseDto> getByClassId(
			Long classId,
			Instant startAt,
			Instant endAt
	) {
		return pointRepository
				.getByClazz_IdOrderByGroup_IdAscCreatedAtDescFilterByStartAtAndEndAt(
						classId, startAt, endAt
				);
	}

	@Transactional(readOnly = true)
	public List<PointResponseDto> getByClassIdAndGroupId(
			Long classId,
			Long groupId,
			Instant startAt,
			Instant endAt
	) {
		return pointRepository
				.getByClazz_IdAndGroup_IdOrderByCreatedAtDescFilterByStartAtAndEndAt(
						classId, groupId, startAt, endAt
				);
	}

}