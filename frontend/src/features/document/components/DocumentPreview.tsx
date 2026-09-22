// src/features/document/components/DocumentPreview.tsx
import React from "react";
import { Maximize2, FileText } from "lucide-react";
import type { FileItem } from "@/features/document/types";

interface DocumentPreviewProps {
  file: FileItem;
  onOpenFullScreen: () => void;
}

export const DocumentPreview: React.FC<DocumentPreviewProps> = ({
  file,
  onOpenFullScreen,
}) => {
  return (
    <div className="flex-1 flex flex-col min-w-0">
      {/* Header */}
      <div className="flex items-start justify-between mb-4">
        <div>
          <h2 className="text-xl font-bold text-[var(--ink-1)]">{file.name}</h2>
          <p className="text-sm text-[var(--ink-3)] mt-1">
            {file.uploader} • {file.uploadDate}
          </p>
        </div>
        {/* Giữ class màu đỏ của Tailwind cho nhãn định dạng (PDF/DOC) để dễ phân biệt */}
        <span className="px-3 py-1 bg-red-100 text-red-600 text-xs font-bold rounded-full uppercase tracking-wider">
          {file.fileExtension}
        </span>
      </div>

      {/* Vùng hiển thị xem trước - Dùng nền primary-fill và viền rule */}
      <div className="flex-1 min-h-[400px] lg:min-h-[500px] bg-[var(--primary-fill)] border border-[var(--rule)] rounded-2xl flex flex-col items-center justify-center relative group">
        <div className="text-center flex flex-col items-center">
          <div className="w-16 h-20 bg-red-500 rounded-lg flex items-center justify-center mb-4 shadow-sm relative overflow-hidden">
            <div className="absolute top-0 right-0 w-6 h-6 bg-red-600 rounded-bl-lg"></div>
            <FileText size={32} color="white" />
          </div>

          <p className="text-[var(--ink-2)] text-sm font-medium mb-4">
            Xem trước tài liệu {file.fileExtension.toUpperCase()}
          </p>

          {/* Nút full màn hình dùng màu Primary */}
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
