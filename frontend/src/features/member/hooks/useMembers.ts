import { useState, useEffect, useCallback } from "react";
import { memberAPI } from "@features/member/api";
import type { Member } from "@features/member/types";


export const useMembers = (classId: string, currentUserId?: number | string, ownerId?: number | string) => {
  const [members, setMembers] = useState<Member[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  const fetchMembers = useCallback(async () => {
    // Chỉ fetch khi có classId. Nếu chưa có ownerId cũng cứ fetch, 
    // ta sẽ map lại role sau ở useEffect bên dưới.
    if (!classId) return; 

    try {
      setIsLoading(true);
      const data = await memberAPI.getMembers(classId);
      
      const mapped = data.map(m => ({
        ...m,
        role: (ownerId && String(m.userId) === String(ownerId)) ? "OWNER" : m.role
      }));
      
      setMembers(mapped);
    } catch (error) {
      console.error("Lỗi tải thành viên:", error);
    } finally {
      setIsLoading(false);
    }
  }, [classId, ownerId]);

  useEffect(() => {
    fetchMembers();
  }, [fetchMembers]);

  // Cập nhật lại role OWNER ngay khi ownerId thay đổi (khi fetchClassInfo xong)
  useEffect(() => {
    if (members.length > 0 && ownerId) {
      setMembers(prev => prev.map(m => ({
        ...m,
        role: String(m.userId) === String(ownerId) ? "OWNER" : m.role
      })));
    }
  }, [ownerId]);

  const currentUser = members.find(m => String(m.userId) === String(currentUserId));
  const myRole = currentUser?.role || "CLASS_MEMBER";

  return { members, isLoading, myRole, refresh: fetchMembers };
};