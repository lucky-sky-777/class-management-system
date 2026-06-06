package com.mezon.classmanagement.backend.common.security;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.common.security.authority.ClassPermission;
import com.mezon.classmanagement.backend.common.security.service.JwtService;
import com.mezon.classmanagement.backend.domain.auth.service.AuthService;
import com.mezon.classmanagement.backend.domain.auth.service.UserService;
import com.mezon.classmanagement.backend.domain.classuser.entity.ClassUser;
import com.mezon.classmanagement.backend.domain.classuser.service.ClassUserService;
import com.mezon.classmanagement.backend.domain.clazz.service.ClassService;
import com.mezon.classmanagement.backend.domain.group.service.GroupService;
import com.mezon.classmanagement.backend.domain.groupuser.entity.GroupUser;
import com.mezon.classmanagement.backend.domain.groupuser.service.GroupUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@SuppressWarnings(value = {
		WarningConstant.UNUSED,
		WarningConstant.SPELL_CHECKING_INSPECTION,
		WarningConstant.BOOLEAN_METHOD_IS_ALWAYS_INVERTED
})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component("ClassSecurity")
public class ClassSecurity {

	AuthService authService;
	JwtService jwtService;

	ClassService classService;
	UserService userService;
	GroupService groupService;
	GroupUserService groupUserService;
	ClassUserService classUserService;

	public boolean adminOnly(Long classId) {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserIdFromAuthentication(authentication);

		ClassUser classUser = classUserService.findByClassIdAndUserIdOrThrow(classId, userId);

		if (!classUserService.isAdmin(classUser)) {
			throw new AccessDeniedException("Chỉ Admin được phép làm việc này");
		}

		return true;
	}

	public boolean exceptAdmin(Long classId) {
		try {
			if (adminOnly(classId)) {
				throw new AccessDeniedException("Admin không được phép làm việc này");
			}
		} catch (AccessDeniedException ignored) {
		}

		return true;
	}

	public boolean manageActivity(Long classId) {
		if (!hasClassAccess(classId, ClassPermission.MANAGE_ACTIVITY)) {
			throw new AccessDeniedException("Yêu cầu quyền: " + ClassPermission.MANAGE_ACTIVITY.getLabel());
		}

		return true;
	}

	public boolean manageGroupData(
			Long classId,
			Long groupId
	) {
		if (!(manageGroup(classId) || hasGroupAccess(classId, groupId))) {
			throw new AccessDeniedException("Yêu cầu quyền: "
					+ ClassPermission.MANAGE_ACTIVITY.getLabel()
					+ " hoặc "
					+ GroupUser.Role.GROUP_LEADER.name()
			);
		}

		return true;
	}

	public boolean manageGroup(Long classId) {
		if (!hasClassAccess(classId, ClassPermission.MANAGE_GROUP)) {
			throw new AccessDeniedException("Yêu cầu quyền: " + ClassPermission.MANAGE_GROUP.getLabel());
		}

		return true;
	}

	public boolean manageFund(Long classId) {
		if (!hasClassAccess(classId, ClassPermission.MANAGE_FUND)) {
			throw new AccessDeniedException("Yêu cầu quyền: " + ClassPermission.MANAGE_FUND.getLabel());
		}

		return true;
	}

	public boolean manageAbsenceRequest(Long classId) {
		if (!hasClassAccess(classId, ClassPermission.MANAGE_ABSENCE_REQUEST)) {
			throw new AccessDeniedException("Yêu cầu quyền: " + ClassPermission.MANAGE_ABSENCE_REQUEST.getLabel());
		}

		return true;
	}

	public boolean managePoint(Long classId) {
		if (!hasClassAccess(classId, ClassPermission.MANAGE_POINT)) {
			throw new AccessDeniedException("Yêu cầu quyền: " + ClassPermission.MANAGE_POINT.getLabel());
		}

		return true;
	}

	public boolean everyoneInClass(Long classId) {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserIdFromAuthentication(authentication);

		if (!classUserService.existsByClassIdAndUserId(classId, userId)) {
			throw new AccessDeniedException("Chỉ thành viên lớp được phép làm việc này");
		}

		return true;
	}

	/**
	 * Private
	 */

	private boolean hasGroupAccess(
			Long classId,
			Long groupId
	) {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserIdFromAuthentication(authentication);

		GroupUser groupUser = groupUserService.findByClassIdAndGroupIdAndUserIdOrThrow(classId, groupId, userId);

		return groupUserService.isLeader(groupUser);
	}

	private boolean hasClassAccess(
			Long classId,
			ClassPermission classPermission
	) {
		classService.throwIfNotExistsById(classId);

		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserIdFromAuthentication(authentication);

		userService.throwIfNotExistsById(userId);

		ClassUser classUser = classUserService.findByClassIdAndUserIdOrThrow(classId, userId);

		if (classUserService.isAdmin(classUser)) {
			return true;
		}
		if (classUserService.isMember(classUser)) {
			return classUserService.hasPermission(classUser, classPermission);
		}

		return false;
	}

}