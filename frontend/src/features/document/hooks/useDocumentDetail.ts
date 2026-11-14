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
  const [isTogglingLike, setIsTogglingLike] = useState<boolean>(false);

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
        const fullData = await classDocumentsAPI.getFileDetail(
          fileId,
          initialFile,
        );
        if (isMounted) {
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

  // Toggle Bookmark / Like (Optimistic Update)
  const handleToggleLike = useCallback(
    async (targetId?: string | number) => {
      const activeId = targetId ?? fileDetail?.id ?? fileId;
      if (!activeId || isTogglingLike || !fileDetail) return;

      const prevIsLiked = fileDetail.isLiked ?? false;
      const prevLikes = fileDetail.likes ?? 0;

      const nextIsLiked = !prevIsLiked;
      setFileDetail((prev) =>
        prev
          ? {
              ...prev,
              isLiked: nextIsLiked,
              likes: nextIsLiked
                ? (prev.likes ?? 0) + 1
                : Math.max(0, (prev.likes ?? 1) - 1),
            }
          : null,
      );

      setIsTogglingLike(true);
      try {
        await classDocumentsAPI.toggleBookmark(activeId);
      } catch (err) {
        console.error("Lỗi khi gọi API bookmark/like:", err);
        // Rollback state nếu gọi thất bại
        setFileDetail((prev) =>
          prev
            ? {
                ...prev,
                isLiked: prevIsLiked,
                likes: prevLikes,
              }
            : null,
        );
      } finally {
        setIsTogglingLike(false);
      }
    },
    [fileDetail, fileId, isTogglingLike],
  );

  return {
    fileDetail,
    setFileDetail,
    isLoading,
    isTogglingLike,
    handleToggleLike,
  };
};
