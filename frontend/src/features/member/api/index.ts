import { apiClient } from "@services/api-client";
import type { Member, MemberRole } from "@features/member/types";

export const memberAPI = {
  // Lấy danh sách thành viên
  getMembers: async (classId: string): Promise<Member[]> => {
    const res: any = await apiClient.get(`/classes/${classId}/members`);
    const rawData = res.data?.data || res.data || [];

    return rawData.map((m: any) => ({
      userId: Number(m.user_id),            
      displayName: m.user_display_name,  
      username: m.username || "",            
      role: m.role as MemberRole,         
      joinedAt: m.joined_at,
    }));
  },

  // Đổi vai trò (Phân quyền)
  updateRole: async (classId: string, userId: number, role: MemberRole) => {
    return await apiClient.patch(`/classes/${classId}/members/${userId}/role`, {
      role,
    });
  },

  // Kick thành viên
  kickMember: async (classId: string, userId: number) => {
    return await apiClient.delete(`/classes/${classId}/members/${userId}`);
  },
};
