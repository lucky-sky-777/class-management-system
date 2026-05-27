import { useState, useEffect, useCallback } from "react";
import { emulationAPI } from "@features/emulation/api";

import type { 
  CompetitionData, 
  WeekItem, 
  GroupItem, 
  GroupMember,
  CompetitionHistory,
  TeamRanking
} from "@features/emulation/types";

// Type cục bộ map dữ liệu thô từ API trả về
type RawHistoryItem = {
  id?: string | number;
  created_at?: string;
  description?: string;
  point?: number;
  group_id?: number;
  actor_display_name?: string;
  actor_avatar_url?: string;
};

type RawRankItem = {
  rank: number;
  group_id?: number;
  group_name?: string;
  total_point?: number;
};

export const useEmulation = (classId: string) => {
  const [data, setData] = useState<CompetitionData | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [weeks, setWeeks] = useState<WeekItem[]>([]);
  const [groups, setGroups] = useState<GroupItem[]>([]);

  const [filters, setFilters] = useState({
    year: new Date().getFullYear(),
    startDate: "",
    endDate: "",
  });

  const loadWeeks = useCallback(async () => {
    try {
      const currentYear = filters.year || new Date().getFullYear();
      const weeksData = await emulationAPI.getWeeks(currentYear);
      setWeeks(weeksData);

      if (weeksData && weeksData.length > 0) {
        const currentWeek = weeksData.find((w: WeekItem) => w.is_current_week);
        if (currentWeek) {
          setFilters((prev) => ({
            ...prev,
            startDate: currentWeek.start_at,
            endDate: currentWeek.end_at,
          }));
        } else {
          setFilters((prev) => ({
            ...prev,
            startDate: weeksData[0].start_at,
            endDate: weeksData[0].end_at,
          }));
        }
      } else {
        setData({
          teamCount: 4,
          teams: {},
          history: [],
          weeklyRanking: [],
          monthlyRanking: [],
        });
        setIsLoading(false);
      }
    } catch (error) {
      console.error("Lỗi lấy danh sách tuần:", error);
      setIsLoading(false);
    }
  }, [filters.year]);

  const loadData = useCallback(
    async (silent = false) => {
      if (!classId || !filters.startDate) return;
      try {
        if (!silent) setIsLoading(true);

        const [historyRes, weekRankRes, monthRankRes, groupRes] = await Promise.all([
          emulationAPI.getHistoryByClass(classId, filters.startDate, filters.endDate),
          emulationAPI.getWeekRanking(classId, filters.startDate, filters.endDate),
          emulationAPI.getMonthRanking(classId, filters.startDate, filters.endDate),
          emulationAPI.getGroups(classId),
        ]);

        setGroups(groupRes);

        // Ép kiểu ngược lại thành Raw Item để Map dữ liệu an toàn
        const history: CompetitionHistory[] = (historyRes as unknown as RawHistoryItem[]).map((item) => ({
          id: item.id?.toString() || Math.random().toString(),
          date: item.created_at || "Vừa xong",
          content: item.description || "Chưa có nội dung",
          points: item.point || 0,
          teamId: item.group_id || 1,
          actor: item.actor_display_name || "Giáo viên",
          actor_avatar_url: item.actor_avatar_url || null,
        }));

        const weeklyRanking: TeamRanking[] = (weekRankRes as unknown as RawRankItem[]).map((item) => ({
          rank: item.rank,
          teamId: item.group_id || (item.group_name ? parseInt(item.group_name.replace(/\D/g, "")) : 1),
          points: item.total_point || 0,
        }));

        const monthlyRanking: TeamRanking[] = (monthRankRes as unknown as RawRankItem[]).map((item) => ({
          rank: item.rank,
          teamId: item.group_id || (item.group_name ? parseInt(item.group_name.replace(/\D/g, "")) : 1),
          points: item.total_point || 0,
        }));

        setData({
          teamCount: Math.max(weeklyRanking.length, 4),
          teams: {},
          history,
          weeklyRanking,
          monthlyRanking,
        });
      } catch (error) {
        console.error("Lỗi khi gọi API Thi đua:", error);
      } finally {
        setIsLoading(false);
      }
    },
    [classId, filters.startDate, filters.endDate],
  );

  const addPoint = async (groupId: number, content: string, points: number) => {
    try {
      const res = await emulationAPI.addPoints(classId, groupId, content, points);
      
      const isSuccess = res && (res.success || res.code === 200 || res.id);

      if (isSuccess) {
        await new Promise(resolve => setTimeout(resolve, 200)); 
        await loadData(true);
        return { success: true };
      }
      
      console.error("API báo lỗi hoặc thiếu dữ liệu:", res);
      return { success: false };
    } catch (error) {
      console.error("Lỗi ghi điểm:", error);
      return { success: false };
    }
  };

  const deletePoint = async (pointId: number) => {
    try {
      await emulationAPI.deletePointLog(classId, pointId);
      await loadData(true);
    } catch (error) {
      console.error("Lỗi xóa tổ:", error);
    }
  };

  const addGroup = async () => {
    try {
      const nextNumber = groups.length + 1;
      const groupName = `Tổ ${nextNumber}`;
      
      const res = await emulationAPI.createGroup(classId, groupName);
      
      if (res) {
        await new Promise(resolve => setTimeout(resolve, 300)); 
        await loadData(true); 
      }
    } catch (error) {
      console.error("Lỗi thêm tổ:", error);
    }
  };

  const editGroup = async (groupId: number, newName: string) => {
    if (!newName.trim()) return false;
    try {
      const res = await emulationAPI.updateGroup(classId, groupId, newName.trim());
      // Check luôn res thẳng vì đã xóa vỏ .data
      if (res) {
        await new Promise(resolve => setTimeout(resolve, 300));
        await loadData(true);
        return true; 
      }
    } catch (error) {
      console.error("Lỗi sửa tên tổ:", error);
    }
    return false;
  };

  const removeGroup = async (groupId: number) => {
    try {
      const res = await emulationAPI.deleteGroup(classId, groupId);
      if (res) {
        await new Promise(resolve => setTimeout(resolve, 300));
        await loadData(true);
        return true;
      }
    } catch (error) {
      console.error("Lỗi xóa tổ:", error);
    }
    return false;
  };

  const fetchGroupMembers = async (groupId: number): Promise<GroupMember[]> => {
    try {
      return await emulationAPI.getGroupMembers(classId, groupId);
    } catch (error) {
      console.error("Lỗi lấy danh sách thành viên tổ:", error);
      return [];
    }
  };

  const fetchUngroupedMembers = async (): Promise<GroupMember[]> => {
    try {
      return await emulationAPI.getUngroupedMembers(classId);
    } catch (error) {
      console.error("Lỗi lấy học sinh chưa có tổ:", error);
      return [];
    }
  };

  const addMemberToGroup = async (groupId: number, userId: string) => {
    try {
      await emulationAPI.addGroupMember(classId, groupId, userId);
      return true;
    } catch (error) {
      console.error("Lỗi thêm thành viên:", error);
      return false;
    }
  };

  const removeMemberFromGroup = async (groupId: number, userId: string) => {
    try {
      await emulationAPI.removeGroupMember(classId, groupId, userId);
      return true;
    } catch (error) {
      console.error("Lỗi xóa thành viên:", error);
      return false;
    }
  };

  // useEffect(() => {
  //   checkPermission();
  // }, [checkPermission]);

  useEffect(() => {
    loadWeeks();
  }, [loadWeeks]);

  useEffect(() => {
    if (filters.startDate) {
      loadData(true);
    }
  }, [loadData, filters.startDate]);

  return {
    data,
    groups,
    isLoading,
    filters,
    weeks,
    // canEdit,
    setFilters: (newFilters: Partial<typeof filters>) => {
      setFilters((prev) => ({ ...prev, ...newFilters }));
    },
    refresh: () => loadData(true),
    addPoint,
    deletePoint,
    addGroup,
    editGroup,
    removeGroup,
    fetchGroupMembers,
    fetchUngroupedMembers,
    addMemberToGroup,
    removeMemberFromGroup,
  };
};