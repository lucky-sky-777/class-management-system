package com.mezon.classmanagement.backend.domain.seat.service;

import com.mezon.classmanagement.backend.domain.groupuser.dto.response.GroupUserResponseDto;
import com.mezon.classmanagement.backend.domain.groupuser.entity.GroupUser;
import com.mezon.classmanagement.backend.domain.groupuser.service.GroupUserService;
import com.mezon.classmanagement.backend.domain.seat.dto.ClassSeatResponseDto;
import com.mezon.classmanagement.backend.domain.seat.dto.DeskPositionSeatResponseDto;
import com.mezon.classmanagement.backend.domain.seat.dto.DeskSeatResponseDto;
import com.mezon.classmanagement.backend.domain.seat.dto.GroupSeatResponseDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class SeatService {

	/**
	 * Service
	 */

	GroupUserService groupUserService;

	@Transactional(readOnly = true)
	public ClassSeatResponseDto getClassSeats(Long classId) {
		List<GroupUserResponseDto> groupUserList = groupUserService.findByClassId(classId);

		// from ClassSeat
		List<LinkedHashMap<Long, GroupSeatResponseDto>> groups = new ArrayList<>();

		Long currentGroupId = null;
		Short currentDesk = null;

		// from GroupSeat
		List<LinkedHashMap<Short, DeskSeatResponseDto>> currentDesks = null;
		// from DeskSeat
		List<LinkedHashMap<Short, DeskPositionSeatResponseDto>> currentDeskPositions = null;

		for (GroupUserResponseDto groupUser : groupUserList) {

			if (!Objects.equals(currentGroupId, groupUser.getGroupId())) {

				currentGroupId = groupUser.getGroupId();
				currentDesk = null;

				currentDesks = new ArrayList<>();

				GroupSeatResponseDto groupDto = GroupSeatResponseDto.builder()
						.desks(currentDesks)
						.build();

				LinkedHashMap<Long, GroupSeatResponseDto> groupMap = new LinkedHashMap<>();
				groupMap.put(currentGroupId, groupDto);

				groups.add(groupMap);
			}

			if (!Objects.equals(currentDesk, groupUser.getDesk())) {

				currentDesk = groupUser.getDesk();

				currentDeskPositions = new ArrayList<>();

				DeskSeatResponseDto deskDto = DeskSeatResponseDto.builder()
						.deskPositions(currentDeskPositions)
						.build();

				LinkedHashMap<Short, DeskSeatResponseDto> deskMap = new LinkedHashMap<>();
				deskMap.put(currentDesk, deskDto);

				currentDesks.add(deskMap);
			}

			DeskPositionSeatResponseDto seatDto = DeskPositionSeatResponseDto.builder()
					.userId(groupUser.getUserId())
					.userDisplayName(groupUser.getUserDisplayName())
					.build();

			LinkedHashMap<Short, DeskPositionSeatResponseDto> deskPositionMap = new LinkedHashMap<>();
			deskPositionMap.put(groupUser.getDeskPosition(), seatDto);

			currentDeskPositions.add(deskPositionMap);
		}

		return ClassSeatResponseDto.builder()
				.groups(groups)
				.build();
	}

}