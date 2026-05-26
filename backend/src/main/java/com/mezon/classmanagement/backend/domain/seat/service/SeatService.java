package com.mezon.classmanagement.backend.domain.seat.service;

import com.mezon.classmanagement.backend.common.constant.GroupConstant;
import com.mezon.classmanagement.backend.domain.group.dto.GroupResponseDto;
import com.mezon.classmanagement.backend.domain.group.entity.Group;
import com.mezon.classmanagement.backend.domain.group.service.GroupService;
import com.mezon.classmanagement.backend.domain.groupuser.dto.request.CreateGroupUserSeatRequestDto;
import com.mezon.classmanagement.backend.domain.groupuser.dto.request.UpdateGroupUserSeatRequestDto;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class SeatService {

	/**
	 * Service
	 */

	GroupService groupService;
	GroupUserService groupUserService;

	@Transactional
	public ClassSeatResponseDto create(
			Long classId,
			CreateGroupUserSeatRequestDto request
	) {
		groupUserService.throwIfExistsByClassIdAndGroupIdAndDeskAndDeskPosition(
				classId,
				request.getTargetGroupId(),
				request.getTargetDesk(),
				request.getTargetDeskPosition()
		);

		GroupUser groupUser = groupUserService.findByClassIdAndUserIdOrThrow(
				classId,
				request.getUserId()
		);

		Group group = groupService.findByClassIdAndGroupIdOrThrow(classId, request.getTargetGroupId());

		groupUser.setGroup(group);
		groupUser.setDesk(request.getTargetDesk());
		groupUser.setDeskPosition(request.getTargetDeskPosition());

		groupUserService.save(groupUser);

		System.out.println("create");

		return get(classId);
	}

	@Transactional
	public ClassSeatResponseDto update(
			Long classId,
			UpdateGroupUserSeatRequestDto request
	) {
		Group targetGroup = groupService.findByClassIdAndGroupIdOrThrow(classId, request.getTargetGroupId());

		GroupUser sourceGroupUser = groupUserService
				.findByClassIdAndGroupIdAndDeskAndDeskPositionOrThrow(
						classId,
						request.getSourceGroupId(),
						request.getSourceDesk(),
						request.getSourceDeskPosition()
				);

		if (groupUserService.existsByClassIdAndGroupIdAndDeskAndDeskPosition(
				classId,
				request.getTargetGroupId(),
				request.getTargetDesk(),
				request.getTargetDeskPosition()
		)) {
			GroupUser targetGroupUser = groupUserService
					.findByClassIdAndGroupIdAndDeskAndDeskPositionOrThrow(
							classId,
							request.getTargetGroupId(),
							request.getTargetDesk(),
							request.getTargetDeskPosition()
					);
			swap(sourceGroupUser, targetGroupUser);
			System.out.println("swap");

			//groupUserService.save(sourceGroupUser);
			//groupUserService.save(targetGroupUser);
			groupUserService.saveAll(List.of(sourceGroupUser, targetGroupUser));
		} else {
			set(
					sourceGroupUser,
					targetGroup,
					request.getTargetDesk(),
					request.getTargetDeskPosition()
			);
			System.out.println("set");

			groupUserService.save(sourceGroupUser);
		}

		return get(classId);
	}

	@Transactional
	public ClassSeatResponseDto shuffle(Long classId) {
		return shuffleRoundRobin2(classId);
	}

	private void set(GroupUser groupUser, Group newGroup, Short newDesk, Short newDeskPosition) {
		groupUser.setGroup(newGroup);
		groupUser.setDesk(newDesk);
		groupUser.setDeskPosition(newDeskPosition);
	}

	private void swap(GroupUser source, GroupUser target) {
		Group tempGroup = source.getGroup();
		Short tempDesk = source.getDesk();
		Short tempDeskPosition = source.getDeskPosition();

		set(source, target.getGroup(), target.getDesk(), target.getDeskPosition());
		set(target, tempGroup, tempDesk, tempDeskPosition);
	}

	@Transactional
	protected ClassSeatResponseDto shuffleRoundRobin(Long classId) {

		List<GroupUser> groupUsers = groupUserService.findByClassId(classId);

		Collections.shuffle(groupUsers);

		final int TOTAL_GROUPS = 4;
		final int POSITIONS_PER_DESK = 2;

		Map<Long, Group> groupMap = groupService
				.findAllById(
						List.of(1L, 2L, 3L, 4L)
				)
				.stream()
				.collect(
						Collectors
								.toMap(
										Group::getId,
										Function.identity()
								)
				);

		Map<Integer, Integer> groupSeatCounter = new HashMap<>();

		for (int i = 0; i < TOTAL_GROUPS; i++) {
			groupSeatCounter.put(i + 1, 0);
		}

		for (int i = 0; i < groupUsers.size(); i++) {

			GroupUser groupUser = groupUsers.get(i);

			int groupNumber = (i % TOTAL_GROUPS) + 1;

			int seatIndex = groupSeatCounter.get(groupNumber);

			short desk =
					(short) ((seatIndex / POSITIONS_PER_DESK) + 1);

			short deskPosition =
					(short) ((seatIndex % POSITIONS_PER_DESK) + 1);

			groupUser.setGroup(
					groupMap.get((long) groupNumber)
			);

			groupUser.setDesk(desk);

			groupUser.setDeskPosition(deskPosition);

			groupSeatCounter.put(
					groupNumber,
					seatIndex + 1
			);
		}

		groupUserService.saveAll(groupUsers);

		return get(classId);
	}

	@Transactional
	protected ClassSeatResponseDto shuffleRoundRobin2(Long classId) {
		List<GroupUser> groupUsers = groupUserService.findByClassId(classId);

		Collections.shuffle(groupUsers);

		List<Group> groups = groupService.findByClassId(classId);

		int totalGroups = groups.size();

		Map<Long, Integer> groupSeatCounter = new HashMap<>();

		for (Group group : groups) {
			groupSeatCounter.put(group.getId(), 0);
		}

		for (int i = 0; i < groupUsers.size(); i++) {
			GroupUser groupUser = groupUsers.get(i);

			Group group = groups.get(i % totalGroups);

			int seatIndex = groupSeatCounter.get(group.getId());

			short desk = (short) ((seatIndex / GroupConstant.DESK_POSITION_COUNT) + 1);

			short deskPosition = (short) ((seatIndex % GroupConstant.DESK_POSITION_COUNT) + 1);

			groupUser.setGroup(group);

			groupUser.setDesk(desk);

			groupUser.setDeskPosition(deskPosition);

			groupSeatCounter.put(
					group.getId(),
					seatIndex + 1
			);
		}

		groupUserService.saveAll(groupUsers);

		return get(classId);
	}

//	@Transactional(readOnly = true)
//	public ClassSeatResponseDto get(Long classId) {
//		List<GroupUserResponseDto> groupUserList = groupUserService.getByClassId(classId);
//		Map<Long, Map<Short, Map<Short, GroupUserResponseDto>>> seatMap = new HashMap<>();
//		Map<Long, String> groupNameMap = new HashMap<>();
//		for (GroupUserResponseDto groupUser : groupUserList) {
//			seatMap
//					.computeIfAbsent(
//							groupUser.getGroupId(),
//							k -> new HashMap<>()
//					)
//					.computeIfAbsent(
//							groupUser.getDesk(),
//							k -> new HashMap<>()
//					)
//					.put(groupUser.getDeskPosition(), groupUser);
//			groupNameMap.putIfAbsent(
//					groupUser.getGroupId(),
//					groupUser.getGroupName()
//			);
//		}
//		List<LinkedHashMap<Long, GroupSeatResponseDto>> groups = new ArrayList<>();
//
//		for (/*long groupId = 1; groupId <= GroupConstant.GROUP_COUNT; groupId++*/ Long groupId : groupNameMap.keySet()) {
//			List<LinkedHashMap<Short, DeskSeatResponseDto>> desks = new ArrayList<>();
//			Map<Short, Map<Short, GroupUserResponseDto>> groupData = seatMap.getOrDefault(groupId, Collections.emptyMap());
//			for (short desk = 1; desk <= GroupConstant.DESK_COUNT; desk++) {
//				List<LinkedHashMap<Short, DeskPositionSeatResponseDto>> deskPositions = new ArrayList<>();
//				Map<Short, GroupUserResponseDto> deskData = groupData.getOrDefault(desk, Collections.emptyMap());
//				for (short position = 1; position <= GroupConstant.DESK_POSITION_COUNT; position++) {
//					GroupUserResponseDto user = deskData.get(position);
//					DeskPositionSeatResponseDto seatDto = null;
//					if (user != null) {
//						seatDto = DeskPositionSeatResponseDto.builder()
//								.userId(user.getUserId())
//								.userDisplayName(user.getUserDisplayName())
//								.attendanceStatus(user.getAttendanceStatus())
//								.build();
//					}
//					LinkedHashMap<Short, DeskPositionSeatResponseDto> positionMap = new LinkedHashMap<>();
//					positionMap.put(position, seatDto);
//					deskPositions.add(positionMap);
//				}
//				DeskSeatResponseDto deskDto = DeskSeatResponseDto.builder()
//						.deskPositions(deskPositions)
//						.build();
//				LinkedHashMap<Short, DeskSeatResponseDto> deskMap = new LinkedHashMap<>();
//				deskMap.put(desk, deskDto);
//				desks.add(deskMap);
//			}
//			GroupSeatResponseDto groupDto = GroupSeatResponseDto.builder()
//					.name(groupNameMap.get(groupId))
//					.desks(desks)
//					.build();
//			LinkedHashMap<Long, GroupSeatResponseDto> groupMap = new LinkedHashMap<>();
//			groupMap.put(groupId, groupDto);
//			groups.add(groupMap);
//		}
//		return ClassSeatResponseDto.builder()
//				.groups(groups)
//				.build();
//	}

	@Transactional(readOnly = true)
	public ClassSeatResponseDto get(Long classId) {
		List<GroupResponseDto> groupList = groupService.getByClassId(classId);
		List<GroupUserResponseDto> groupUserList = groupUserService.getByClassId(classId);
		Map<Long, Map<Short, Map<Short, GroupUserResponseDto>>> seatMap = new HashMap<>();
		for (GroupUserResponseDto groupUser : groupUserList) {
			seatMap
					.computeIfAbsent(
							groupUser.getGroupId(),
							k -> new HashMap<>()
					)
					.computeIfAbsent(
							groupUser.getDesk(),
							k -> new HashMap<>()
					)
					.put(groupUser.getDeskPosition(), groupUser);
		}
		List<LinkedHashMap<Long, GroupSeatResponseDto>> groups = new ArrayList<>();

		for (/*long groupId = 1; groupId <= GroupConstant.GROUP_COUNT; groupId++*/ GroupResponseDto group : groupList) {
			Long groupId = group.getId();
			List<LinkedHashMap<Short, DeskSeatResponseDto>> desks = new ArrayList<>();
			Map<Short, Map<Short, GroupUserResponseDto>> groupData = seatMap.getOrDefault(groupId, Collections.emptyMap());
			for (short desk = 1; desk <= GroupConstant.DESK_COUNT; desk++) {
				List<LinkedHashMap<Short, DeskPositionSeatResponseDto>> deskPositions = new ArrayList<>();
				Map<Short, GroupUserResponseDto> deskData = groupData.getOrDefault(desk, Collections.emptyMap());
				for (short position = 1; position <= GroupConstant.DESK_POSITION_COUNT; position++) {
					GroupUserResponseDto user = deskData.get(position);
					DeskPositionSeatResponseDto seatDto = null;
					if (user != null) {
						seatDto = DeskPositionSeatResponseDto.builder()
								.userId(user.getUserId())
								.userDisplayName(user.getUserDisplayName())
								.userAvatarUrl(user.getUserAvatarUrl())
								.attendanceStatus(user.getAttendanceStatus())
								.build();
					}
					LinkedHashMap<Short, DeskPositionSeatResponseDto> positionMap = new LinkedHashMap<>();
					positionMap.put(position, seatDto);
					deskPositions.add(positionMap);
				}
				DeskSeatResponseDto deskDto = DeskSeatResponseDto.builder()
						.deskPositions(deskPositions)
						.build();
				LinkedHashMap<Short, DeskSeatResponseDto> deskMap = new LinkedHashMap<>();
				deskMap.put(desk, deskDto);
				desks.add(deskMap);
			}
			GroupSeatResponseDto groupDto = GroupSeatResponseDto.builder()
					.name(group.getName())
					.desks(desks)
					.build();
			LinkedHashMap<Long, GroupSeatResponseDto> groupMap = new LinkedHashMap<>();
			groupMap.put(groupId, groupDto);
			groups.add(groupMap);
		}
		return ClassSeatResponseDto.builder()
				.groups(groups)
				.build();
	}

	@Deprecated
	@Transactional(readOnly = true)
	public ClassSeatResponseDto getClassSeats2(Long classId) {
		List<GroupUserResponseDto> groupUserList = groupUserService.getByClassId(classId);

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