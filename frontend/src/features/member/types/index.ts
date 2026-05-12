export type MemberRole = "OWNER" | "CLASS_ADMIN" | "CLASS_MEMBER" | "PENDING";

export interface Member {
  userId: number;
  displayName: string;
  username: string;
  role: MemberRole;
  avatarUrl?: string;
  joinedAt: string;
  requestId?: number;
}

export interface UpdateRoleRequest {
  role: MemberRole;
}