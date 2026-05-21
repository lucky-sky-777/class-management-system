package com.mezon.classmanagement.backend.common.security;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import com.mezon.classmanagement.backend.common.security.permission.ClassPermission;
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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@SuppressWarnings({WarningConstant.UNUSED})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component("ClassSecurity")
public class ClassSecurity {

	AuthService authService;
	ClassService classService;
	UserService userService;
	GroupService groupService;
	GroupUserService groupUserService;
	ClassUserService classUserService;
	JwtService jwtService;

	public boolean adminOnly(Long classId) {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserId(authentication);

		ClassUser classUser = classUserService.findByClassIdAndUserIdOrThrow(classId, userId);

		return classUserService.isAdmin(classUser);
	}

	public boolean exceptAdmin(Long classId) {
		return !adminOnly(classId);
	}

	public boolean manageActivity(Long classId) {
		return hasClassAccess(classId, ClassPermission.MANAGE_ACTIVITY);
	}

	public boolean manageGroupData(Long classId, Long groupId) {
		return manageGroup(classId) || hasGroupAccess(classId, groupId);
	}

	public boolean manageGroup(Long classId) {
		return hasClassAccess(classId, ClassPermission.MANAGE_GROUP);
	}

	public boolean manageFund(Long classId) {
		return hasClassAccess(classId, ClassPermission.MANAGE_FUND);
	}

	public boolean manageAbsenceRequest(Long classId) {
		return hasClassAccess(classId, ClassPermission.MANAGE_ABSENCE_REQUEST);
	}

	public boolean managePoint(Long classId) {
		return hasClassAccess(classId, ClassPermission.MANAGE_POINT);
	}

	public boolean hasGroupAccess(Long classId, Long groupId) {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserId(authentication);

		GroupUser groupUser = groupUserService.findByClassIdAndGroupIdAndUserIdOrThrow(classId, groupId, userId);

		return groupUserService.isLeader(groupUser);
	}

	public boolean everyoneInClass(Long classId) {
		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserId(authentication);

		return classUserService.existsByClassIdAndUserId(classId, userId);
	}

	private boolean hasClassAccess(Long classId, ClassPermission classPermission) {
		classService.throwIfNotExistsById(classId);

		Authentication authentication = authService.getAuthentication();
		Long userId = jwtService.extractUserId(authentication);

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