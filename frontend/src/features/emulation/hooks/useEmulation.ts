import { useState, useEffect, useCallback } from "react";
import { emulationAPI } from "@features/emulation/api";
import type { CompetitionData } from "@features/emulation/types";

export const useEmulation = (classId: string) => {
  const [data, setData] = useState<CompetitionData | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [weeks, setWeeks] = useState<any[]>([]);

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

        const [historyRes, weekRankRes, monthRankRes] = await Promise.all([
          emulationAPI.getHistoryByClass(classId, filters.startDate, filters.endDate),
          emulationAPI.getWeekRanking(classId, filters.startDate, filters.endDate),
          emulationAPI.getMonthRanking(classId),
        ]);

        const rawHistory = historyRes?.data || (Array.isArray(historyRes) ? historyRes : []);
        const rawWeekRank = weekRankRes?.data || (Array.isArray(weekRankRes) ? weekRankRes : []);
        const rawMonthRank = monthRankRes?.data || (Array.isArray(monthRankRes) ? monthRankRes : []);

        const history = rawHistory.map((item: any) => ({
          id: item.id?.toString() || Math.random().toString(),
          date: item.created_at ? new Date(item.created_at).toLocaleString("vi-VN") : "Vừa xong",
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

        // 👉 ĐÃ SỬA LỖI 3: Thêm Map cho Xếp Hạng Tháng để UI có thể hiển thị
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
      
      // 👉 ĐÃ SỬA LỖI 1 & 2: Bọc điều kiện an toàn, hỗ trợ trường hợp Axios bọc res.data
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

  const changeTeamCount = async (newCount: number) => {
    console.log("Cập nhật team count thành:", newCount);
  };

  useEffect(() => {
    loadWeeks();
  }, [loadWeeks]);

  useEffect(() => {
    if (filters.startDate) {
      loadData();
    }
  }, [loadData, filters.startDate]);

  return {
    data,
    isLoading,
    filters,
    weeks,
    setFilters: (newFilters: Partial<typeof filters>) => {
      setFilters((prev) => ({ ...prev, ...newFilters }));
    },
    refresh: () => loadData(true),
    addPoint,
    deletePoint,
    changeTeamCount,
  };
};