package com.mezon.classmanagement.backend.domain.attendance.service;

import com.mezon.classmanagement.backend.domain.groupuser.entity.GroupUser;
import com.mezon.classmanagement.backend.domain.groupuser.service.GroupUserService;
import com.mezon.classmanagement.backend.domain.seat.dto.ClassSeatResponseDto;
import com.mezon.classmanagement.backend.domain.seat.service.SeatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class AttendanceService {

	/**
	 * Service
	 */

	GroupUserService groupUserService;
	SeatService seatService;

	@Transactional
	public ClassSeatResponseDto attend(Long classId, Long groupId, Long userId, GroupUser.AttendanceStatus attendanceStatus) {
		GroupUser currentGroupUser = groupUserService.findByClassIdAndGroupIdAndUserIdOrThrow(classId, groupId, userId);

		currentGroupUser.setAttendanceStatus(attendanceStatus);

		groupUserService.save(currentGroupUser);

		return seatService.getClassSeats(classId);
	}

}