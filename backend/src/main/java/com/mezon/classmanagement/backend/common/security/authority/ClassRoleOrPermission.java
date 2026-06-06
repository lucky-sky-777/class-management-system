package com.mezon.classmanagement.backend.common.security.authority;

public sealed interface ClassRoleOrPermission permits ClassRole, ClassPermission {

	String getLabel();

}