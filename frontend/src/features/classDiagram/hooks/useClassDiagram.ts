// src/features/classDiagram/hooks/useClassDiagram.ts
import { useState, useEffect } from 'react';
import { classDiagramAPI } from '@features/classDiagram/api';
import type { ClassDiagramData } from '@features/classDiagram/types';

export const useClassDiagram = (classId: string) => {
  const [data, setData] = useState<ClassDiagramData | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const fetchDiagram = async () => {
    try {
      setIsLoading(true);
      const result = await classDiagramAPI.getDiagram(classId);
      setData(result);
    } catch (error) {
      console.error("Lỗi tải sơ đồ:", error);
    } finally {
      setIsLoading(false);
    }
  };

  // TÍNH NĂNG MỚI: GỌI SHUFFLE VÀ TỰ ĐỘNG REFRESH
  const shuffleDiagram = async () => {
    try {
      setIsLoading(true);
      await classDiagramAPI.shuffleSeats(classId);
      await fetchDiagram(); // Gọi xong bắt buộc phải load lại data mới
    } catch (error) {
      console.error("Lỗi shuffle:", error);
      alert("Xếp tự động thất bại!");
      setIsLoading(false); // Nếu lỗi thì tắt loading
    }
  };

  useEffect(() => {
    if (classId) fetchDiagram();
  }, [classId]);

  // Trả về thêm hàm shuffleDiagram
  return { data, isLoading, refresh: fetchDiagram, shuffle: shuffleDiagram };
};