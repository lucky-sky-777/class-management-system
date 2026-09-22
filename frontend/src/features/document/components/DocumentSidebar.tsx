// src/features/document/components/DocumentSidebar.tsx
import React from "react";
import { HardDrive, Download, Heart, Edit2, Trash2 } from "lucide-react";
import type { FileItem } from "@/features/document/types";

interface DocumentSidebarProps {
  file: FileItem;
  onDownload: () => void;
  onToggleLike: () => void;
  onEdit: () => void;
  onDelete: () => void;
}

export const DocumentSidebar: React.FC<DocumentSidebarProps> = ({
  file, onDownload, onToggleLike, onEdit, onDelete
}) => {
  return (
    <div className="w-full lg:w-[320px] xl:w-[360px] shrink-0 flex flex-col gap-6">
      
      {/* Card Thông tin chung */}
      <div className="bg-[var(--bg-surface)] p-5 rounded-2xl border border-[var(--rule)]">
        <h3 className="font-bold text-[var(--ink-1)] mb-4 text-lg">Chi tiết tài liệu</h3>
        
        <div className="flex flex-col gap-3 text-sm">
          <div className="flex justify-between items-center">
            <div className="flex items-center gap-2 text-[var(--ink-2)]">
              <HardDrive size={18} /> <span>Dung lượng</span>
            </div>
            <span className="font-semibold text-[var(--ink-1)]">{file.size}</span>
          </div>
          <div className="flex justify-between items-center">
            <div className="flex items-center gap-2 text-[var(--ink-2)]">
              <Download size={18} /> <span>Lượt tải xuống</span>
            </div>
            <span className="font-semibold text-[var(--ink-1)]">{file.downloads}</span>
          </div>
          <div className="flex justify-between items-center">
            <div className="flex items-center gap-2 text-[var(--ink-2)]">
              <Heart size={18} className={file.isLiked ? "fill-red-500 text-red-500" : ""} /> 
              <span>Yêu thích</span>
            </div>
            <span className="font-semibold text-[var(--ink-1)]">{file.likes}</span>
          </div>
        </div>

        <hr className="my-5 border-[var(--rule)]" />

        <div className="mb-4">
          <h4 className="text-xs font-bold text-[var(--ink-3)] uppercase tracking-wider mb-2">Mô tả</h4>
          <p className="text-sm text-[var(--ink-2)] leading-relaxed">
            {file.description || "Chưa có mô tả cho tài liệu này."}
          </p>
        </div>

        <div>
          <h4 className="text-xs font-bold text-[var(--ink-3)] uppercase tracking-wider mb-2">Từ khóa</h4>
          <div className="flex flex-wrap gap-2">
            {file.tags?.map((tag, index) => (
              <span 
                key={index} 
                // Sử dụng màu nền và chữ dựa trên var(--primary)
                className="px-3 py-1 bg-[var(--primary-fill)] text-[var(--primary)] border border-[var(--primary-fill)] rounded-full text-xs font-semibold"
              >
                {tag.startsWith('#') ? tag : `#${tag}`}
              </span>
            ))}
          </div>
        </div>
      </div>

      {/* Cụm Nút Hành Động */}
      <div className="flex flex-col gap-3">
        {/* Nút Tải xuống chính (Nền Primary) */}
        <button 
          onClick={onDownload}
          className="w-full flex items-center justify-center gap-2 px-4 py-3 bg-[var(--primary)] text-white hover:brightness-95 rounded-xl font-bold transition-all shadow-sm active:scale-95"
        >
          <Download size={18} /> TẢI XUỐNG TÀI LIỆU
        </button>
        
        {/* Nút Yêu thích (Nền xám nhạt) */}
        <button 
          onClick={onToggleLike}
          className="w-full flex items-center justify-center gap-2 px-4 py-3 bg-[var(--bg-surface-2)] hover:bg-[var(--rule)] text-[var(--ink-1)] border border-[var(--rule)] rounded-xl font-semibold transition-all active:scale-95"
        >
          <Heart size={18} className={file.isLiked ? "fill-red-500 text-red-500" : ""} /> 
          {file.isLiked ? "Đã yêu thích" : "Lưu vào yêu thích"}
        </button>

        <div className="flex gap-3">
          {/* Nút Chỉnh sửa */}
          <button 
            onClick={onEdit}
            className="flex-1 flex items-center justify-center gap-2 px-4 py-2.5 bg-[var(--bg-surface)] hover:bg-[var(--bg-surface-2)] text-[var(--ink-2)] border border-[var(--rule)] rounded-xl text-sm font-semibold transition-all active:scale-95"
          >
            <Edit2 size={16} /> Chỉnh sửa
          </button>
          
          {/* Nút Xóa (Giữ màu đỏ báo hiệu Danger) */}
          <button 
            onClick={onDelete}
            className="flex-1 flex items-center justify-center gap-2 px-4 py-2.5 bg-[var(--bg-surface)] hover:bg-red-50 text-red-500 border border-red-200 rounded-xl text-sm font-semibold transition-all active:scale-95"
          >
            <Trash2 size={16} /> Xóa
          </button>
        </div>
      </div>
    </div>
  );
};