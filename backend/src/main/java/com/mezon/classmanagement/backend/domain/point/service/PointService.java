package com.mezon.classmanagement.backend.domain.point.service;

import com.mezon.classmanagement.backend.common.api.week.service.WeekService;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.security.annotation.RequireClassPermission;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain.clazz.entity.Class;
import com.mezon.classmanagement.backend.domain.group.entity.Group;
import com.mezon.classmanagement.backend.domain.point.dto.CreatePointRequestDto;
import com.mezon.classmanagement.backend.domain.point.dto.GetPointRequestDto;
import com.mezon.classmanagement.backend.domain.point.dto.PointIdResponseDto;
import com.mezon.classmanagement.backend.domain.point.dto.PointResponseDto;
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

	/**
	 * Other services
	 */

	WeekService weekService;

	@RequireClassPermission
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

	@RequireClassPermission
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

	@RequireClassPermission
	@Transactional(readOnly = true)
	public List<PointResponseDto> getByGroup(Long classId, Long groupId, GetPointRequestDto request) {
		if (request == null) {
			return getByClassIdAndGroupId(
					classId, groupId, weekService.getCurrentWeekStartAt(), weekService.getCurrentWeekEndAt()
			);
		}

		return getByClassIdAndGroupId(classId, groupId, request.getStartAt(), request.getEndAt());
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
	public Point findByClassIdAndPointIdOrThrow(Long classId, Long pointId) {
		return pointRepository
				.findByClazz_IdAndId(classId, pointId)
				.orElseThrow(() ->
						new GlobalException(GlobalException.Type.NOT_FOUND, "Point not found")
				);
	}

	@Transactional(readOnly = true)
	public List<Point> findByClassIdAndGroupId(Long classId, Long groupId) {
		return pointRepository
				.findByClazz_IdAndGroup_IdOrderByCreatedAtDesc(classId, groupId);
	}

	@Transactional(readOnly = true)
	public List<PointResponseDto> getByClassIdAndGroupId(Long classId, Long groupId) {
		return pointRepository
				.getByClazz_IdAndGroup_IdOrderByCreatedAtDesc(classId, groupId);
	}

	@Transactional(readOnly = true)
	public List<PointResponseDto> getByClassIdAndGroupId(Long classId, Long groupId, Instant startAt, Instant endAt) {
		return pointRepository
				.getByClazz_IdAndGroup_IdOrderByCreatedAtDescFilterByStartAtAndEndAt(
						classId, groupId, startAt, endAt
				);
	}

}