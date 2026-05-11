package com.mezon.classmanagement.backend.domain.point.mapper;

import com.mezon.classmanagement.backend.domain.point.dto.CreatePointRequestDto;
import com.mezon.classmanagement.backend.domain.point.dto.PointResponseDto;
import com.mezon.classmanagement.backend.domain.point.entity.Point;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PointMapper {
	Point toPoint(CreatePointRequestDto createPointRequestDto);

	@Mapping(source = "clazz.id", target = "classId")
	@Mapping(source = "actor.id", target = "actorUserId")
	PointResponseDto toPointResponseDto(Point point);
}