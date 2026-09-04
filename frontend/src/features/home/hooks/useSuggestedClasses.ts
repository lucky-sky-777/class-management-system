// src/features/home/hooks/useSuggestedClasses.ts
import { useState, useEffect, useCallback } from "react";
import { homeAPI } from "@features/home/api";
import type { ClassItems } from "@features/home/types";

export const useSuggestedClasses = () => {
  const [suggestedClasses, setSuggestedClasses] = useState<ClassItems[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const fetchSuggested = useCallback(async () => {
    try {
      setIsLoading(true);
      const res = await homeAPI.getSuggestedClasses();

      if (res.success) {
        setSuggestedClasses(res.data);
      }
    } catch (err) {
      console.error("Lỗi lấy danh sách gợi ý:", err);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    const timer = setTimeout(() => {
      fetchSuggested();
    }, 0);

    return () => clearTimeout(timer);
  }, [fetchSuggested]);

  return { suggestedClasses, isLoading, refreshSuggested: fetchSuggested };
};
