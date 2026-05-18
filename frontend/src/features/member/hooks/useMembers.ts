// src/features/member/hooks/useMembers.ts
import { useState, useEffect, useCallback, useRef } from "react";
import { memberAPI } from "@features/member/api";
import { apiClient } from "@services/api-client";
import type { Member, ClassInfo } from "@features/member/types";

export const useMembers = (
  classId: string,
  currentUserId?: number | string,
) => {
  const [members, setMembers] = useState<Member[]>([]);
  const [classInfo, setClassInfo] = useState<ClassInfo | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const fetchTracker = useRef<string | null>(null);

  const fetchData = useCallback(
    async (silent = false) => {
      if (!classId) return;

      try {
        if (!silent) setIsLoading(true);

        // Bước 1: Gọi song song 2 API cơ bản lấy thông tin lớp và thành viên chính thức
        const [classesRes, officialData] = await Promise.all([
          apiClient.get("/classes") as Promise<{
            data?: ClassInfo[] | { data?: ClassInfo[] };
          }>,
          memberAPI.getMembers(classId),
        ]);

        // Xử lý thông tin lớp học (Class Info)
        const responseData = classesRes.data;
        const classes =
          (Array.isArray(responseData) ? responseData : responseData?.data) ||
          [];
        const foundClass = classes.find(
          (c) => String(c.id) === String(classId),
        );
        if (foundClass) setClassInfo(foundClass);

        // Định danh vai trò của user hiện tại từ danh sách chính thức
        const currentUserInOfficial = officialData.find(
          (m) => String(m.userId) === String(currentUserId),
        );

        const isAdminOrOwner =
          currentUserInOfficial?.role === "OWNER" ||
          currentUserInOfficial?.role === "CLASS_ADMIN";

        let allMembers = [...officialData];

        // Bước 2: Chỉ gọi API pending nếu thực sự có quyền Admin/Owner
        if (isAdminOrOwner) {
          try {
            const pendingData = await memberAPI.getPendingRequests(classId);
            allMembers = [...allMembers, ...pendingData];
          } catch (pendingError) {
            console.error("Lỗi khi tải danh sách chờ duyệt:", pendingError);
          }
        }
        setMembers(allMembers);
      } catch (error) {
        console.error("Lỗi tải dữ liệu trang thành viên:", error);
      } finally {
        setIsLoading(false);
      }
    },
    [classId, currentUserId],
  );

  useEffect(() => {
    // Tạo mã phiên độc nhất dựa trên classId và currentUserId hiện tại
    const currentFetchKey = `${classId}-${currentUserId || "guest"}`;

    // Nếu phiên này đã được gọi chạy thành công rồi -> CẤM TUYỆT ĐỐI không gọi lại nữa
    if (fetchTracker.current === currentFetchKey) return;

    // Sập khóa lại ngay lập tức trước khi luồng bất đồng bộ kịp chạy
    fetchTracker.current = currentFetchKey;

    fetchData();
  }, [classId, currentUserId, fetchData]);

  // Hàm ép đồng bộ thủ công dành riêng cho các nút bấm tương tác (Duyệt, từ chối, kick...)
  const handleRefresh = useCallback(
    async (silent?: boolean) => {
      // Khi chủ động click refresh từ nút bấm, ta cho phép đục thủng Tracker để lấy data mới
      await fetchData(silent);
    },
    [fetchData],
  );

  const currentUser = members.find(
    (m) => String(m.userId) === String(currentUserId),
  );

  return {
    classInfo,
    members,
    isLoading,
    myRole: currentUser?.role || "CLASS_MEMBER",
    refresh: handleRefresh, // Trả về hàm refresh an toàn đã được bọc lại
  };
};
