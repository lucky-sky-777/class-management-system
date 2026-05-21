package com.mezon.classmanagement.backend.common.security.service;

import com.mezon.classmanagement.backend.common.security.annotation.RequireClassSecurity;
import com.mezon.classmanagement.backend.common.security.dto.ClassRoleAndPermissionListResponseDto;
import com.mezon.classmanagement.backend.common.security.dto.ClassRoleOrPermissionResponseDto;
import com.mezon.classmanagement.backend.common.security.permission.ClassPermission;
import com.mezon.classmanagement.backend.common.security.permission.ClassRole;
import com.mezon.classmanagement.backend.common.security.permission.ClassRoleOrPermission;
import com.mezon.classmanagement.backend.common.util.EnumUtils;
import com.mezon.classmanagement.backend.domain.classuser.dto.ClassUserResponseDto;
import com.mezon.classmanagement.backend.domain.classuser.dto.UpdateClassUserRoleAndPermissionRequestDto;
import com.mezon.classmanagement.backend.domain.classuser.service.ClassUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class ClassRoleAndPermissionService {

	ClassUserService classUserService;

	@RequireClassSecurity
	@Transactional
	public ClassUserResponseDto setClassUserRoleAndPermission(Long classId, Long userId, UpdateClassUserRoleAndPermissionRequestDto request) {
		return classUserService.updateClassUserRoleAndPermission(classId, userId, request);
	}

	@RequireClassSecurity
	public ClassRoleAndPermissionListResponseDto getClassRoleAndPermission() {
		return ClassRoleAndPermissionListResponseDto.builder()
				.roles(getClassRoleOrPermissionList(ClassRole.class))
				.permissions(getClassRoleOrPermissionList(ClassPermission.class))
				.build();
	}

	private <T extends Enum<T> & ClassRoleOrPermission> List<ClassRoleOrPermissionResponseDto> getClassRoleOrPermissionList(Class<T> classRoleOrPermissionClass) {
		return EnumUtils
				.toList(classRoleOrPermissionClass).stream()
				.map(item ->
						new ClassRoleOrPermissionResponseDto(
								(long) item.ordinal() + 1,
								item.name(),
								item.getLabel()
						)
				)
				.toList();
	}

}