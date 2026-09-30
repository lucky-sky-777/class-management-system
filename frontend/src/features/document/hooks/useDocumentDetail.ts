// src/features/document/hooks/useDocumentDetail.ts
import { useState, useEffect } from "react";
import { classDocumentsAPI } from "@/features/document/api";
import type { FileItem } from "@/features/document/types";

export const useDocumentDetail = (fileId: string | number | null) => {
  const [fileDetail, setFileDetail] = useState<FileItem | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (!fileId) return;
    
    const fetchDetail = async () => {
      setIsLoading(true);
      try {
        const data = await classDocumentsAPI.getFileDetail(fileId);
        setFileDetail(data);
      } catch (error) {
        console.error("Lỗi khi tải chi tiết tệp:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchDetail();
  }, [fileId]);

  const handleToggleLike = () => {
    if (!fileDetail) return;
    setFileDetail(prev => prev ? {
      ...prev,
      isLiked: !prev.isLiked,
      likes: prev.isLiked ? (prev.likes || 1) - 1 : (prev.likes || 0) + 1
    } : null);
  };

  const handleDownload = () => {
    console.log("Đang tải xuống tài liệu:", fileDetail?.name);
    // Logic gọi API tải file thực tế ở đây
  };

  return { fileDetail, isLoading, handleToggleLike, handleDownload };
};