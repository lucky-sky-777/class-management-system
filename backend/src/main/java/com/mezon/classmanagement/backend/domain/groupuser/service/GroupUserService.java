package com.mezon.classmanagement.backend.domain.groupuser.service;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.common.exeption.entity.GlobalException;
import com.mezon.classmanagement.backend.common.security.annotation.RequireClassSecurity;
import com.mezon.classmanagement.backend.domain.auth.entity.User;
import com.mezon.classmanagement.backend.domain.clazz.entity.Class;
import com.mezon.classmanagement.backend.domain.group.entity.Group;
import com.mezon.classmanagement.backend.domain.group.service.GroupService;
import com.mezon.classmanagement.backend.domain.groupuser.dto.request.CreateGroupUserRequestDto;
import com.mezon.classmanagement.backend.domain.groupuser.dto.request.UpdateGroupUserRoleRequestDto;
import com.mezon.classmanagement.backend.domain.groupuser.dto.response.GroupUserIdResponseDto;
import com.mezon.classmanagement.backend.domain.groupuser.dto.response.GroupUserResponseDto;
import com.mezon.classmanagement.backend.domain.groupuser.entity.GroupUser;
import com.mezon.classmanagement.backend.domain.groupuser.mapper.GroupUserMapper;
import com.mezon.classmanagement.backend.domain.groupuser.repository.GroupUserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SuppressWarnings({WarningConstant.BOOLEAN_METHOD_IS_ALWAYS_INVERTED})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class GroupUserService {

	GroupUserRepository groupUserRepository;

	GroupUserMapper groupUserMapper;

	GroupService groupService;

	@RequireClassSecurity
	@Transactional
	public GroupUserResponseDto createGroupUser(Long classId, Long groupId, CreateGroupUserRequestDto request) {
		throwIfExistsByClassIdAndGroupIdAndUserId(classId, groupId, request.getUserId());

		Class clazz = Class.builder()
				.id(classId)
				.build();
		Group group = Group.builder()
				.id(groupId)
				.build();
		User user = User.builder()
				.id(request.getUserId())
				.build();

		GroupUser newGroupUser = GroupUser.builder()
				.clazz(clazz)
				.group(group)
				.user(user)
				.role(GroupUser.Role.GROUP_MEMBER)
				.build();
		GroupUser responseGroupUser = save(newGroupUser);

		return groupUserMapper.toGroupUserResponseDto(responseGroupUser);
	}

	@RequireClassSecurity
	@Transactional
	public GroupUserResponseDto updateGroupUser(Long classId, Long groupId, Long userId, UpdateGroupUserRoleRequestDto request) {
		GroupUser currentGroupUser = findByClassIdAndGroupIdAndUserIdOrThrow(classId, groupId, userId);

		groupUserMapper.updateGroupUserFromRequestDto(request, currentGroupUser);

		GroupUser responseGroupUser = save(currentGroupUser);

		return groupUserMapper.toGroupUserResponseDto(responseGroupUser);
	}

	@RequireClassSecurity
	@Transactional
	public GroupUserIdResponseDto deleteGroupUser(Long classId, Long groupId, Long userId) {
		GroupUser currentGroupUser = findByClassIdAndGroupIdAndUserIdOrThrow(classId, groupId, userId);

		delete(currentGroupUser);

		return GroupUserIdResponseDto.builder()
				.groupUserId(currentGroupUser.getId())
				.build();
	}

	@RequireClassSecurity
	@Transactional(readOnly = true)
	public List<GroupUserResponseDto> getByClassAndGroup(Long classId, Long groupId) {
		return getByClassIdAndGroupId(classId, groupId);
	}

	/**
	 * Action
	 */

	@Transactional
	public GroupUser save(GroupUser groupUser) {
		return groupUserRepository.save(groupUser);
	}

	@Transactional
	public List<GroupUser> saveAll(Iterable<GroupUser> groupUsers) {
		return groupUserRepository.saveAll(groupUsers);
	}

	@Transactional
	public void delete(GroupUser groupUser) {
		groupUserRepository.delete(groupUser);
	}

	/**
	 * Find
	 */

	@Transactional(readOnly = true)
	public List<GroupUser> findByClassId(Long classId) {
		return groupUserRepository
				.findByClazz_IdOrderByGroup_IdAscDeskAscDeskPositionAsc(classId);
	}

	@Transactional(readOnly = true)
	public List<GroupUserResponseDto> getByClassId(Long classId) {
		return groupUserRepository
				.getByClazz_IdOrderByGroup_IdAscDeskAscDeskPositionAsc(classId);
	}

	@Transactional(readOnly = true)
	public List<GroupUserResponseDto> getByClassIdAndGroupId(Long classId, Long groupId) {
		return groupUserRepository
				.getByClazz_IdAndGroup_IdOrderByGroup_IdAscUser_DisplayNameAsc(classId, groupId);
	}

	@Transactional(readOnly = true)
	public List<GroupUser> findByClassIdAndGroupId(Long classId, Long groupId) {
		return groupUserRepository
				.findByClazz_IdAndGroup_Id(classId, groupId);
	}

	@Transactional(readOnly = true)
	public GroupUser findByClassIdAndUserIdOrThrow(Long classId, Long userId) {
		return groupUserRepository
				.findByClazz_IdAndUser_Id(classId, userId)
				.orElseThrow(() ->
						new GlobalException(GlobalException.Type.NOT_FOUND, "Group user not found")
				);
	}

	@Transactional(readOnly = true)
	public GroupUser findByClassIdAndGroupIdAndUserIdOrThrow(Long classId, Long groupId, Long userId) {
		return groupUserRepository
				.findByClazz_IdAndGroup_IdAndUser_Id(classId, groupId, userId)
				.orElseThrow(() ->
						new GlobalException(GlobalException.Type.NOT_FOUND, "Group user not found")
				);
	}

	@Transactional(readOnly = true)
	public GroupUser findByClassIdAndGroupIdAndDeskAndDeskPositionOrThrow(Long classId, Long groupId, Short desk, Short deskPosition) {
		return groupUserRepository
				.findByClazz_IdAndGroup_IdAndDeskAndDeskPosition(classId ,groupId, desk, deskPosition)
				.orElseThrow(() ->
						new GlobalException(GlobalException.Type.NOT_FOUND, "Group user not found")
				);
	}

	/**
	 * Exists
	 */

	@Transactional(readOnly = true)
	public boolean existsByClassIdAndGroupIdAndUserid(Long classId, Long groupId, Long userId) {
		return groupUserRepository.existsByClazz_IdAndGroup_IdAndUser_Id(classId, groupId, userId);
	}

	@Transactional(readOnly = true)
	public boolean existsByClassIdAndGroupIdAndDeskAndDeskPosition(Long classId, Long groupId, Short desk, Short deskPosition) {
		return groupUserRepository
				.existsByClazz_IdAndGroup_IdAndDeskAndDeskPosition(classId, groupId, desk, deskPosition);
	}

	@Transactional(readOnly = true)
	public void throwIfExistsByClassIdAndGroupIdAndDeskAndDeskPosition(Long classId, Long groupId, Short desk, Short deskPosition) {
		if (existsByClassIdAndGroupIdAndDeskAndDeskPosition(classId, groupId, desk, deskPosition)) {
			throw new GlobalException(GlobalException.Type.ALREADY_EXISTS, "Class user seat exists");
		}
	}

	@Transactional(readOnly = true)
	public void throwIfExistsByClassIdAndGroupIdAndUserId(Long classId, Long groupId, Long userId) {
		if (existsByClassIdAndGroupIdAndUserid(classId, groupId, userId)) {
			throw new GlobalException(GlobalException.Type.NOT_FOUND, "Group user exists");
		}
	}

	/**
	 * Validate
	 */

	public boolean isLeader(GroupUser groupUser) {
		return GroupUser.Role.GROUP_LEADER.equals(groupUser.getRole());
	}

}