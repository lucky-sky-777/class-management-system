// src/features/home/hooks/useHome.ts
import { useState, useEffect, useCallback } from "react";
import { homeAPI } from "@features/home/api";
import { useClassStore } from "@app/store";
import type { ClassResponse, JoinClassResult } from "@features/home/types";
import { useAuth } from "@features/auth";
import { ClassPrivacy } from "@shared/domain/enums";

export const useHome = () => {
  const { user, isAuthenticated } = useAuth();

  // Lôi trực tiếp State dùng chung từ Zustand ra
  const {
    classes,
    isLoading,
    error,
    fetchClasses,
    forceRefresh,
    clearClasses,
  } = useClassStore();

  const [isCreating, setIsCreating] = useState(false);
  const [isJoining, setIsJoining] = useState(false);

  // TỰ ĐỘNG TRIGGER FETCH CHUẨN ZUSTAND:
  // Tất cả component Header, Sidebar, HomePage đều dùng chung 1 State nên sẽ đồng loạt nhận được data!
  useEffect(() => {
    if (!isAuthenticated || !user || !user.id) {
      clearClasses();
      return;
    }

    fetchClasses(user.id, isAuthenticated);
  }, [isAuthenticated, user, fetchClasses, clearClasses]);

  // Hàm ép đồng bộ thủ công khi có biến động
  const loadData = useCallback(async () => {
    if (user?.id) {
      await forceRefresh(isAuthenticated, user.id);
    }
  }, [isAuthenticated, user?.id, forceRefresh]);

  const createClassMutation = async (formData: {
    className: string;
    description: string;
    status: string;
  }) => {
    try {
      setIsCreating(true);
      const payload: Omit<ClassResponse, "id"> = {
        name: formData.className,
        description: formData.description,
        privacy: formData.status as ClassPrivacy,
        owner_username: user?.username || "alice",
        avatar_url: "",
      };
      const res = await homeAPI.createClass(payload);

      if (res.success) {
        window.dispatchEvent(new Event("refreshHomeClasses"));
      } else {
        throw new Error(res.message || "Tạo lớp thất bại");
      }
    } catch (err) {
      console.error("Lỗi khi tạo lớp:", err);
      throw err;
    } finally {
      setIsCreating(false);
    }
  };

  const joinClassMutation = async (
    code: string,
  ): Promise<JoinClassResult | undefined> => {
    try {
      setIsJoining(true);
      const res = await homeAPI.joinClass(code);

      if (res.success) {
        window.dispatchEvent(new Event("refreshHomeClasses"));
        return res.data as JoinClassResult;
      } else {
        throw new Error(res.message || "Không thể tham gia lớp học");
      }
    } catch (err: unknown) {
      console.error("Lỗi khi tham gia lớp:", err);
      throw err;
    } finally {
      setIsJoining(false);
    }
  };

  const deleteClassMutation = async (classId: number) => {
    try {
      const res = await homeAPI.deleteClass(classId);
      if (res.success) {
        window.dispatchEvent(new Event("refreshHomeClasses"));
      } else {
        throw new Error(res.message || "Không thể xóa lớp");
      }
    } catch (err: unknown) {
      console.error("Lỗi khi xóa lớp:", err);
      throw err;
    }
  };

  const leaveClassMutation = async (classId: number) => {
    try {
      const res = await homeAPI.leaveClass(classId);
      if (res.success) {
        window.dispatchEvent(new Event("refreshHomeClasses"));
      } else {
        throw new Error(res.message || "Không thể rời lớp");
      }
    } catch (err: unknown) {
      console.error("Lỗi khi rời lớp:", err);
      throw err;
    }
  };

  const updateClassMutation = async (
    classId: number,
    updateData: Partial<ClassResponse>,
  ) => {
    try {
      const res = await homeAPI.updateClass(classId, updateData);
      if (res.success) {
        window.dispatchEvent(new Event("refreshHomeClasses"));
      } else {
        throw new Error(res.message || "Cập nhật thất bại");
      }
    } catch (err: unknown) {
      console.error("Lỗi khi cập nhật lớp:", err);
      throw err;
    }
  };

  return {
    classes,
    isLoading,
    isCreating,
    isJoining,
    error,
    refresh: loadData,
    createClassMutation,
    joinClassMutation,
    deleteClassMutation,
    leaveClassMutation,
    updateClassMutation,
  };
};
