package com.mezon.classmanagement.backend.domain.attendance.controller;

import com.mezon.classmanagement.backend.common.dto.ResponseDTO;
import com.mezon.classmanagement.backend.domain.attendance.dto.AttendanceRequestDto;
import com.mezon.classmanagement.backend.domain.attendance.service.AttendanceService;
import com.mezon.classmanagement.backend.domain.seat.dto.ClassSeatResponseDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/attendances/classes/{classId}/groups/{groupId}")
@RestController
public class AttendanceController {

	AttendanceService attendanceService;

	@PreAuthorize("@ClassPermission.adminOnly(#classId)")
	@PostMapping
	public ResponseDTO<ClassSeatResponseDto> attend(
			@PathVariable Long classId,
			@PathVariable Long groupId,
			@RequestBody AttendanceRequestDto request

			) {
		ClassSeatResponseDto response = attendanceService.attend(classId, groupId, request);

		return ResponseDTO.<ClassSeatResponseDto>builder()
				.success(true)
				.message("Attend successful")
				.data(response)
				.build();
	}

}