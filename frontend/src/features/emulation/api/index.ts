import { apiClient } from "@services/api-client";

export const emulationAPI = {
  // 1. LẤY LỊCH SỬ TOÀN LỚP
  getHistoryByClass: async (classId: string, startDate?: string, endDate?: string) => {
    const query = new URLSearchParams();
    if (startDate && endDate) {
      // ÉP TÊN BIẾN KHỚP VỚI JAVA (startAt, endAt)
      query.append("startAt", startDate); 
      query.append("endAt", endDate);
    }
    const queryString = query.toString() ? `?${query.toString()}` : "";
    
    // Gọi API với query string đã build
    const response = await apiClient.get(`/classes/${classId}/points${queryString}`) as any;
    return response.data?.data || response.data || [];
  },

  // 2. LẤY XẾP HẠNG TUẦN
  getWeekRanking: async (classId: string, startDate?: string, endDate?: string) => {
    const query = new URLSearchParams();
    if (startDate && endDate) {
      // KHỚP VỚI @ModelAttribute ở Backend
      query.append("startAt", startDate); 
      query.append("endAt", endDate);
    }
    const queryString = query.toString() ? `?${query.toString()}` : "";

    const response = await apiClient.get(`/classes/${classId}/points/week-ranking${queryString}`) as any;
    return response.data?.data || response.data || [];
  },

  // 3. LẤY XẾP HẠNG THÁNG (Không cần startDate/endDate, để Backend lo)
  getMonthRanking: async (classId: string) => {
    const response = await apiClient.get(`/classes/${classId}/points/month-ranking`) as any;
    return response.data?.data || response.data || [];
  },

  // 4. GHI ĐIỂM 
  addPoints: async (classId: string, groupId: number, content: string, points: number) => {
    const response = await apiClient.post(
      `/classes/${classId}/points/groups/${groupId}`, 
      { content, points }
    ) as any;
    return response.data;
  },

  // 5. XÓA ĐIỂM
  deletePointLog: async (classId: string, pointId: number) => {
    const response = await apiClient.delete(`/classes/${classId}/points/${pointId}`) as any;
    return response.data;
  },

  // 6. LẤY LỊCH SỬ CỦA 1 TỔ 
  getHistoryByGroup: async (classId: string, groupId: number, startDate?: string, endDate?: string) => {
    const query = new URLSearchParams();
    if (startDate && endDate) {
      query.append("start_at", startDate);
      query.append("end_at", endDate);
    }
    const queryString = query.toString() ? `?${query.toString()}` : "";
    const response = await apiClient.get(`/classes/${classId}/points/groups/${groupId}${queryString}`) as any;
    return response.data?.data || [];
  },

  // 7. LẤY DANH SÁCH TUẦN
  getWeeks: async (year: number) => {
    const response = await apiClient.get(`/public/weeks?year=${year}`) as any;
    return response.data?.data || response.data || response || [];
  },
};