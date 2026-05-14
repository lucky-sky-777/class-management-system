import { useState, useEffect, useCallback } from "react";
import { emulationAPI } from "@features/emulation/api";
import type { CompetitionData } from "@features/emulation/types";

export const useEmulation = (classId: string) => {
  const [data, setData] = useState<CompetitionData | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [weeks, setWeeks] = useState<any[]>([]);
  const [groups, setGroups] = useState<any[]>([]);

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
        const currentWeek = weeksData.find((w: any) => w.is_current_week);
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

        
        const rawHistory = historyRes?.data || (Array.isArray(historyRes) ? historyRes : []);
        const rawWeekRank = weekRankRes?.data || (Array.isArray(weekRankRes) ? weekRankRes : []);
        const rawMonthRank = monthRankRes?.data || (Array.isArray(monthRankRes) ? monthRankRes : []);
        const rawGroups = groupRes?.data || (Array.isArray(groupRes) ? groupRes : []);
        setGroups(rawGroups);

        const history = rawHistory.map((item: any) => ({
          id: item.id?.toString() || Math.random().toString(),
          date: item.created_at || "Vừa xong",
          content: item.description || "Chưa có nội dung",
          points: item.point || 0,
          teamId: item.group_id || 1,
          actor: item.actor_display_name || "Giáo viên",
        }));

        const weeklyRanking = rawWeekRank.map((item: any) => ({
          rank: item.rank,
          teamId: item.group_id || (item.group_name ? parseInt(item.group_name.replace(/\D/g, "")) : 1),
          points: item.total_point || 0,
        }));

        const monthlyRanking = rawMonthRank.map((item: any) => ({
          rank: item.rank,
          teamId: item.group_id || (item.group_name ? parseInt(item.group_name.replace(/\D/g, "")) : 1),
          points: item.total_point || 0,
        }));

        setData({
          teamCount: Math.max(weeklyRanking.length, 4),
          teams: {},
          history: [...history],
          weeklyRanking: [...weeklyRanking],
          monthlyRanking: [...monthlyRanking], // Dùng biến đã Map
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
      
      const responseData = res?.data ? res.data : res; 
      const isSuccess = responseData && (responseData.success || responseData.code === 200 || responseData.id);

      if (isSuccess) {
        // Mẹo chống delay: Đợi DB commit transaction xong (200ms) rồi mới gọi lệnh Load
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
    } catch {
      alert("Xóa điểm thất bại");
    }
  };

// THÊM TỔ MỚI (Nút Dấu +)
  const addGroup = async () => {
    try {
      // Tự động tính tên tổ tiếp theo (VD: Đang có 4 tổ -> Tạo "Tổ 5")
      const nextNumber = groups.length + 1;
      const groupName = `Tổ ${nextNumber}`;
      
      const res = await emulationAPI.createGroup(classId, groupName);
      
      if (res) {
        await new Promise(resolve => setTimeout(resolve, 300)); // Đợi DB lưu xíu
        await loadData(true); // Load lại danh sách ngay lập tức
        // alert(`Đã thêm ${groupName} thành công!`);
      }
    } catch (error) {
      console.error("Lỗi thêm tổ:", error);
      alert("Đã có lỗi xảy ra khi thêm tổ!");
    }
  };

  // 1. HÀM SỬA TÊN TỔ
  const editGroup = async (groupId: number, newName: string) => {
    if (!newName.trim()) return false;
    try {
      const res = await emulationAPI.updateGroup(classId, groupId, newName.trim());
      if (res) {
        await new Promise(resolve => setTimeout(resolve, 300));
        await loadData(true);
        return true; // Trả về true để UI biết đường đóng ô nhập
      }
    } catch (error) {
      console.error("Lỗi sửa tên tổ:", error);
      alert("Đã có lỗi xảy ra khi sửa tên tổ!");
    }
    return false;
  };

  // 2. HÀM XÓA TỔ CHỈ ĐỊNH
  const removeGroup = async (groupId: number) => {
    try {
      const res = await emulationAPI.deleteGroup(classId, groupId);
      if (res) {
        await new Promise(resolve => setTimeout(resolve, 300));
        await loadData(true);
        // alert("Đã xóa tổ thành công!");
        return true;
      }
    } catch (error) {
      console.error("Lỗi xóa tổ:", error);
      alert("Đã có lỗi xảy ra khi xóa tổ!");
    }
    return false;
  };

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
    setFilters: (newFilters: Partial<typeof filters>) => {
      setFilters((prev) => ({ ...prev, ...newFilters }));
    },
    refresh: () => loadData(true),
    addPoint,
    deletePoint,
    addGroup,
    editGroup,
    removeGroup,
  };
};