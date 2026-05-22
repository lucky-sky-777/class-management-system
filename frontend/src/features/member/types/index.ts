import type { PermissionCode } from "@shared/domain/enums";

export type MemberRole = "OWNER" | "CLASS_ADMIN" | "CLASS_MEMBER" | "PENDING";

export interface Member {
    userId: number;
    displayName: string;
    username: string;
    role: MemberRole;
    avatarUrl?: string;
    joinedAt: string;
    requestId?: number;
    permissions?: PermissionCode[];
}

export interface UpdateRoleRequest {
    role: MemberRole;
}

export interface ClassRoleOrPermission {
    id: number;
    code: string;
    label: string;
}

export interface ClassRoleAndPermissionListResponse {
    roles: ClassRoleOrPermission[];
    permissions: ClassRoleOrPermission[];
}

export interface UpdateClassUserRoleAndPermissionRequest {
    role: MemberRole;
    permissions: PermissionCode[];
}

export type ClassInfo = {
    id: number | string;
    name: string;
    code: string;
    type?: string;
    privacy?: string;
    owner_user_id?: number | string;
};