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
      // Lấy năm từ bộ lọc (hoặc dùng new Date().getFullYear())
      const currentYear = filters.year || new Date().getFullYear();

      const weeksData = await emulationAPI.getWeeks(currentYear);
      setWeeks(weeksData);

      if (weeksData && weeksData.length > 0) {
        // CÓ DỮ LIỆU: Tìm tuần hiện tại hoặc lấy tuần đầu tiên
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
        console.warn(`Không có dữ liệu tuần nào cho năm ${currentYear}!`);
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
      setIsLoading(false); // Có lỗi cũng phải tắt loading
    }
  }, [filters.year]);

  const loadData = useCallback(
    async (silent = false) => {
      if (!classId) return;
      try {
        if (!silent) setIsLoading(true);
        // Gọi đồng thời 3 API của Backend
        const [historyRes, weekRankRes, monthRankRes] = await Promise.all([
          emulationAPI.getHistoryByClass(
            classId,
            filters.startDate,
            filters.endDate,
          ),
          emulationAPI.getWeekRanking(
            classId, 
            filters.startDate, 
            filters.endDate
          ),
          emulationAPI.getMonthRanking(classId),
        ]);

        console.log("1. Data Lịch sử gốc:", historyRes);
        console.log("2. Data Rank Tuần gốc:", weekRankRes);
        console.log("3. Data Rank Tháng gốc:", monthRankRes);

        // 1. Map dữ liệu Xếp hạng Tuần
        const weeklyRanking = weekRankRes.map((item: any) => ({
          rank: item.rank,
          teamId: parseInt(item.group_name.replace(/\D/g, "")) || 1, // Bóc tách số từ "Group 1" hoặc "Tổ 1"
          points: item.total_point,
        }));

        // 2. Map dữ liệu Xếp hạng Tháng
        const monthlyRanking = monthRankRes.map((item: any) => ({
          rank: item.rank,
          teamId: parseInt(item.group_name.replace(/\D/g, "")) || 1,
          points: item.total_point,
          weeks: {
            t1: item.week_1_point || 0,
            t2: item.week_2_point || 0,
            t3: item.week_3_point || 0,
            t4: item.week_4_point || 0,
          },
        }));

        // 3. Map dữ liệu Lịch sử (Đoán tên trường DTO của Backend, nếu sai Hào sửa lại nhé)
        const history = historyRes.map((item: any) => ({
          id: item.id?.toString() || Math.random().toString(),
          date: item.created_at
            ? new Date(item.created_at).toLocaleString("vi-VN", {
                day: "2-digit",
                month: "2-digit",
                year: "numeric",
                hour: "2-digit",
                minute: "2-digit",
              })
            : "Vừa xong",
          content: item.description || "Chưa có nội dung",
          points: item.point || 0,
          teamId: item.group_id || 1,
          actor: item.actor_display_name || "Giáo viên",
        }));

        // 4. Tìm teamCount tự động (Dựa vào số lượng tổ trong ranking)
        const teamCount = Math.max(weeklyRanking.length, 4); // Mặc định ít nhất 4 tổ

        setData({
          teamCount,
          teams: {}, // Tạm thời để trống, UI vẫn chạy (hoặc gọi API lấy danh sách hs tổ sau)
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
    [classId, filters.startDate, filters.endDate], // Load lại mỗi khi startDate/endDate đổi
  );

  // Gọi Ghi điểm
  const addPoint = async (groupId: number, content: string, points: number) => {
    try {
      await emulationAPI.addPoints(classId, groupId, content, points);
      await loadData(true); // Ghi xong tự động chạy ngầm loadData để update biểu đồ
      return { success: true };
    } catch (error) {
      console.error("Lỗi ghi điểm:", error);
      return { success: false };
    }
  };

  // Gọi Xóa điểm
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
    // Hào bảo Backend viết thêm 1 API cấu hình lại số lượng tổ nhé
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
