// src/features/classDiagram/api.ts
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
      let excusedCount = 0;
      let unexcusedCount = 0;
      let lateCount = 0;

      if (backendData && backendData.groups) {
        backendData.groups.forEach((groupObj: any) => {
          const groupIdStr = Object.keys(groupObj)[0];
          const groupDataBE = groupObj[groupIdStr];
          const groupId = parseInt(groupIdStr);

          const desks: DeskData[] = [];

          if (groupDataBE.desks) {
            groupDataBE.desks.forEach((deskObj: any) => {
              const deskIdStr = Object.keys(deskObj)[0];
              const deskPositionsBE = deskObj[deskIdStr].desk_positions;
              const deskId = parseInt(deskIdStr);

              const positions: PositionData[] = [];

              if (deskPositionsBE) {
                deskPositionsBE.forEach((posObj: any) => {
                  const posIdStr = Object.keys(posObj)[0];
                  const studentData = posObj[posIdStr];
                  const positionId = parseInt(posIdStr);

                  let student = null;
                  if (studentData && studentData.user_id) {
                    
                    // 1. CHUYỂN ĐỔI TRẠNG THÁI TỪ BACKEND SANG FRONTEND
                    const beStatus = studentData.attendance_status || "PRESENT";
                    const statusMapUI: Record<string, AttendanceStatus> = {
                      PRESENT: "present",
                      ABSENT_EXCUSED: "absent_excused",
                      ABSENT_UNEXCUSED: "absent_unexcused",
                      LATE: "late",
                    };
                    const currentStatus = statusMapUI[beStatus] || "present";

                    // 2. ĐẾM SỐ LƯỢNG CHO BẢNG THỐNG KÊ
                    totalStudents++;
                    if (currentStatus === "present") presentCount++;
                    else if (currentStatus === "absent_excused") excusedCount++;
                    else if (currentStatus === "absent_unexcused") unexcusedCount++;
                    else if (currentStatus === "late") lateCount++;

                    // 3. RÁP DỮ LIỆU VÀO GHẾ
                    student = {
                      id: String(studentData.user_id),
                      name: studentData.user_display_name || "Vô danh",
                      avatarUrl: studentData.avatar_url || null,
                      status: currentStatus, // <-- ĐÃ SỬA THÀNH BIẾN ĐỘNG
                      groupId,
                      deskId,
                      positionId,
                    };
                  }
                  positions.push({ positionId, student });
                });
              }
              desks.push({ deskId, positions });
            });
          }
          groups.push({ groupId, desks });
        });
      }

      return {
        totalStudents,
        presentCount,
        excusedCount,
        unexcusedCount,
        lateCount,
        groups,
      };
    } catch (error) {
      console.error("Lỗi lấy sơ đồ lớp:", error);
      throw error;
    }
  },

  // 2. GỌI API ĐIỂM DANH
  updateAttendance: async (
    classId: string,
    groupId: number,
    studentId: string,
    status: AttendanceStatus,
  ) => {
    // 1. Map trạng thái của Frontend (chữ thường) sang Enum của Backend (CHỮ IN HOA)
    const statusMap: Record<string, string> = {
      present: "PRESENT",
      absent_excused: "ABSENT_EXCUSED",
      absent_unexcused: "ABSENT_UNEXCUSED",
      late: "LATE",
    };

    const payload = {
      user_id: parseInt(studentId),
      attendance_status: statusMap[status] || "PRESENT", // Mặc định nếu lỗi thì là PRESENT
    };

    try {
      // 2. Gọi POST API theo chuẩn tài liệu Hào đưa
      const response: any = await apiClient.post(
        `/attendances/classes/${classId}/groups/${groupId}`,
        payload,
      );
      return response.data;
    } catch (error) {
      console.error("Lỗi điểm danh:", error);
      throw error;
    }
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
      user_id: parseInt(studentId),
      source_group_id: sourceGroupId,
      source_desk: sourceDeskId,
      source_desk_position: sourcePositionId,
      target_group_id: targetGroupId,
      target_desk: targetDeskId,
      target_desk_position: targetPositionId,
    };

    const response: any = await apiClient.patch(
      `/seats/classes/${classId}`,
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