// src/features/emulation/api.ts
import { apiClient } from "@services/api-client";

export const emulationAPI = {
  // 1. Lấy dữ liệu thi đua & Xếp hạng
  getCompetition: async (classId: string, month: number, startDate: string, endDate: string) => {
    const query = new URLSearchParams({
      month: String(month),
      startDate: startDate || "",
      endDate: endDate || ""
    }).toString();

    const response = await apiClient.get(`/emulations/classes/${classId}?${query}`) as any;
    return response.data?.data || response.data;
  },

  // 2. GHI ĐIỂM 
  // URL: /api/classes/{classId}/points/groups/{groupId}
  addPoints: async (classId: string, groupId: number, content: string, points: number) => {
    const payload = {
      content: content,
      points: points
    };

    const response = await apiClient.post(
      `/classes/${classId}/points/groups/${groupId}`, 
      payload
    ) as any;
    return response.data;
  },

  // 3. XÓA ĐIỂM
  deletePointLog: async (classId: string, pointId: number) => {
    const response = await apiClient.delete(`/classes/${classId}/points/${pointId}`) as any;
    return response.data;
  },

  // 4. LẤY LỊCH SỬ THEO NHÓM
  getHistoryByGroup: async (classId: string, groupId: number, startDate: string, endDate: string) => {
    const query = new URLSearchParams({
      startDate: startDate || "",
      endDate: endDate || ""
    }).toString();

    const response = await apiClient.get(
      `/classes/${classId}/points/groups/${groupId}?${query}`
    ) as any;
    return response.data?.data || response.data;
  },

  // 5. Cập nhật số lượng tổ
  updateTeamCount: async (classId: string, newCount: number) => {
    const response = await apiClient.patch(`/emulations/classes/${classId}/config`, {
      team_count: newCount
    }) as any;
    return response.data;
  }
};