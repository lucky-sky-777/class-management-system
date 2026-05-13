// src/features/emulation/hooks/useEmulation.ts
import { useState, useEffect, useCallback } from "react";
import { emulationAPI } from "@features/emulation/api";
import type { CompetitionData } from "@features/emulation/types";

const MOCK_DATA: CompetitionData = {
  teamCount: 4,
  teams: {
    1: [
      { id: "1", name: "Đặng Phong Hào" },
      { id: "2", name: "Trần Việt Tuấn" },
    ],
    2: [{ id: "3", name: "Nguyễn Văn A" }],
    3: [],
    4: [],
  },
  history: [
    {
      id: "1",
      date: "13/05/2026",
      content: "Làm bài tốt",
      points: 10,
      teamId: 1,
      actor: "Giáo viên",
    },
  ],
  weeklyRanking: [
    { rank: 1, teamId: 1, points: 100 },
    { rank: 2, teamId: 2, points: 90 },
  ],
  monthlyRanking: [
    {
      rank: 1,
      teamId: 1,
      points: 400,
      weeks: { t1: 100, t2: 100, t3: 100, t4: 100 },
    },
  ],
};

export const useEmulation = (classId: string) => {
  const [data, setData] = useState<CompetitionData | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const [filters, setFilters] = useState({
    month: new Date().getMonth() + 1,
    year: 2026,
    startDate: "",
    endDate: "",
  });

  // // 1. Hàm load dữ liệu chính (Ranking, Teams...)
  // const loadData = useCallback(async (silent = false) => {
  //   if (!classId) return;
  //   try {
  //     if (!silent) setIsLoading(true);
  //     const result = await emulationAPI.getCompetition(
  //       classId,
  //       filters.month,
  //       filters.startDate,
  //       filters.endDate
  //     );
  //     setData(result);
  //   } catch (error) {
  //     console.error("Lỗi tải dữ liệu thi đua:", error);
  //   } finally {
  //     setIsLoading(false);
  //   }
  // }, [classId, filters.month, filters.startDate, filters.endDate]);

  const loadData = useCallback(
    async (silent = false) => {
      if (!classId) return;
      try {
        if (!silent) setIsLoading(true);
        const result = await emulationAPI.getCompetition(
          classId,
          filters.month,
          filters.startDate,
          filters.endDate,
        );

        // Nếu API thật trả về data thì xài data thật
        if (result) {
          setData(result);
        } else {
          setData(MOCK_DATA); // Chữa cháy nếu API trả về rỗng
        }
      } catch (error) {
        console.error(
          "Lỗi API Tổng quan (404), đang dùng dữ liệu giả để test UI...",
        );
        setData(MOCK_DATA);
      } finally {
        setIsLoading(false);
      }
    },
    [classId, filters.month, filters.startDate, filters.endDate],
  );

  // 2. Hàm ghi điểm (Gọi trực tiếp từ Hook này cho gọn Emulation.tsx)
  const addPoint = async (groupId: number, content: string, points: number) => {
    try {
      await emulationAPI.addPoints(classId, groupId, content, points);
      await loadData(true); // Ghi xong tự refresh dữ liệu ngầm
      return { success: true };
    } catch (error) {
      console.error("Lỗi ghi điểm:", error);
      return { success: false };
    }
  };

  // 3. Hàm xóa dòng lịch sử điểm
  const deletePoint = async (pointId: number) => {
    try {
      await emulationAPI.deletePointLog(classId, pointId);
      await loadData(true);
    } catch {
      alert("Xóa điểm thất bại");
    }
  };

  const changeTeamCount = async (newCount: number) => {
    if (newCount < 1) return;
    try {
      await emulationAPI.updateTeamCount(classId, newCount);
      await loadData(true);
    } catch {
      alert("Lỗi cập nhật số tổ");
    }
  };

  // Tự động gọi API khi mount hoặc filter đổi
  useEffect(() => {
    loadData();
  }, [loadData]);

  return {
    data,
    isLoading,
    filters,
    // Hàm này giúp cập nhật filter từ Component
    setFilters: (newFilters: Partial<typeof filters>) => {
      setFilters((prev) => ({ ...prev, ...newFilters }));
    },
    refresh: () => loadData(true),
    addPoint,
    deletePoint,
    changeTeamCount,
  };
};
