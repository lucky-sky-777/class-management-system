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
}

export const DocumentViewerModal: React.FC<DocumentViewerModalProps> = ({
  isOpen,
  onClose,
  file,
  directoryId = 0,
  onDownload,
  onToggleLike,
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

    classDocumentsAPI
      .getFileBlob(classId, directoryId, file.id)
      .then((blob) => {
        if (isCancelled) return;
        url = URL.createObjectURL(blob);
        setBlobUrl(url);
        setError(null);
        setIsLoading(false);
      })
      .catch((err) => {
        if (isCancelled) return;
        console.error("Lỗi khi nạp file toàn màn hình:", err);
        setError("Không thể đọc stream tài liệu này.");
        setBlobUrl(null);
        setIsLoading(false);
      });

    return () => {
      isCancelled = true;
      if (url) {
        URL.revokeObjectURL(url);
      }
    };
  }, [isOpen, classId, directoryId, file.id]);

  if (!isOpen) return null;

  const ext = file.fileExtension?.toLowerCase().replace(".", "");

  return (
    <div className="fixed inset-0 z-[100] bg-[#0F0F0F] flex flex-col animate-in fade-in zoom-in-95 duration-200">
      {/* HEADER TỐI MÀU */}
      <header className="h-[60px] shrink-0 bg-[#0A0A0A] border-b border-[#222222] flex items-center justify-between px-4 sm:px-6">
        {/* Header Left: Nút Back & Thông tin */}
        <div className="flex items-center gap-4 sm:gap-6 min-w-0">
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-white transition-colors p-1 rounded-full hover:bg-white/10"
          >
            <ArrowLeft size={24} />
          </button>

          <div className="hidden sm:block px-2 py-0.5 bg-red-950 border border-red-800 rounded text-red-500 text-[10px] font-bold uppercase tracking-wider">
            {file.fileExtension}
          </div>

          <div className="flex flex-col min-w-0">
            <h2 className="text-white text-sm sm:text-base font-semibold truncate max-w-[200px] sm:max-w-[400px] lg:max-w-[600px]">
              {file.name}
            </h2>
            <p className="text-gray-400 text-[11px] sm:text-xs truncate">
              Đăng bởi: {file.uploader} • Ngày: {file.uploadDate || "27/7/2026"}
            </p>
          </div>
        </div>

        {/* Header Right: Các hành động */}
        <div className="flex items-center gap-2 sm:gap-6 shrink-0">
          <button
            onClick={onToggleLike}
            className={`hidden sm:flex items-center gap-2 text-sm transition-colors ${
              file.isLiked ? "text-red-500" : "text-gray-300 hover:text-white"
            }`}
          >
            <Heart size={18} className={file.isLiked ? "fill-red-500" : ""} />{" "}
            Yêu thích
          </button>

          <button className="hidden sm:flex items-center gap-2 text-sm text-gray-300 hover:text-white transition-colors">
            <Share2 size={18} /> Chia sẻ
          </button>

          <div className="w-px h-6 bg-[#333] hidden sm:block mx-1"></div>

          <button
            onClick={() => setIsAIOpen(true)}
            className="flex items-center gap-2 px-3 sm:px-4 py-2 bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-500 hover:to-indigo-500 text-white rounded-lg text-sm font-semibold transition-all shadow-[0_0_15px_rgba(124,58,237,0.3)] hover:shadow-[0_0_20px_rgba(124,58,237,0.5)]"
          >
            <Sparkles size={16} />
            <span className="hidden sm:inline">Hỏi đáp AI</span>
          </button>

          <button
            onClick={onDownload}
            className="flex items-center gap-2 px-3 sm:px-4 py-2 bg-[#2D68FE] hover:bg-[#1A56EB] text-white rounded-lg text-sm font-semibold transition-colors"
          >
            <Download size={16} />
            <span className="hidden sm:inline">Tải xuống</span>
          </button>
        </div>
      </header>

      {/* KHU VỰC HIỂN THỊ TÀI LIỆU (CANVAS) */}
      <div className="flex-1 overflow-hidden relative flex justify-center items-center bg-[#1A1A1A]">
        {isLoading ? (
          <div className="flex flex-col items-center gap-3 text-gray-400">
            <Loader2 className="animate-spin" size={36} />
            <p className="text-sm">Đang nạp dữ liệu tài liệu...</p>
          </div>
        ) : error ? (
          <div className="text-center text-gray-400 p-6">
            <FileText size={48} className="mx-auto mb-3 opacity-40" />
            <p className="text-sm text-red-400">{error}</p>
          </div>
        ) : blobUrl && ext === "pdf" ? (
          <iframe
            src={blobUrl}
            className="w-full h-full max-w-5xl bg-white shadow-2xl border-none"
            title={file.name}
          />
        ) : blobUrl && ["png", "jpg", "jpeg", "webp"].includes(ext) ? (
          <img
            src={blobUrl}
            alt={file.name}
            className="max-h-full max-w-full object-contain p-4"
          />
        ) : (
          <div className="text-center p-8 bg-[#222] rounded-xl text-gray-300 max-w-md">
            <FileText size={48} className="mx-auto mb-3 text-gray-400" />
            <h3 className="font-semibold text-white mb-1">
              Định dạng {file.fileExtension.toUpperCase()} không hỗ trợ xem trực
              tiếp
            </h3>
            <p className="text-xs text-gray-400 mb-6">
              Bạn có thể tải file về thiết bị hoặc sử dụng tính năng Hỏi đáp AI
              để phân tích nội dung.
            </p>
            {blobUrl && (
              <a
                href={blobUrl}
                download={file.name}
                className="inline-flex items-center gap-2 px-4 py-2 bg-[#2D68FE] text-white rounded-lg text-sm font-medium hover:bg-[#1A56EB]"
              >
                <Download size={16} /> Tải file về máy
              </a>
            )}
          </div>
        )}

        {/* AI Sidebar */}
        <DocumentAIChatSidebar
          isOpen={isAIOpen}
          onClose={() => setIsAIOpen(false)}
          file={file}
        />
      </div>
    </div>
  );
};
