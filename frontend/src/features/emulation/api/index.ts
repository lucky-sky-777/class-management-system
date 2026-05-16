import { apiClient } from "@services/api-client";
import type {
  CompetitionHistory,
  TeamRanking,
  GroupItem,
  GroupMember,
  WeekItem,
} from "@features/emulation/types";

// Type hỗ trợ cho các API chỉ trả về object trạng thái chung chung (VD: { success: true })
export type GenericResponse = Record<string, unknown>;

// BỘ HELPER XỬ LÝ DỮ LIỆU AN TOÀN (THAY THẾ CHO ANY)
// Dùng cho các API trả về mảng (List)
const extractList = <T>(response: unknown): T[] => {
  if (!response) return [];
  const res = response as Record<string, unknown>;
  const data = res.data as Record<string, unknown> | undefined;
  return (data?.data || res.data || res || []) as T[];
};

// Dùng cho các API trả về đối tượng đơn (Object)
const extractData = <T>(response: unknown): T => {
  if (!response) return {} as T;
  const res = response as Record<string, unknown>;
  return (res.data ?? res ?? {}) as T;
};

export const emulationAPI = {
  // 1. LẤY LỊCH SỬ TOÀN LỚP
  getHistoryByClass: async (
    classId: string,
    startDate?: string,
    endDate?: string,
  ): Promise<CompetitionHistory[]> => {
    const query = new URLSearchParams();
    if (startDate && endDate) {
      query.append("startAt", startDate);
      query.append("endAt", endDate);
    }
    const queryString = query.toString() ? `?${query.toString()}` : "";

    const response = await apiClient.get(
      `/classes/${classId}/points${queryString}`,
    );
    return extractList<CompetitionHistory>(response);
  },

  // 2. LẤY XẾP HẠNG TUẦN
  getWeekRanking: async (
    classId: string,
    startDate?: string,
    endDate?: string,
  ): Promise<TeamRanking[]> => {
    const query = new URLSearchParams();
    if (startDate && endDate) {
      query.append("startAt", startDate);
      query.append("endAt", endDate);
    }
    const queryString = query.toString() ? `?${query.toString()}` : "";

    const response = await apiClient.get(
      `/classes/${classId}/points/week-ranking${queryString}`,
    );
    return extractList<TeamRanking>(response);
  },

  // 3. LẤY XẾP HẠNG THÁNG
  getMonthRanking: async (
    classId: string,
    startDate?: string,
    endDate?: string,
  ): Promise<TeamRanking[]> => {
    const query = new URLSearchParams();
    if (startDate && endDate) {
      query.append("startAt", startDate);
      query.append("endAt", endDate);
    }
    query.append("_t", new Date().getTime().toString()); // Phá cache

    const response = await apiClient.get(
      `/classes/${classId}/points/month-ranking?${query.toString()}`,
    );
    return extractList<TeamRanking>(response);
  },

  // 4. GHI ĐIỂM
  addPoints: async (
    classId: string,
    groupId: number,
    content: string,
    points: number,
  ): Promise<GenericResponse> => {
    const response = await apiClient.post(
      `/classes/${classId}/points/groups/${groupId}`,
      {
        description: content,
        point: points,
      },
    );
    return extractData<GenericResponse>(response);
  },

  // 5. XÓA ĐIỂM
  deletePointLog: async (
    classId: string,
    pointId: number,
  ): Promise<GenericResponse> => {
    const response = await apiClient.delete(
      `/classes/${classId}/points/${pointId}`,
    );
    return extractData<GenericResponse>(response);
  },

  // 6. LẤY LỊCH SỬ CỦA 1 TỔ
  getHistoryByGroup: async (
    classId: string,
    groupId: number,
    startDate?: string,
    endDate?: string,
  ): Promise<CompetitionHistory[]> => {
    const query = new URLSearchParams();
    if (startDate && endDate) {
      query.append("start_at", startDate);
      query.append("end_at", endDate);
    }
    const queryString = query.toString() ? `?${query.toString()}` : "";
    
    const response = await apiClient.get(
      `/classes/${classId}/points/groups/${groupId}${queryString}`,
    );
    return extractList<CompetitionHistory>(response);
  },

  // 7. LẤY DANH SÁCH TUẦN
  getWeeks: async (year: number): Promise<WeekItem[]> => {
    const response = await apiClient.get(`/public/weeks?year=${year}`);
    return extractList<WeekItem>(response);
  },

  // 8. TẠO TỔ MỚI
  createGroup: async (classId: string, name: string): Promise<GroupItem> => {
    const response = await apiClient.post(
      `/classes/${classId}/groups`,
      { name }, 
    );
    return extractData<GroupItem>(response);
  },

  // 9. CẬP NHẬT TÊN TỔ
  updateGroup: async (
    classId: string,
    groupId: number,
    name: string,
  ): Promise<GenericResponse> => {
    const response = await apiClient.patch(
      `/classes/${classId}/groups/${groupId}`,
      { name },
    );
    return extractData<GenericResponse>(response);
  },

  // 10. XÓA TỔ
  deleteGroup: async (
    classId: string,
    groupId: number,
  ): Promise<GenericResponse> => {
    const response = await apiClient.delete(
      `/classes/${classId}/groups/${groupId}`,
    );
    return extractData<GenericResponse>(response);
  },

  // 11. LẤY DANH SÁCH TỔ (GROUP)
  getGroups: async (classId: string): Promise<GroupItem[]> => {
    const response = await apiClient.get(
      `/classes/${classId}/groups?_t=${new Date().getTime()}`,
    );
    return extractList<GroupItem>(response);
  },

  // 12. LẤY DANH SÁCH THÀNH VIÊN TRONG TỔ
  getGroupMembers: async (
    classId: string,
    groupId: number,
  ): Promise<GroupMember[]> => {
    const response = await apiClient.get(
      `/classes/${classId}/groups/${groupId}/members`,
    );
    return extractList<GroupMember>(response);
  },

  // 13. THÊM THÀNH VIÊN VÀO TỔ
  addGroupMember: async (
    classId: string,
    groupId: number,
    userId: string,
  ): Promise<GenericResponse> => {
    const response = await apiClient.post(
      `/classes/${classId}/groups/${groupId}/members`,
      { user_id: parseInt(userId) },
    );
    return extractData<GenericResponse>(response);
  },

  // 14. XÓA THÀNH VIÊN KHỎI TỔ
  removeGroupMember: async (
    classId: string,
    groupId: number,
    userId: string,
  ): Promise<GenericResponse> => {
    const response = await apiClient.delete(
      `/classes/${classId}/groups/${groupId}/members/${userId}`,
    );
    return extractData<GenericResponse>(response);
  },

  // 15. LẤY DANH SÁCH HỌC SINH CHƯA CÓ TỔ
  getUngroupedMembers: async (classId: string): Promise<GroupMember[]> => {
    try {
      const response = await apiClient.get(
        `/classes/${classId}/members/ungrouped`,
      );
      return extractList<GroupMember>(response);
    } catch (error) {
      console.error("Lỗi lấy danh sách chưa có tổ:", error);
      return []; 
    }
  },
};