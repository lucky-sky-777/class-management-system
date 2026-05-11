import type {
  ClassDiagramData,
  AttendanceStatus,
  StudentSeat
} from "@features/classDiagram/types";
import { homeAPI } from "@features/home/api";
import { apiClient } from "@services/api-client";

export const classDiagramAPI = {
  getDiagram: async (classId: string): Promise<ClassDiagramData> => {
    try {
      const response: any = await apiClient.get(`/seats/classes/${classId}`);
      
      const backendData = response.data?.data || response.data; 

      const seats: StudentSeat[] = [];
      let totalStudents = 0;
      let presentCount = 0;

      if (backendData && backendData.groups) {
        backendData.groups.forEach((groupObj: any) => {
          const groupId = Object.keys(groupObj)[0];
          const groupData = groupObj[groupId];
          const groupNum = parseInt(groupId);

          groupData.desks.forEach((deskObj: any) => {
            const deskId = Object.keys(deskObj)[0];
            const deskPositions = deskObj[deskId].desk_positions;
            const deskNum = parseInt(deskId);

            deskPositions.forEach((posObj: any) => {
              const posId = Object.keys(posObj)[0];
              const studentData = posObj[posId];
              const posNum = parseInt(posId);

              // BỘ DỊCH TỌA ĐỘ
              const side: "left" | "right" = groupNum <= 2 ? "left" : "right";
              let col = 1;
              
              if (side === "left") {
                if (groupNum === 1) col = posNum === 1 ? 1 : 2;
                if (groupNum === 2) col = posNum === 1 ? 3 : 4;
              } else {
                if (groupNum === 3) col = posNum === 1 ? 1 : 2;
                if (groupNum === 4) col = posNum === 1 ? 3 : 4;
              }

              if (studentData) {
                totalStudents++;
                presentCount++;
                
                seats.push({
                  id: String(studentData.user_id),
                  name: studentData.user_display_name,
                  row: deskNum,
                  column: col,
                  side: side,
                  status: "present", 
                });
              }
            });
          });
        });
      }

      return {
        totalStudents,
        presentCount,
        excusedCount: 0,
        unexcusedCount: 0,
        seats,
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
    row: number,
    col: number,
    side: "left" | "right",
    classId?: string 
  ) => {
    let group_id = 1;
    let position_id = 1;

    if (side === "left") {
      if (col === 1) { group_id = 1; position_id = 1; }
      if (col === 2) { group_id = 1; position_id = 2; }
      if (col === 3) { group_id = 2; position_id = 1; }
      if (col === 4) { group_id = 2; position_id = 2; }
    } else { 
      if (col === 1) { group_id = 3; position_id = 1; }
      if (col === 2) { group_id = 3; position_id = 2; }
      if (col === 3) { group_id = 4; position_id = 1; }
      if (col === 4) { group_id = 4; position_id = 2; }
    }
    const desk_id = row;

    // LƯU Ý: Vì studentId đã được truyền trên URL, có thể BE không cần user_id trong payload nữa
    // (Tùy thuộc vào DTO UpdateGroupUserSeatRequestDto của BE)
    const payload = {
      group_id: group_id,
      desk_id: desk_id,
      position_id: position_id
    };

    console.log(`Bắn dữ liệu xếp chỗ SV ${studentId} lên lớp ${classId}:`, payload);

    try {
      const response: any = await apiClient.patch(`/seats/classes/${classId}/${studentId}`, payload);
      return response.data;
    } catch (error) {
      console.error("Lỗi xếp chỗ:", error);
      throw error;
    }
  },
 
  getMembers: async (classId: string): Promise<{ id: string; name: string }[]> => {
    try {
      const res = await homeAPI.getClassMembers(Number(classId));
      
      if (res.success) {
        return res.data.map((m) => ({
          id: String(m.user_id),                   // Lấy ID học sinh
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
