package com.mezon.classmanagement.backend.common.security.permission;

public sealed interface ClassRoleOrPermission permits ClassRole, ClassPermission {

	String getLabel();

}