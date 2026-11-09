// src/features/document/components/DocumentViewerModal.tsx
import React, { useState, useEffect } from "react";
import {
  ArrowLeft,
  Heart,
  Share2,
  Download,
  Sparkles,
  Loader2,
  FileText,
} from "lucide-react";
import { useParams } from "react-router-dom";
import type { FileItem } from "@/features/document/types";
import { DocumentAIChatSidebar } from "@/features/document/components/DocumentAIChatSidebar";
import { classDocumentsAPI } from "@/features/document/api";

interface DocumentViewerModalProps {
  isOpen: boolean;
  onClose: () => void;
  file: FileItem;
  directoryId?: string | number;
  onDownload: () => void;
  onToggleLike: () => void;
  onSelectDocument?: (documentId: number | string) => void;
}

export const DocumentViewerModal: React.FC<DocumentViewerModalProps> = ({
  isOpen,
  onClose,
  file,
  directoryId = 0,
  onDownload,
  onToggleLike,
  onSelectDocument,
}) => {
  const { classId } = useParams();
  const [isAIOpen, setIsAIOpen] = useState(false);
  const [blobUrl, setBlobUrl] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let url: string | null = null;
    let isCancelled = false;

    if (!isOpen || !classId) return;

    setIsLoading(true);
    setError(null);
    setBlobUrl(null);

    const currentDirId = file.directoryId ?? directoryId ?? 0;

    classDocumentsAPI
      .getFileBlob(classId, currentDirId, file.id)
      .then(async (blob) => {
        if (isCancelled) return;

        // BẮT LỖI QUAN TRỌNG: API trả về lỗi JSON nhưng bị convert thành Blob
        if (
          blob.type.includes("application/json") ||
          blob.type.includes("json")
        ) {
          const errorText = await blob.text();
          console.error("Lỗi từ server:", errorText);
          throw new Error(
            "Không thể tải tài liệu. Có thể sai thư mục nguồn hoặc tài liệu đã bị xóa.",
          );
        }

        if (blob.size === 0) {
          throw new Error("Tài liệu trống (0 bytes).");
        }

        url = URL.createObjectURL(blob);
        setBlobUrl(url);
        setError(null);
        setIsLoading(false);
      })
      .catch((err) => {
        if (isCancelled) return;
        console.error("Lỗi khi nạp file toàn màn hình:", err);
        setError(err.message || "Không thể đọc stream tài liệu này.");
        setBlobUrl(null);
        setIsLoading(false);
      });

    return () => {
      isCancelled = true;
      if (url) {
        URL.revokeObjectURL(url);
      }
    };
  }, [isOpen, classId, directoryId, file.id, file.directoryId]);

  if (!isOpen) return null;

  const ext = file.fileExtension?.toLowerCase().replace(".", "");

  return (
    // Background dùng bg-surface (đen nhạt ở Dark mode, trắng ở Light mode)
    <div className="fixed inset-0 z-[100] bg-[var(--bg-surface)] flex flex-col animate-in fade-in zoom-in-95 duration-200">
      {/* HEADER */}
      <header className="h-[60px] shrink-0 bg-[var(--bg-surface)] border-b border-[var(--rule)] flex items-center justify-between px-4 sm:px-6 relative z-[103]">
        {/* Header Left: Nút Back & Thông tin */}
        <div className="flex items-center gap-4 sm:gap-6 min-w-0">
          <button
            onClick={onClose}
            className="text-[var(--ink-3)] hover:text-[var(--ink-1)] transition-colors p-1.5 rounded-full hover:bg-[var(--bg-surface-2)]"
          >
            <ArrowLeft size={24} />
          </button>

          <div className="hidden sm:block px-2 py-0.5 bg-[var(--red-fill)] border border-[var(--red-border)] rounded text-[var(--red)] text-[10px] font-bold uppercase tracking-wider">
            {file.fileExtension}
          </div>

          <div className="flex flex-col min-w-0">
            <h2 className="text-[var(--ink-1)] text-sm sm:text-base font-semibold truncate max-w-[200px] sm:max-w-[400px] lg:max-w-[600px]">
              {file.name}
            </h2>
            <p className="text-[var(--ink-3)] text-[11px] sm:text-xs truncate mt-0.5">
              Đăng bởi: {file.uploader}
              <span className="mx-1 text-[var(--rule-lg)]">•</span>
              Ngày: {file.uploadDate || "27/7/2026"}
            </p>
          </div>
        </div>

        {/* Header Right: Các hành động */}
        <div className="flex items-center gap-2 sm:gap-4 lg:gap-6 shrink-0">
          <button
            onClick={onToggleLike}
            className={`hidden sm:flex items-center gap-1.5 text-sm transition-colors ${
              file.isLiked
                ? "text-[var(--red)]"
                : "text-[var(--ink-3)] hover:text-[var(--ink-1)]"
            }`}
          >
            <Heart
              size={18}
              className={file.isLiked ? "fill-[var(--red)]" : ""}
            />{" "}
            Yêu thích
          </button>

          <button className="hidden sm:flex items-center gap-1.5 text-sm text-[var(--ink-3)] hover:text-[var(--ink-1)] transition-colors">
            <Share2 size={18} /> Chia sẻ
          </button>

          <div className="w-px h-6 bg-[var(--rule-lg)] hidden sm:block mx-1"></div>

          {/* Nút Hỏi đáp AI (Dùng Violet Gradient để nổi bật tính năng) */}
          <button
            onClick={() => setIsAIOpen(!isAIOpen)}
            className={`flex items-center gap-1.5 px-3 sm:px-4 py-1.5 sm:py-2 rounded-[var(--r-md)] text-sm font-semibold transition-all active:scale-95 border ${
              isAIOpen
                ? "bg-[var(--violet-fill)] text-[var(--violet)] border-[var(--violet)] shadow-inner"
                : "bg-gradient-to-r from-[var(--violet)] to-[var(--primary)] hover:opacity-90 text-white border-[var(--violet-border)] shadow-[var(--shadow-sm)]"
            }`}
          >
            <Sparkles size={16} />
            <span className="hidden sm:inline">
              {isAIOpen ? "Đóng AI" : "Hỏi đáp AI"}
            </span>
            <span className="inline sm:hidden">AI</span>
          </button>

          <button
            onClick={onDownload}
            className="flex items-center gap-1.5 px-3 sm:px-4 py-1.5 sm:py-2 bg-[var(--primary)] hover:bg-[var(--primary-hover)] text-white rounded-[var(--r-md)] text-sm font-semibold transition-colors shadow-[var(--shadow-sm)] active:scale-95"
          >
            <Download size={16} />
            <span className="hidden sm:inline">Tải xuống</span>
          </button>
        </div>
      </header>

      {/* BODY - Flex row để Sidebar nằm bên phải Canvas */}
      <div className="flex-1 flex overflow-hidden bg-[var(--bg-surface-2)]">
        {/* KHU VỰC HIỂN THỊ TÀI LIỆU (CANVAS) */}
        {/* Nếu isAIOpen = true, thẻ div này sẽ bị thu hẹp lại để nhường chỗ cho Sidebar AI (vì Sidebar có độ rộng cố định) */}
        <div className="flex-1 flex justify-center items-center overflow-hidden transition-all duration-300 ease-[cubic-bezier(0.2,0.8,0.2,1)] p-4 sm:p-6">
          <div className="w-full h-full max-w-5xl flex justify-center items-center relative">
            {isLoading ? (
              <div className="flex flex-col items-center gap-3 text-[var(--ink-3)]">
                <Loader2
                  className="animate-spin text-[var(--primary)]"
                  size={36}
                />
                <p className="text-sm font-medium">
                  Đang nạp dữ liệu tài liệu...
                </p>
              </div>
            ) : error ? (
              <div className="text-center text-[var(--ink-2)] p-8 bg-[var(--bg-surface)] border border-[var(--rule)] rounded-[var(--r-lg)] shadow-[var(--shadow-sm)]">
                <FileText
                  size={48}
                  className="mx-auto mb-4 text-[var(--red)] opacity-70"
                />
                <p className="text-sm font-medium text-[var(--red)]">{error}</p>
              </div>
            ) : blobUrl && ext === "pdf" ? (
              <iframe
                key={`pdf-${file.id}`}
                src={blobUrl}
                className="w-full h-full bg-white shadow-[var(--shadow-lg)] rounded-[var(--r-md)] border border-[var(--rule)]"
                title={file.name}
              />
            ) : blobUrl && ["png", "jpg", "jpeg", "webp"].includes(ext) ? (
              <div className="w-full h-full flex justify-center items-center dark:brightness-[0.85] dark:saturate-[0.9] transition-all duration-200">
                {" "}
                <img
                  key={`img-${file.id}`}
                  src={blobUrl}
                  alt={file.name}
                  className="max-h-full max-w-full object-contain drop-shadow-xl rounded-[var(--r-md)]"
                />
              </div>
            ) : (
              <div className="text-center p-8 bg-[var(--bg-surface)] border border-[var(--rule)] rounded-[var(--r-xl)] text-[var(--ink-2)] max-w-md shadow-[var(--shadow-sm)]">
                <FileText
                  size={48}
                  className="mx-auto mb-4 text-[var(--ink-3)]"
                />
                <h3 className="font-semibold text-[var(--ink-1)] mb-2 text-lg">
                  Định dạng {file.fileExtension?.toUpperCase() || "Này"} không
                  hỗ trợ xem trực tiếp
                </h3>
                <p className="text-sm text-[var(--ink-3)] mb-8">
                  Bạn có thể tải file về thiết bị hoặc sử dụng tính năng Hỏi đáp
                  AI để phân tích nội dung.
                </p>
                {blobUrl && (
                  <button
                    onClick={onDownload}
                    className="inline-flex items-center gap-2 px-5 py-2.5 bg-[var(--primary)] text-white rounded-[var(--r-md)] text-sm font-semibold hover:bg-[var(--primary-hover)] transition-colors shadow-[var(--shadow-sm)] active:scale-95"
                  >
                    <Download size={18} /> Tải file về máy
                  </button>
                )}
              </div>
            )}
          </div>
        </div>

        {/* AI Sidebar - Được render bên trong layout Flex của body thay vì fixed đè lên trên */}
        <div
          className={`h-full bg-[var(--bg-surface)] border-l border-[var(--rule)] transition-all duration-300 ease-[cubic-bezier(0.2,0.8,0.2,1)] shadow-[-8px_0_24px_rgba(0,0,0,0.05)] ${
            isAIOpen
              ? "w-[100%] lg:w-[480px] opacity-100"
              : "w-0 opacity-0 overflow-hidden"
          }`}
        >
          <div className="w-[100vw] lg:w-[480px] h-full flex flex-col relative">
            <DocumentAIChatSidebar
              // Mẹo: Sidebar bên trong DocumentViewerModal không cần quan tâm đến `isOpen` của riêng nó nữa vì chiều rộng/hiển thị đã được kiểm soát bởi div container bọc ngoài ở trên.
              isOpen={isAIOpen}
              onClose={() => setIsAIOpen(false)}
              file={file}
              classId={classId}
              onSelectDocument={onSelectDocument}
            />
          </div>
        </div>
      </div>
    </div>
  );
};
