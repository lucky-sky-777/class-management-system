export type MemberRole = "OWNER" | "CLASS_ADMIN" | "CLASS_MEMBER";

export interface Member {
  userId: number;
  displayName: string;
  username: string;
  role: MemberRole;
  avatarUrl?: string;
  joinedAt: string;
}

export interface UpdateRoleRequest {
  role: MemberRole;
}