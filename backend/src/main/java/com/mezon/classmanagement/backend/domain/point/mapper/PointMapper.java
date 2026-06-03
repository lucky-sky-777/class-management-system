package com.mezon.classmanagement.backend.domain.point.mapper;

import com.mezon.classmanagement.backend.config.MapStructConfig;
import com.mezon.classmanagement.backend.domain.point.dto.CreatePointRequestDto;
import com.mezon.classmanagement.backend.domain.point.dto.PointResponseDto;
import com.mezon.classmanagement.backend.domain.point.entity.Point;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface PointMapper {
	Point toPoint(CreatePointRequestDto createPointRequestDto);

	@Mapping(source = "clazz.id", target = "classId")
	@Mapping(source = "actor.id", target = "actorUserId")
	@Mapping(source = "group.id", target = "groupId")
	PointResponseDto toPointResponseDto(Point point);
}