// src/features/classDocuments/components/FilePreviewCard.tsx
import React, { useState, useRef, useEffect } from "react";
import { Edit2, Trash2, Heart } from "lucide-react";
import { DocumentIcon } from "@/shared/components/icons/DocumentIcon";
import { classDocumentsAPI } from "@/features/document/api";
import type { FileItem } from "@/features/document/types";

interface FilePreviewCardProps {
  file: FileItem;
  viewMode?: "grid" | "list";
  onClick: () => void;
  onEdit?: (id: string | number, newName: string) => void;
  onDelete?: (id: string | number) => void;
  onToggleLike?: (id: string | number) => void;
}

export const FilePreviewCard: React.FC<FilePreviewCardProps> = ({
  file,
  viewMode = "grid",
  onClick,
  onDelete,
  onEdit,
  onToggleLike,
}) => {
  const fileName = file.name || (file as any).fileName || "Tài liệu không tên";
  const uploadDate = file.uploadDate || (file as any).created_at || "";
  const uploader = file.uploader || (file as any).creator_display_name || "";
  const isList = viewMode === "list";

  const [isEditing, setIsEditing] = useState(false);
  const [editName, setEditName] = useState(fileName);
  const inputRef = useRef<HTMLInputElement>(null);

  const initialLiked = Boolean(
    file.isLiked ?? (file as any).is_bookmarked ?? false,
  );
  const [isLiked, setIsLiked] = useState<boolean>(initialLiked);
  const [isLiking, setIsLiking] = useState(false);

  useEffect(() => {
    setIsLiked(Boolean(file.isLiked ?? (file as any).is_bookmarked ?? false));
  }, [file.isLiked, (file as any).is_bookmarked]);

  useEffect(() => {
    if (isEditing && inputRef.current) {
      inputRef.current.focus();
      const dotIndex = editName.lastIndexOf(".");
      if (dotIndex > 0) {
        inputRef.current.setSelectionRange(0, dotIndex);
      } else {
        inputRef.current.select();
      }
    }
  }, [isEditing]);

  const handleStartEdit = (e: React.MouseEvent) => {
    e.stopPropagation();
    setEditName(fileName);
    setIsEditing(true);
  };

  const handleSave = () => {
    const trimmed = editName.trim();
    if (trimmed && trimmed !== fileName) {
      onEdit?.(file.id, trimmed);
    } else {
      setEditName(fileName);
    }
    setIsEditing(false);
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") handleSave();
    else if (e.key === "Escape") {
      setEditName(fileName);
      setIsEditing(false);
    }
  };

  const handleLikeClick = async (e: React.MouseEvent) => {
    e.stopPropagation();
    if (isLiking) return;

    if (onToggleLike) {
      setIsLiked((prev) => !prev);
      onToggleLike(file.id);
      return;
    }

    const prevLiked = isLiked;
    setIsLiked(!prevLiked);
    setIsLiking(true);
    try {
      await classDocumentsAPI.toggleBookmark(file.id);
    } catch (error) {
      setIsLiked(prevLiked);
      console.error("Lỗi yêu thích tài liệu:", error);
    } finally {
      setIsLiking(false);
    }
  };

  // Cụm nút thao tác: Edit -> Delete -> Tim (ngoài cùng bên phải)
  const renderActions = () =>
    !isEditing && (
      <div className="flex items-center gap-1 shrink-0">
        {onEdit && (
          <button
            type="button"
            onClick={handleStartEdit}
            className="p-1.5 text-[var(--ink-3)] hover:text-[var(--primary)] hover:bg-[var(--primary-fill)] rounded-[var(--r-sm)] transition-colors active:scale-95 opacity-70 sm:opacity-0 sm:group-hover:opacity-100"
            title="Đổi tên"
          >
            <Edit2 size={16} />
          </button>
        )}

        {onDelete && (
          <button
            type="button"
            onClick={(e) => {
              e.stopPropagation();
              if (
                window.confirm(
                  `Bạn có chắc chắn muốn xóa file "${fileName}" không?`,
                )
              ) {
                onDelete(file.id);
              }
            }}
            className="p-1.5 text-[var(--ink-3)] hover:text-[var(--red)] hover:bg-[var(--red-fill)] rounded-[var(--r-sm)] transition-colors active:scale-95 opacity-70 sm:opacity-0 sm:group-hover:opacity-100"
            title="Xóa file"
          >
            <Trash2 size={16} />
          </button>
        )}

        {/* Nút Tim nằm ngoài cùng bên phải */}
        <button
          type="button"
          disabled={isLiking}
          onClick={handleLikeClick}
          className={`p-1.5 rounded-[var(--r-sm)] transition-all active:scale-95 ${
            isLiked
              ? "text-[var(--red)] bg-[var(--red-fill)] opacity-100"
              : "text-[var(--ink-3)] hover:text-[var(--red)] hover:bg-[var(--red-fill)] opacity-70 sm:opacity-0 sm:group-hover:opacity-100"
          }`}
          title={isLiked ? "Bỏ yêu thích" : "Thêm vào yêu thích"}
        >
          <Heart size={16} className={isLiked ? "fill-current" : ""} />
        </button>
      </div>
    );

  // ==========================================
  // CHẾ ĐỘ LIST: NẰM GỌN TRÊN 1 HÀNG NGANG
  // ==========================================
  if (isList) {
    return (
      <div
        onClick={() => !isEditing && onClick()}
        className={`bg-[var(--bg-surface)] border ${
          isEditing
            ? "border-[var(--primary)] shadow-[var(--shadow-md)]"
            : "border-[var(--rule)] shadow-[var(--shadow-xs)]"
        } rounded-[var(--r-lg)] px-4 py-2.5 flex items-center justify-between gap-4 hover:shadow-[var(--shadow-sm)] hover:border-[var(--rule-md)] cursor-pointer transition-all duration-150 w-full group`}
      >
        <div className="flex items-center gap-3 min-w-0 flex-1">
          <div className="shrink-0 dark:brightness-[0.85] dark:saturate-[0.9]">
            <DocumentIcon
              extension={file.fileExtension || (file as any).fileType || ""}
              size={24}
            />
          </div>

          {isEditing ? (
            <input
              ref={inputRef}
              value={editName}
              onChange={(e) => setEditName(e.target.value)}
              onBlur={handleSave}
              onKeyDown={handleKeyDown}
              onClick={(e) => e.stopPropagation()}
              className="w-full max-w-md text-sm font-bold text-[var(--ink-1)] border border-[var(--primary)] rounded-[var(--r-sm)] px-1.5 py-0.5 outline-none focus:ring-2 focus:ring-[var(--primary-border)] bg-[var(--bg-surface-2)]"
            />
          ) : (
            <h4
              className="font-semibold text-sm text-[var(--ink-1)] truncate"
              title={fileName}
            >
              {fileName}
            </h4>
          )}
        </div>

        <div className="flex items-center gap-4 shrink-0">
          <div className="hidden sm:flex items-center gap-2 text-xs text-[var(--ink-3)]">
            {uploadDate && <span className="shrink-0">{uploadDate}</span>}
            {uploadDate && uploader && (
              <span className="text-[var(--rule-lg)]">•</span>
            )}
            {uploader && (
              <span className="truncate max-w-[140px]">{uploader}</span>
            )}
          </div>

          {renderActions()}
        </div>
      </div>
    );
  }

  // ==========================================
  // CHẾ ĐỘ GRID (MẶC ĐỊNH)
  // ==========================================
  return (
    <div
      onClick={() => !isEditing && onClick()}
      className={`bg-[var(--bg-surface)] border ${
        isEditing
          ? "border-[var(--primary)] shadow-[var(--shadow-md)]"
          : "border-[var(--rule)] shadow-[var(--shadow-xs)]"
      } rounded-[var(--r-xl)] p-4 flex flex-col gap-3 hover:shadow-[var(--shadow-md)] hover:border-[var(--rule-md)] cursor-pointer transition-all duration-200 hover:-translate-y-0.5 w-full group relative`}
    >
      <div className="flex justify-between items-start">
        <div className="dark:brightness-[0.85] dark:saturate-[0.9] transition-all duration-200">
          <DocumentIcon
            extension={file.fileExtension || (file as any).fileType || ""}
            size={32}
          />
        </div>

        {renderActions()}
      </div>

      <div>
        {isEditing ? (
          <input
            ref={inputRef}
            value={editName}
            onChange={(e) => setEditName(e.target.value)}
            onBlur={handleSave}
            onKeyDown={handleKeyDown}
            onClick={(e) => e.stopPropagation()}
            className="w-full text-sm font-bold text-[var(--ink-1)] border border-[var(--primary)] rounded-[var(--r-sm)] px-1.5 py-0.5 outline-none focus:ring-2 focus:ring-[var(--primary-border)] transition-shadow bg-[var(--bg-surface-2)]"
          />
        ) : (
          <h4
            className="font-bold text-sm text-[var(--ink-1)] truncate"
            title={fileName}
          >
            {fileName}
          </h4>
        )}

        <div className="flex items-center gap-2 mt-1">
          {uploadDate && (
            <p className="text-[11px] text-[var(--ink-3)] shrink-0">
              {uploadDate}
            </p>
          )}
          {uploadDate && uploader && (
            <span className="text-[11px] text-[var(--rule-lg)]">•</span>
          )}
          {uploader && (
            <p className="text-[11px] text-[var(--ink-3)] truncate">
              {uploader}
            </p>
          )}
        </div>
      </div>
    </div>
  );
};
