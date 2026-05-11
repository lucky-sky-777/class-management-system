package com.mezon.classmanagement.backend.domain.attendance.controller;

import com.mezon.classmanagement.backend.domain.attendance.service.AttendanceService;
import com.mezon.classmanagement.backend.domain.seat.dto.ClassSeatResponseDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api/attendance/classes/{classId}")
@RestController
public class AttendanceController {

	AttendanceService attendanceService;

//	@PreAuthorize("@ClassPermission.adminOnly(#classId)")
//	public ClassSeatResponseDto attend(
//			@PathVariable Long classId
//	) {
//		ClassSeatResponseDto response = attendanceService.attend()
//	}

}