import { useState, useEffect, useCallback } from "react";
import { memberAPI } from "@features/member/api";
import type { Member } from "@features/member/types";


// src/features/member/hooks/useMembers.ts

export const useMembers = (classId: string, currentUserId?: number | string) => {
  const [members, setMembers] = useState<Member[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  const fetchMembers = useCallback(async (silent = false) => {
    if (!classId) return;

    try {
      if (!silent) setIsLoading(true);

      // Gọi song song nhưng tách biệt
      const [officialData, pendingData] = await Promise.all([
        memberAPI.getMembers(classId),
        memberAPI.getPendingRequests(classId)
      ]);
      
      // Gộp lại để Page chỉ cần dùng 1 biến 'members' duy nhất
      setMembers([...officialData, ...pendingData]);
    } catch (error) {
      console.error("Lỗi tải thành viên:", error);
    } finally {
      setIsLoading(false);
    }
  }, [classId]);

  useEffect(() => {
    fetchMembers();
  }, [fetchMembers]);

  const currentUser = members.find(m => String(m.userId) === String(currentUserId));
  
  return { 
    members, 
    isLoading, 
    myRole: currentUser?.role || "CLASS_MEMBER", 
    refresh: (silent?: boolean) => fetchMembers(silent) 
  };
};