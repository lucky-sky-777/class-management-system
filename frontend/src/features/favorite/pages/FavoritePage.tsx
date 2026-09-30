// src/features/favorite/pages/FavoritePage.tsx
import { useState } from "react";
import { Loader2, ArrowLeft } from "lucide-react";
import { useFavorites } from "@/features/favorite/hooks/useFavorites";
import { useDocumentDetail } from "@/features/document/hooks/useDocumentDetail";
import { FilePreviewCard } from "@/features/document/components/FilePreviewCard";
import { DocumentPreview } from "@/features/document/components/DocumentPreview";
import { DocumentSidebar } from "@/features/document/components/DocumentSidebar";
import { DocumentViewerModal } from "@/features/document/components/DocumentViewerModal";

export const FavoritePage = () => {
  // Lấy danh sách tổng
  const { favorites, isLoading: isListLoading } = useFavorites();

  // Quản lý trạng thái đang xem file nào
  const [selectedFileId, setSelectedFileId] = useState<string | number | null>(
    null,
  );
  const [isViewerOpen, setIsViewerOpen] = useState(false);

  // Hook lấy chi tiết file được chọn
  const {
    fileDetail,
    isLoading: isDetailLoading,
    handleToggleLike,
    handleDownload,
  } = useDocumentDetail(selectedFileId);

  // Màn hình loading ban đầu
  if (isListLoading) {
    return (
      <div className="flex h-[400px] items-center justify-center">
        <Loader2 className="animate-spin text-[var(--primary)]" size={40} />
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto p-4 md:p-6 h-full flex flex-col">
      {/* TRẠNG THÁI 1: HIỂN THỊ CHI TIẾT FILE (SPLIT-PANE) */}
      {selectedFileId && fileDetail ? (
        <div className="animate-in fade-in slide-in-from-bottom-4 duration-300">
          {/* Thanh Toolbar phụ để quay lại */}
          <div className="flex items-center gap-4 mb-6">
            <button
              onClick={() => setSelectedFileId(null)}
              className="flex items-center gap-2 px-3 py-1.5 text-sm font-medium text-[var(--ink-2)] hover:text-[var(--ink-1)] hover:bg-[var(--bg-surface-2)] rounded-lg transition-colors"
            >
              <ArrowLeft size={18} /> Quay lại danh sách
            </button>
            <div className="h-4 w-px bg-[var(--rule)]"></div>
            <h1 className="text-lg font-bold text-[var(--ink-1)] flex items-center gap-2">
              Chi tiết tài liệu
            </h1>
          </div>

          {/* Dùng chung Layout chia đôi y hệt trang DocumentPage */}
          {isDetailLoading ? (
            <div className="flex justify-center py-20">
              <Loader2
                className="animate-spin text-[var(--primary)]"
                size={40}
              />
            </div>
          ) : (
            <div className="flex flex-col lg:flex-row gap-6 lg:gap-8">
              <DocumentPreview
                file={fileDetail}
                onOpenFullScreen={() => setIsViewerOpen(true)}
              />
              <DocumentSidebar
                file={fileDetail}
                onDownload={handleDownload}
                onToggleLike={handleToggleLike}
              />
            </div>
          )}
        </div>
      ) : (
        /* TRẠNG THÁI 2: HIỂN THỊ DANH SÁCH LƯỚI CARD */
        <div className="animate-in fade-in duration-300">
          <div className="mb-6">
            <h1 className="text-2xl font-bold text-[var(--ink-1)] flex items-center gap-2">
              Tài liệu đã thích
            </h1>
          </div>

          {favorites.length === 0 ? (
            <div className="text-center py-20 text-[var(--ink-3)] bg-[var(--bg-surface)] rounded-2xl border border-[var(--rule)]">
              Bạn chưa thả tim tài liệu nào.
            </div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-4">
              {favorites.map((file) => (
                <FilePreviewCard
                  key={file.id}
                  file={file}
                  onClick={() => setSelectedFileId(file.id)}
                  // Cố tình không truyền onEdit và onDelete để cấm sửa/xóa ở màn Yêu thích
                />
              ))}
            </div>
          )}
        </div>
      )}

      {/* MODAL VIEW TOÀN MÀN HÌNH (Gắn ở cuối trang) */}
      {fileDetail && (
        <DocumentViewerModal
          isOpen={isViewerOpen}
          onClose={() => setIsViewerOpen(false)}
          file={fileDetail}
          onDownload={handleDownload}
          onToggleLike={handleToggleLike}
        />
      )}
    </div>
  );
};
