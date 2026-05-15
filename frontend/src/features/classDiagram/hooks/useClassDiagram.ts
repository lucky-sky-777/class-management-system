// src/features/classDiagram/hooks/useClassDiagram.ts
import { useState, useEffect, useCallback } from 'react';
import { classDiagramAPI } from '@features/classDiagram/api';
import { homeAPI } from '@features/home/api';
import { useAuth } from '@features/auth';
import { ClassRole } from '@shared/domain/enums';
import type { ClassDiagramData, AttendanceStatus } from '@features/classDiagram/types';

export const useClassDiagram = (classId: string, mode: "view" | "attendance" | "setup") => {
  const { user } = useAuth();
  const [data, setData] = useState<ClassDiagramData | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [canEdit, setCanEdit] = useState(false);
  const [unseatedMembers, setUnseatedMembers] = useState<{ id: string; name: string }[]>([]);

  // 1. TẢI SƠ ĐỒ LỚP
  const fetchDiagram = useCallback(async () => {
    if (!classId) return;
    try {
      setIsLoading(true);
      const result = await classDiagramAPI.getDiagram(classId);
      setData(result);
    } catch (error) {
      console.error("Lỗi tải sơ đồ:", error);
    } finally {
      setIsLoading(false);
    }
  }, [classId]);

  // 2. TẢI HỌC SINH CHƯA CÓ CHỖ
  const fetchUnseatedMembers = useCallback(async () => {
    if (!classId) return;
    try {
      const memberList = await classDiagramAPI.getMembers(classId);
      setUnseatedMembers(memberList);
    } catch (error) {
      console.error("Lỗi tải danh sách chưa có chỗ:", error);
    }
  }, [classId]);

  // EFFECT 1: Kiểm tra quyền Admin khi vào trang
  useEffect(() => {
    const checkPermission = async () => {
      if (!classId || !user?.id) return;
      try {
        const res = await homeAPI.getClassMembers(Number(classId));
        if (res.success) {
          const currentMember = res.data.find((m) => String(m.user_id) === String(user.id));
          setCanEdit(currentMember?.role === ClassRole.CLASS_ADMIN);
        }
      } catch (error) {
        console.error("Lỗi kiểm tra quyền:", error);
      }
    };
    checkPermission();
  }, [classId, user?.id]);

  // EFFECT 2: Tải danh sách chưa có chỗ NẾU đang ở chế độ "Xếp chỗ"
  useEffect(() => {
    if (mode === "setup" && classId) {
      fetchUnseatedMembers();
    } else {
      setUnseatedMembers([]); // Xóa list nếu thoát chế độ xếp chỗ
    }
  }, [mode, classId, fetchUnseatedMembers]);

  // EFFECT 3: Tải sơ đồ lần đầu
  useEffect(() => {
    fetchDiagram();
  }, [fetchDiagram]);

  // HÀM XẾP CHỖ / ĐỔI CHỖ
  const assignOrUpdateSeat = async (
    studentId: string, targetGroupId: number, targetDeskId: number, targetPositionId: number,
    sourceGroupId: number | null, sourceDeskId: number | null, sourcePositionId: number | null
  ) => {
    try {
      if (sourceGroupId !== null && sourceDeskId !== null && sourcePositionId !== null) {
        await classDiagramAPI.updateSeat(
          studentId, targetGroupId, targetDeskId, targetPositionId,
          classId, sourceGroupId, sourceDeskId, sourcePositionId
        );
      } else {
        await classDiagramAPI.assignSeat(
          studentId, targetGroupId, targetDeskId, targetPositionId,
          classId, null, null, null
        );
      }
      
      // Thành công thì tải lại cả Sơ đồ VÀ Danh sách khay chờ
      await fetchDiagram();
      if (mode === "setup") await fetchUnseatedMembers(); 
      return true; 
    } catch (error: any) {
      console.error("Lỗi xếp/đổi chỗ:", error);
      const backendMessage = error.response?.data?.message || "Xếp chỗ thất bại do lỗi không xác định!";
      alert(`❌ Lỗi Xếp Chỗ:\n${backendMessage}`);
      return false; 
    }
  };

  // HÀM ĐIỂM DANH
  const markAttendance = async (groupId: number, studentId: string, currentStatus: string) => {
    const nextStatus: Record<string, AttendanceStatus> = {
      present: "absent_excused",
      absent_excused: "absent_unexcused",
      absent_unexcused: "present",
    };
    try {
      await classDiagramAPI.updateAttendance(classId, groupId, studentId, nextStatus[currentStatus] || "present");
      await fetchDiagram();
    } catch (error: any) {
      console.error("Lỗi điểm danh:", error);
      alert(`❌ Điểm danh thất bại: ${error.response?.data?.message || "Lỗi máy chủ!"}`);
    }
  };

  // HÀM XẾP TỰ ĐỘNG
  const shuffleDiagram = async () => {
    try {
      setIsLoading(true);
      await classDiagramAPI.shuffleSeats(classId);
      await fetchDiagram(); 
      await fetchUnseatedMembers(); // Cập nhật lại khay chờ
    } catch (error) {
      alert("Xếp tự động thất bại!");
      setIsLoading(false);
    }
  };

  return { 
    data, isLoading, canEdit, unseatedMembers, // Xuất thêm state ra UI
    refresh: fetchDiagram, shuffle: shuffleDiagram, assignSeat: assignOrUpdateSeat, markAttendance 
  };
};