package com.mezon.classmanagement.backend.common.security.permission;

import com.mezon.classmanagement.backend.common.constant.WarningConstant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@SuppressWarnings(value = {WarningConstant.SPELL_CHECKING_INSPECTION})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
@RequiredArgsConstructor
public enum ClassRole implements ClassRoleOrPermission {

	CLASS_ADMIN("Quản trị viên"),
	CLASS_MEMBER("Thành viên");

	String label;

}