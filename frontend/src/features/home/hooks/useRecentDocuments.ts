import { useState, useEffect, useCallback } from "react";
import { homeAPI } from "@features/home/api";
import type { RecentGroupDocument } from "@features/home/types";

export const useRecentDocuments = () => {
  const [documents, setDocuments] = useState<RecentGroupDocument[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const fetchDocuments = useCallback(async () => {
    try {
      setIsLoading(true);
      const res = await homeAPI.getRecentDocuments();
      if (res.success) {
        setDocuments(res.data);
      }
    } catch (error) {
      console.error("Lỗi lấy danh sách tài liệu mới:", error);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    const timer = setTimeout(() => {
      fetchDocuments();
    }, 0);

    return () => clearTimeout(timer);
  }, [fetchDocuments]);
  return { documents, isLoading, refreshDocuments: fetchDocuments };
};