package com.mezon.classmanagement.backend.common.security.service;

import com.mezon.classmanagement.backend.common.security.dto.PermissionResponseDto;
import com.mezon.classmanagement.backend.common.security.permission.Permission;
import com.mezon.classmanagement.backend.common.util.EnumUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class PermissionService {

	public List<PermissionResponseDto> getPermissionList() {
		return EnumUtils
				.toList(Permission.class).stream()
				.map(item ->
						new PermissionResponseDto(
								(long) item.ordinal() + 1,
								item.name(),
								item.getLabel()
						)
				)
				.toList();
	}

}