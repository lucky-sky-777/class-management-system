// src/features/document/components/DocumentPreview.tsx
import React from "react";
import { Maximize2, FileText, Heart } from "lucide-react";
import type { FileItem } from "@/features/document/types";

interface DocumentPreviewProps {
  file: FileItem;
  onOpenFullScreen: () => void;
  onToggleLike?: (id: string | number) => void;
}

export const DocumentPreview: React.FC<DocumentPreviewProps> = ({
  file,
  onOpenFullScreen,
  onToggleLike,
}) => {
  return (
    <div className="flex-1 flex flex-col min-w-0">
      {/* Header */}
      <div className="flex items-start justify-between mb-4">
        <div>
          <h2 className="text-xl font-bold text-[var(--ink-1)]">{file.name}</h2>
          <p className="text-sm text-[var(--ink-3)] mt-1 flex items-center gap-2">
            <span>{file.uploader}</span>
            {file.uploadDate && (
              <>
                <span>•</span>
                <span>{file.uploadDate}</span>
              </>
            )}
          </p>
        </div>
        
        {/* Nhóm nút góc phải */}
        <div className="flex items-center gap-3">
          {/* Nút Yêu thích */}
          {onToggleLike && (
            <button
              onClick={() => onToggleLike(file.id)}
              className={`p-2 rounded-full transition-all active:scale-95 border ${
                file.isLiked
                  ? "bg-red-50 border-red-100 text-red-500 shadow-sm" // Trạng thái đã thích
                  : "bg-white border-[var(--rule)] text-[var(--ink-3)] hover:text-red-500 hover:bg-red-50 hover:border-red-100 shadow-sm" // Trạng thái chưa thích
              }`}
              title={file.isLiked ? "Bỏ yêu thích" : "Thêm vào yêu thích"}
            >
              <Heart size={18} className={file.isLiked ? "fill-current" : ""} />
            </button>
          )}

          {/* Nhãn định dạng */}
          <span className="px-3 py-1.5 bg-red-100 text-red-600 text-xs font-bold rounded-full uppercase tracking-wider">
            {file.fileExtension}
          </span>
        </div>
      </div>

      {/* Vùng hiển thị xem trước */}
      <div className="flex-1 min-h-[400px] lg:min-h-[500px] bg-[var(--primary-fill)] border border-[var(--rule)] rounded-2xl flex flex-col items-center justify-center relative group">
        <div className="text-center flex flex-col items-center">
          <div className="w-16 h-20 bg-red-500 rounded-lg flex items-center justify-center mb-4 shadow-sm relative overflow-hidden">
            <div className="absolute top-0 right-0 w-6 h-6 bg-red-600 rounded-bl-lg"></div>
            <FileText size={32} color="white" />
          </div>

          <p className="text-[var(--ink-2)] text-sm font-medium mb-4">
            Xem trước tài liệu {file.fileExtension.toUpperCase()}
          </p>

          {/* Nút full màn hình */}
          <button
            onClick={onOpenFullScreen}
            className="flex items-center gap-2 px-4 py-2 bg-[var(--bg-surface)] border border-[var(--primary)] text-[var(--primary)] rounded-lg text-sm font-semibold hover:bg-[var(--primary-fill)] transition-colors shadow-sm"
          >
            <Maximize2 size={16} /> Mở toàn màn hình
          </button>
        </div>
      </div>
    </div>
  );
};