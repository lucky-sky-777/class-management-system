// src/features/member/api.ts
import { apiClient } from "@services/api-client";
import type { Member, MemberRole } from "@features/member/types";

export const memberAPI = {
  // 1. Chỉ lấy thành viên chính thức
  getMembers: async (classId: string): Promise<Member[]> => {
    const [resMembers, resClass]: any = await Promise.all([
      apiClient.get(`/classes/${classId}/members`),
      apiClient.get(`/classes/${classId}/data`),
    ]);

    const rawMembers = resMembers.data?.data || resMembers.data || [];
    const ownerId =
      resClass.data?.data?.owner_user_id || resClass.data?.owner_user_id;

    return rawMembers.map((m: any) => ({
      userId: Number(m.user_id),
      displayName: m.user_display_name,
      username: m.username || "",
      role:
        Number(m.user_id) === Number(ownerId)
          ? "OWNER"
          : (m.role as MemberRole),
      joinedAt: m.joined_at,
    }));
  },

  getPendingRequests: async (classId: string): Promise<Member[]> => {
    try {
      const res: any = await apiClient.get(`/join-class-requests/${classId}`);
      const rawData = res.data?.data || res.data || [];

      // CHỈ LỌC NHỮNG THẰNG CÓ STATUS LÀ PENDING
      const pendingOnly = rawData.filter((p: any) => p.status === "PENDING");

      return pendingOnly.map((p: any) => ({
        userId: p.user_id,
        requestId: p.id,
        displayName: p.user_display_name || `Học sinh #${p.user_id}`,
        username: `user_${p.user_id}`,
        role: "PENDING",
        joinedAt: p.created_at,
      }));
    } catch (error) {
      return [];
    }
  },

  updateRole: async (classId: string, userId: number, role: MemberRole) => {
    return await apiClient.patch(`/classes/${classId}/members/${userId}/role`, {
      role,
    });
  },

  kickMember: async (classId: string, userId: number) => {
    return await apiClient.delete(`/classes/${classId}/members/${userId}`);
  },

  //duyet
  approveRequest: async (classId: string | number, requestId: number) => {
    return await apiClient.patch(
      `/join-class-requests/classes/${classId}/requests/${requestId}/approve`,
      {},
    );
  },

  //tu choi
  rejectRequest: async (classId: string | number, requestId: number) => {
    return await apiClient.patch(
      `/join-class-requests/classes/${classId}/requests/${requestId}/reject`,
      {},
    );
  },
};
