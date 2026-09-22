// src/features/document/components/DocumentViewerModal.tsx
import React from "react";
import { ArrowLeft, Heart, Share2, Download } from "lucide-react";
import type { FileItem } from "@/features/document/types";

interface DocumentViewerModalProps {
  isOpen: boolean;
  onClose: () => void;
  file: FileItem;
  onDownload: () => void;
  onToggleLike: () => void;
}

export const DocumentViewerModal: React.FC<DocumentViewerModalProps> = ({
  isOpen,
  onClose,
  file,
  onDownload,
  onToggleLike,
}) => {
  if (!isOpen) return null;

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
              Đăng bởi: {file.uploader} - Ngày {file.uploadDate || "27/7/2026"}
            </p>
          </div>
        </div>

        {/* Header Right: Các hành động */}
        <div className="flex items-center gap-2 sm:gap-6 shrink-0">
          <button
            onClick={onToggleLike}
            className={`hidden sm:flex items-center gap-2 text-sm transition-colors ${file.isLiked ? "text-red-500" : "text-gray-300 hover:text-white"}`}
          >
            <Heart size={18} className={file.isLiked ? "fill-red-500" : ""} />{" "}
            Yêu thích
          </button>

          <button className="hidden sm:flex items-center gap-2 text-sm text-gray-300 hover:text-white transition-colors">
            <Share2 size={18} /> Chia sẻ
          </button>

          <div className="w-px h-6 bg-[#333] hidden sm:block mx-1"></div>

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
      <div className="flex-1 overflow-hidden relative flex justify-center bg-[#1A1A1A]">
        {file.fileUrl ? (
          <iframe
            src={file.fileUrl}
            className="w-full h-full max-w-5xl bg-white shadow-2xl"
            title={file.name}
          />
        ) : (
          <div className="flex items-center justify-center h-full text-gray-500">
            Tài liệu này không có nội dung hiển thị hoặc đường dẫn bị lỗi.
          </div>
        )}
      </div>
    </div>
  );
};
