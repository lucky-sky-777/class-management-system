import { apiClient } from "@services/api-client";

export const emulationAPI = {
  // 1. LẤY LỊCH SỬ TOÀN LỚP
  getHistoryByClass: async (
    classId: string,
    startDate?: string,
    endDate?: string,
  ) => {
    const query = new URLSearchParams();
    if (startDate && endDate) {
      // ÉP TÊN BIẾN KHỚP VỚI JAVA (startAt, endAt)
      query.append("startAt", startDate);
      query.append("endAt", endDate);
    }
    const queryString = query.toString() ? `?${query.toString()}` : "";

    // Gọi API với query string đã build
    const response = (await apiClient.get(
      `/classes/${classId}/points${queryString}`,
    )) as any;
    return response.data?.data || response.data || [];
  },

  // 2. LẤY XẾP HẠNG TUẦN
  getWeekRanking: async (
    classId: string,
    startDate?: string,
    endDate?: string,
  ) => {
    const query = new URLSearchParams();
    if (startDate && endDate) {
      // KHỚP VỚI @ModelAttribute ở Backend
      query.append("startAt", startDate);
      query.append("endAt", endDate);
    }
    const queryString = query.toString() ? `?${query.toString()}` : "";

    const response = (await apiClient.get(
      `/classes/${classId}/points/week-ranking${queryString}`,
    )) as any;
    return response.data?.data || response.data || [];
  },

 // 3. LẤY XẾP HẠNG THÁNG
  getMonthRanking: async (classId: string, startDate?: string, endDate?: string) => {
    const query = new URLSearchParams();
    if (startDate && endDate) {
      query.append("startAt", startDate);
      query.append("endAt", endDate);
    }
    query.append("_t", new Date().getTime().toString()); // Phá cache

    const response = (await apiClient.get(
      `/classes/${classId}/points/month-ranking?${query.toString()}`,
    )) as any;
    return response.data?.data || response.data || [];
  },

  // 4. GHI ĐIỂM
  addPoints: async (
    classId: string,
    groupId: number,
    content: string,
    points: number,
  ) => {
    const response = (await apiClient.post(
      `/classes/${classId}/points/groups/${groupId}`,
      {
        description: content,
        point: points,
      },
    )) as any;
    return response.data;
  },

  // 5. XÓA ĐIỂM
  deletePointLog: async (classId: string, pointId: number) => {
    const response = (await apiClient.delete(
      `/classes/${classId}/points/${pointId}`,
    )) as any;
    return response.data;
  },

  // 6. LẤY LỊCH SỬ CỦA 1 TỔ
  getHistoryByGroup: async (
    classId: string,
    groupId: number,
    startDate?: string,
    endDate?: string,
  ) => {
    const query = new URLSearchParams();
    if (startDate && endDate) {
      query.append("start_at", startDate);
      query.append("end_at", endDate);
    }
    const queryString = query.toString() ? `?${query.toString()}` : "";
    const response = (await apiClient.get(
      `/classes/${classId}/points/groups/${groupId}${queryString}`,
    )) as any;
    return response.data?.data || [];
  },

  // 7. LẤY DANH SÁCH TUẦN
  getWeeks: async (year: number) => {
    const response = (await apiClient.get(`/public/weeks?year=${year}`)) as any;
    return response.data?.data || response.data || response || [];
  },

  // 8. TẠO TỔ MỚI
  createGroup: async (classId: string, name: string) => {
    const response = (await apiClient.post(
      `/classes/${classId}/groups`,
      { name } // Gửi tên tổ lên (VD: "Tổ 5")
    )) as any;
    return response.data ?? response;
  },

  // 9. CẬP NHẬT TÊN TỔ (Dành cho sau này nếu muốn đổi tên)
  updateGroup: async (classId: string, groupId: number, name: string) => {
    const response = (await apiClient.patch(
      `/classes/${classId}/groups/${groupId}`,
      { name }
    )) as any;
    return response.data;
  },

  // 10. XÓA TỔ
  deleteGroup: async (classId: string, groupId: number) => {
    const response = (await apiClient.delete(
      `/classes/${classId}/groups/${groupId}`
    )) as any;
    return response.data ?? response;
  },

   // 11 LẤY DANH SÁCH TỔ (GROUP)
  getGroups: async (classId: string) => {
    const response = (await apiClient.get(
      `/classes/${classId}/groups?_t=${new Date().getTime()}`
    )) as any;
    
    return response.data?.data || response.data || [];
  },

  // 12. LẤY DANH SÁCH THÀNH VIÊN TRONG TỔ
  getGroupMembers: async (classId: string, groupId: number) => {
    const response = (await apiClient.get(
      `/classes/${classId}/groups/${groupId}/members`
    )) as any;
    return response.data?.data || response.data || [];
  },

  // 13. THÊM THÀNH VIÊN VÀO TỔ
  addGroupMember: async (classId: string, groupId: number, userId: string) => {
    const response = (await apiClient.post(
      `/classes/${classId}/groups/${groupId}/members`,
      { user_id: parseInt(userId) } // Dự đoán payload dựa theo chuẩn chung
    )) as any;
    return response.data;
  },

  // 14. XÓA THÀNH VIÊN KHỎI TỔ
  removeGroupMember: async (classId: string, groupId: number, userId: string) => {
    const response = (await apiClient.delete(
      `/classes/${classId}/groups/${groupId}/members/${userId}`
    )) as any;
    return response.data;
  },

  // 15. LẤY DANH SÁCH HỌC SINH CHƯA CÓ TỔ
  getUngroupedMembers: async (classId: string) => {
    try {
      const response = (await apiClient.get(
        `/classes/${classId}/members/ungrouped`
      )) as any;
      return response.data?.data || response.data || [];
    } catch (error) {
      console.error("Lỗi lấy danh sách chưa có tổ:", error);
      return []; // Lỗi thì trả về mảng rỗng để UI không sập
    }
  },
};
