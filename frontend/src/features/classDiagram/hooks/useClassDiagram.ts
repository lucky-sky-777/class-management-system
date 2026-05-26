// src/features/classDiagram/hooks/useClassDiagram.ts
import { useState, useEffect, useCallback } from "react";
import { classDiagramAPI } from "@features/classDiagram/api";
import type {
  ClassDiagramData,
  AttendanceStatus,
} from "@features/classDiagram/types";

export const useClassDiagram = (
  classId: string,
  mode: "view" | "attendance" | "setup",
) => {
  const [data, setData] = useState<ClassDiagramData | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [unseatedMembers, setUnseatedMembers] = useState<
    { id: string; name: string }[]
  >([]);

  const fetchDiagram = useCallback(
    async (showLoading = false) => {
      if (!classId) return;
      if (showLoading) setIsLoading(true);
      try {
        const result = await classDiagramAPI.getDiagram(classId);
        setData(result);
      } catch (error) {
        console.error("Lỗi tải sơ đồ:", error);
      } finally {
        setIsLoading(false);
      }
    },
    [classId],
  );

  const fetchUnseatedMembers = useCallback(async () => {
    if (!classId) return;
    try {
      const memberList = await classDiagramAPI.getMembers(classId);
      setUnseatedMembers(memberList);
    } catch (error) {
      console.error("Lỗi tải danh sách chưa có chỗ:", error);
    }
  }, [classId]);

  useEffect(() => {
    if (mode === "setup" && classId) fetchUnseatedMembers();
    else setUnseatedMembers([]);
  }, [mode, classId, fetchUnseatedMembers]);

  useEffect(() => {
    fetchDiagram(true); // Chỉ hiện loading ở lần load đầu tiên
  }, [fetchDiagram]);

  const assignOrUpdateSeat = async (
    studentId: string,
    targetGroupId: number,
    targetDeskId: number,
    targetPositionId: number,
    sourceGroupId: number | null,
    sourceDeskId: number | null,
    sourcePositionId: number | null,
  ) => {
    try {
      if (
        sourceGroupId !== null &&
        sourceDeskId !== null &&
        sourcePositionId !== null
      ) {
        await classDiagramAPI.updateSeat(
          studentId,
          targetGroupId,
          targetDeskId,
          targetPositionId,
          classId,
          sourceGroupId,
          sourceDeskId,
          sourcePositionId,
        );
      } else {
        await classDiagramAPI.assignSeat(
          studentId,
          targetGroupId,
          targetDeskId,
          targetPositionId,
          classId,
          null,
          null,
          null,
        );
      }
      // Không hiện loading, chỉ cập nhật data
      await fetchDiagram(false);
      if (mode === "setup") await fetchUnseatedMembers();
      return true;
    } catch (error) {
      console.error("Lỗi xếp/đổi chỗ:", error);
      return false;
    }
  };

  // Tương tự, hàm markAttendance và shuffleDiagram cũng nên dùng fetchDiagram(false)
  const markAttendance = async (
    groupId: number,
    studentId: string,
    currentStatus: string,
  ) => {
    const nextStatus: Record<string, AttendanceStatus> = {
      present: "absent_excused",
      absent_excused: "absent_unexcused",
      absent_unexcused: "present",
    };
    try {
      await classDiagramAPI.updateAttendance(
        classId,
        groupId,
        studentId,
        nextStatus[currentStatus] || "present",
      );
      await fetchDiagram(false);
    } catch (error) {
      console.error("Lỗi điểm danh:", error);
    }
  };

  const shuffleDiagram = async () => {
    try {
      await classDiagramAPI.shuffleSeats(classId);

      // Load lại dữ liệu ngầm mà không làm UI bị giật
      await fetchDiagram(false);
      await fetchUnseatedMembers();
    } catch (error) {
      console.error("Xếp tự động thất bại:", error);
    }
  };

  return {
    data,
    isLoading,
    unseatedMembers,
    refresh: fetchDiagram,
    shuffle: shuffleDiagram,
    assignSeat: assignOrUpdateSeat,
    markAttendance,
  };
};
