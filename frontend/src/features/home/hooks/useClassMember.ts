// src/features/home/hooks/useClassMembers.ts
import { useState, useEffect, useCallback } from "react";
import { homeAPI } from "@features/home/api";
import type { ClassMember } from "@features/home/types";

export const useClassMembers = (classId: number | null) => {
  const [members, setMembers] = useState<ClassMember[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  // Khóa hàm fetchMembers bằng useCallback để tránh re-create hàm vô nghĩa
  const fetchMembers = useCallback(async () => {
    if (!classId) return;
    try {
      setIsLoading(true);
      const res = await homeAPI.getClassMembers(classId);
      if (res.success) {
        setMembers(res.data);
      }
    } catch (err) {
      console.error("Lỗi lấy thành viên:", err);
    } finally {
      setIsLoading(false);
    }
  }, [classId]);

  useEffect(() => {
    fetchMembers();
  }, [fetchMembers]); // Gọi lại chuẩn xác theo tham chiếu hàm bảo vệ

  return { members, isLoading, refreshMembers: fetchMembers };
};