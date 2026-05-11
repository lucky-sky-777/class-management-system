import type {
  ClassDiagramData,
  AttendanceStatus,
  GroupData,
  DeskData,
  PositionData,
} from "@features/classDiagram/types";
import { homeAPI } from "@features/home/api";
import { apiClient } from "@services/api-client";

export const classDiagramAPI = {
  getDiagram: async (classId: string): Promise<ClassDiagramData> => {
    try {
      const response: any = await apiClient.get(`/seats/classes/${classId}`);
      const backendData = response.data?.data || response.data;

      const groups: GroupData[] = [];
      let totalStudents = 0;
      let presentCount = 0;

      if (backendData && backendData.groups) {
        backendData.groups.forEach((groupObj: any) => {
          const groupIdStr = Object.keys(groupObj)[0];
          const groupDataBE = groupObj[groupIdStr];
          const groupId = parseInt(groupIdStr);

          const desks: DeskData[] = [];

          groupDataBE.desks.forEach((deskObj: any) => {
            const deskIdStr = Object.keys(deskObj)[0];
            const deskPositionsBE = deskObj[deskIdStr].desk_positions;
            const deskId = parseInt(deskIdStr);

            const positions: PositionData[] = [];

            deskPositionsBE.forEach((posObj: any) => {
              const posIdStr = Object.keys(posObj)[0];
              const studentData = posObj[posIdStr];
              const positionId = parseInt(posIdStr);

              let student = null;
              if (studentData) {
                totalStudents++;
                presentCount++; // Giả sử mặc định là present
                student = {
                  id: String(studentData.user_id),
                  name: studentData.user_display_name,
                  avatarUrl: studentData.avatar_url || null, // Nếu BE có trả về
                  status: "present" as AttendanceStatus,
                  groupId,
                  deskId,
                  positionId,
                };
              }
              positions.push({ positionId, student });
            });
            desks.push({ deskId, positions });
          });
          groups.push({ groupId, desks });
        });
      }

      return {
        totalStudents,
        presentCount,
        excusedCount: 0,
        unexcusedCount: 0,
        groups,
      };
    } catch (error) {
      console.error("Lỗi lấy sơ đồ lớp:", error);
      throw error;
    }
  },
  // 2. GỌI API ĐIỂM DANH
  updateAttendance: async (studentId: string, status: AttendanceStatus) => {
    console.log(`API: Cập nhật SV ${studentId} sang trạng thái ${status}`);

    // KHI NÀO NỐI API THÌ MỞ COMMENT RA:
    // return await apiClient.put(`/attendance/${studentId}`, { status });

    return new Promise((resolve) => setTimeout(resolve, 300));
  },

  assignSeat: async (
    studentId: string,
    targetGroupId: number, // ĐỔI PARAM: Nhận thẳng tọa độ Backend
    targetDeskId: number,
    targetPositionId: number,
    classId: string,
    sourceGroupId: number | null, // Nhận tọa độ cũ nếu có
    sourceDeskId: number | null,
    sourcePositionId: number | null,
  ) => {
    const payload = {
      source_group_id: sourceGroupId,
      source_desk: sourceDeskId,
      source_desk_position: sourcePositionId,
      target_group_id: targetGroupId,
      target_desk: targetDeskId,
      target_desk_position: targetPositionId,
    };

    console.log(`Bắn dữ liệu xếp chỗ SV ${studentId}:`, payload);
    const response: any = await apiClient.patch(
      `/seats/classes/${classId}/${studentId}`,
      payload,
    );
    return response.data;
  },

  // Thêm vào file api.ts
  shuffleSeats: async (classId: string) => {
    try {
      // Nhớ dùng apiClient.get nhé! Dùng post là nó báo lỗi 405 Method Not Allowed đó
      const response: any = await apiClient.get(
        `/seats/classes/${classId}/shuffle`,
      );
      return response.data;
    } catch (error) {
      console.error("Lỗi xếp chỗ tự động:", error);
      throw error;
    }
  },

  getMembers: async (
    classId: string,
  ): Promise<{ id: string; name: string }[]> => {
    try {
      const res = await homeAPI.getClassMembers(Number(classId));

      if (res.success) {
        return res.data.map((m) => ({
          id: String(m.user_id), // Lấy ID học sinh
          name: m.user_display_name || "Vô danh", // Lấy tên học sinh
        }));
      }
      return [];
    } catch (error) {
      console.error("Lỗi lấy thành viên:", error);
      return [];
    }
  },
};
