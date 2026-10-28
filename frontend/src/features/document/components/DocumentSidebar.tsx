import React from "react";
import {
  HardDrive,
  Download,
  Heart,
  Edit2,
  Trash2,
  FileCode,
} from "lucide-react";
import type { FileItem } from "@/features/document/types";

interface DocumentSidebarProps {
  file: FileItem;
  onDownload: () => void;
  onToggleLike: () => void;
  onEdit?: () => void;
  onDelete?: () => void;
}

export const DocumentSidebar: React.FC<DocumentSidebarProps> = ({
  file,
  onDownload,
  onToggleLike,
  onEdit,
  onDelete,
}) => {
  return (
    <div className="w-full lg:w-[320px] xl:w-[360px] shrink-0 flex flex-col gap-6">
      {/* Card Thông tin chung */}
      <div className="bg-[var(--bg-surface)] p-5 rounded-2xl border border-[var(--rule)]">
        <h3 className="font-bold text-[var(--ink-1)] mb-4 text-lg">
          Chi tiết tài liệu
        </h3>

        <div className="flex flex-col gap-3 text-sm">
          {/* Dung lượng */}
          <div className="flex justify-between items-center">
            <div className="flex items-center gap-2 text-[var(--ink-2)]">
              <HardDrive size={18} /> <span>Dung lượng</span>
            </div>
            <span className="font-semibold text-[var(--ink-1)]">
              {file.size || "--"}
            </span>
          </div>

          {/* Định dạng MIME nếu có từ metadata */}
          {file.rawMetadata?.mime_type && (
            <div className="flex justify-between items-center">
              <div className="flex items-center gap-2 text-[var(--ink-2)]">
                <FileCode size={18} /> <span>Định dạng</span>
              </div>
              <span className="font-semibold text-[var(--ink-1)] uppercase">
                {file.fileExtension}
              </span>
            </div>
          )}

          {/* Lượt tải xuống */}
          <div className="flex justify-between items-center">
            <div className="flex items-center gap-2 text-[var(--ink-2)]">
              <Download size={18} /> <span>Lượt tải xuống</span>
            </div>
            <span className="font-semibold text-[var(--ink-1)]">
              {file.downloads ?? 0}
            </span>
          </div>

          {/* Yêu thích */}
          <div className="flex justify-between items-center">
            <div className="flex items-center gap-2 text-[var(--ink-2)]">
              <Heart
                size={18}
                className={file.isLiked ? "fill-red-500 text-red-500" : ""}
              />
              <span>Yêu thích</span>
            </div>
            <span className="font-semibold text-[var(--ink-1)]">
              {file.likes ?? 0}
            </span>
          </div>
        </div>

        {/* Khối Metadata chi tiết
        {file.rawMetadata && (
          <div className="mt-4 pt-3 border-t border-[var(--rule)]">
            <h4 className="text-xs font-bold text-[var(--ink-3)] uppercase tracking-wider mb-2">
              Metadata hệ thống
            </h4>
            <div className="bg-[var(--bg-surface-2)] p-2.5 rounded-lg text-xs space-y-1.5 break-all">
              <div>
                <span className="text-[var(--ink-3)]">File gốc: </span>
                <span className="font-medium text-[var(--ink-1)]">
                  {file.rawMetadata.file_name}
                </span>
              </div>
              {file.rawMetadata.mime_type && (
                <div>
                  <span className="text-[var(--ink-3)]">MIME: </span>
                  <span className="font-mono text-[var(--ink-2)] text-[11px]">
                    {file.rawMetadata.mime_type}
                  </span>
                </div>
              )}
            </div>
          </div>
        )} */}

        <hr className="my-5 border-[var(--rule)]" />

        {/* Mô tả */}
        <div className="mb-4">
          <h4 className="text-xs font-bold text-[var(--ink-3)] uppercase tracking-wider mb-2">
            Mô tả
          </h4>
          <p className="text-sm text-[var(--ink-2)] leading-relaxed whitespace-pre-line">
            {file.description?.trim() || "Chưa có mô tả cho tài liệu này."}
          </p>
        </div>

        {/* Từ khóa (Tags từ backend) */}
        <div>
          <h4 className="text-xs font-bold text-[var(--ink-3)] uppercase tracking-wider mb-2">
            Từ khóa
          </h4>
          {file.tags && file.tags.length > 0 ? (
            <div className="flex flex-wrap gap-2">
              {file.tags.map((tag, index) => (
                <span
                  key={index}
                  className="px-3 py-1 bg-[var(--primary-fill)] text-[var(--primary)] border border-[var(--primary-fill)] rounded-full text-xs font-semibold"
                >
                  {tag.startsWith("#") ? tag : `#${tag}`}
                </span>
              ))}
            </div>
          ) : (
            <p className="text-xs text-[var(--ink-3)] italic">
              Không có từ khóa
            </p>
          )}
        </div>
      </div>

      {/* Cụm Nút Hành Động */}
      <div className="flex flex-col gap-3">
        <button
          type="button"
          onClick={onDownload}
          className="w-full flex items-center justify-center gap-2 px-4 py-3 bg-[var(--primary)] text-white hover:brightness-95 rounded-xl font-bold transition-all shadow-sm active:scale-95"
        >
          <Download size={18} /> TẢI XUỐNG TÀI LIỆU
        </button>

        <button
          type="button"
          onClick={onToggleLike}
          className="w-full flex items-center justify-center gap-2 px-4 py-3 bg-[var(--bg-surface-2)] hover:bg-[var(--rule)] text-[var(--ink-1)] border border-[var(--rule)] rounded-xl font-semibold transition-all active:scale-95"
        >
          <Heart
            size={18}
            className={file.isLiked ? "fill-red-500 text-red-500" : ""}
          />
          {file.isLiked ? "Đã yêu thích" : "Lưu vào yêu thích"}
        </button>

        <div className="flex gap-3">
          {onEdit && (
            <button
              type="button"
              onClick={onEdit}
              className="flex-1 flex items-center justify-center gap-2 px-4 py-2.5 bg-[var(--bg-surface)] hover:bg-[var(--bg-surface-2)] text-[var(--ink-2)] border border-[var(--rule)] rounded-xl text-sm font-semibold transition-all active:scale-95"
            >
              <Edit2 size={16} /> Chỉnh sửa
            </button>
          )}

          {onDelete && (
            <button
              type="button"
              onClick={onDelete}
              className="flex-1 flex items-center justify-center gap-2 px-4 py-2.5 bg-[var(--bg-surface)] hover:bg-red-50 text-red-500 border border-red-200 rounded-xl text-sm font-semibold transition-all active:scale-95"
            >
              <Trash2 size={16} /> Xóa
            </button>
          )}
        </div>
      </div>
    </div>
  );
};
