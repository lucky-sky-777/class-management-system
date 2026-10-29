// src/features/document/hooks/useDocumentDetail.ts
import { useState, useEffect, useCallback } from "react";
import { classDocumentsAPI } from "@/features/document/api";
import type { FileItem } from "@/features/document/types";

export const useDocumentDetail = (
  fileId: string | number | null,
  initialFile?: FileItem | null,
) => {
  const [fileDetail, setFileDetail] = useState<FileItem | null>(
    initialFile ?? null,
  );
  const [isLoading, setIsLoading] = useState<boolean>(false);

  // Đồng bộ nhanh khi fileId thay đổi
  const [prevId, setPrevId] = useState(fileId);
  if (fileId !== prevId) {
    setPrevId(fileId);
    setFileDetail(initialFile ?? null);
    setIsLoading(Boolean(fileId));
  }

  useEffect(() => {
    if (!fileId) return;

    let isMounted = true;

    const loadDetail = async () => {
      try {
        const fullData = await classDocumentsAPI.getFileDetail(fileId);
        if (isMounted) {
          // Gộp dữ liệu: giữ lại các thông tin của file ban đầu nếu backend metadata chưa có
          setFileDetail((prev) => ({
            ...(prev || {}),
            ...fullData,
            name:
              fullData.name !== "Tài liệu"
                ? fullData.name
                : prev?.name || fullData.name,
            fileExtension:
              fullData.fileExtension !== "file"
                ? fullData.fileExtension
                : prev?.fileExtension || "file",
          }));
        }
      } catch (err) {
        console.error("Lỗi khi tải chi tiết tài liệu:", err);
      } finally {
        if (isMounted) setIsLoading(false);
      }
    };

    loadDetail();

    return () => {
      isMounted = false;
    };
  }, [fileId]);

  const handleToggleLike = useCallback(() => {
    setFileDetail((prev) =>
      prev
        ? {
            ...prev,
            isLiked: !prev.isLiked,
            likes: prev.isLiked
              ? Math.max(0, (prev.likes || 1) - 1)
              : (prev.likes || 0) + 1,
          }
        : null,
    );
  }, []);

  return { fileDetail, isLoading, handleToggleLike };
};
