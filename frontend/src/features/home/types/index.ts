import type { ClassPrivacy, ClassRole, ClassStatus } from "@shared/domain/enums";

export interface ClassItems {
  id: number;
  name: string;
  owner_user_id: number;
  owner_avatar_url: string;
  description: string;
  code: string;
  owner_display_name: string;
  privacy: ClassPrivacy;
  status: ClassStatus;
}

export interface ClassResponse {
  id: string;
  name: string;       
  description?: string;   
  privacy: ClassPrivacy; 
  owner_username: string;
  avatar_url: string;
  createdAt?: string;
}

export interface ClassIdResponse {
  classId: number;
}

// Interface cho thành viên trong lớp
export interface ClassMember {
  id: number;
  class_id: number;
  class_name: string;
  user_id: number;
  user_display_name: string;
  member_avatar_url: string | null;
  role: ClassRole;
  joined_at?: string;
}

export interface ClassMemberResponse {
  success: boolean;
  message: string;
  data: ClassMember[];
}

export interface JoinClassResult {
  name: string;
  type?: 'REQUESTED' | 'JOINED';
  userJoinStatus?: 'joined' | 'pending';
}