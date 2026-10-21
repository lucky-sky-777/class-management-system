// src/features/classDocuments/components/FilePreviewCard.tsx
import React, { useState, useRef, useEffect } from "react";
import { Edit2, Trash2 } from "lucide-react";
import { DocumentIcon } from "@/shared/components/icons/DocumentIcon";
import type { FileItem } from "@/features/document/types";

interface FilePreviewCardProps {
  file: FileItem;
  onClick: () => void;
  onEdit?: (id: string | number, newName: string) => void;
  onDelete?: (id: string | number) => void;
}

export const FilePreviewCard: React.FC<FilePreviewCardProps> = ({
  file,
  onClick,
  onDelete,
  onEdit,
}) => {
  const fileName = file.name || (file as any).fileName || "Tài liệu không tên";
  const [isEditing, setIsEditing] = useState(false);
  const [editName, setEditName] = useState(fileName);
  const inputRef = useRef<HTMLInputElement>(null);

  // Chỉ focus và chọn vùng chữ khi người dùng kích hoạt chỉnh sửa
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
    setEditName(fileName); // Cập nhật tên mới nhất ngay trong event handler
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

  return (
    <div
      onClick={() => !isEditing && onClick()}
      className={`bg-[var(--bg-surface)] border ${
        isEditing ? "border-[#00B4D8] shadow-md" : "border-[var(--rule)]"
      } rounded-xl p-4 flex flex-col gap-3 hover:shadow-md cursor-pointer transition-all hover:-translate-y-0.5 w-full group relative`}
    >
      <div className="flex justify-between items-start">
        <DocumentIcon
          extension={file.fileExtension || (file as any).fileType || ""}
          size={32}
        />

        {!isEditing && (onEdit || onDelete) && (
          <div className="flex items-center gap-1 opacity-70 sm:opacity-0 sm:group-hover:opacity-100 transition-opacity">
            {onEdit && (
              <button
                type="button"
                onClick={handleStartEdit}
                className="p-1.5 text-[var(--ink-3)] hover:text-[#00B4D8] hover:bg-[#E5F9FD] rounded-md transition-colors active:scale-95"
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
                className="p-1.5 text-[var(--ink-3)] hover:text-red-500 hover:bg-red-50 rounded-md transition-colors active:scale-95"
                title="Xóa file"
              >
                <Trash2 size={16} />
              </button>
            )}
          </div>
        )}
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
            className="w-full text-sm font-bold text-[var(--ink-1)] border border-[#00B4D8] rounded px-1 py-0.5 outline-none focus:ring-2 focus:ring-blue-100 transition-shadow bg-white"
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
          <p className="text-[11px] text-[var(--ink-3)]">{file.size || ""}</p>
          {file.uploader && (
            <>
              <span className="text-[11px] text-[var(--rule)]">•</span>
              <p className="text-[11px] text-[var(--ink-3)] truncate">
                {file.uploader}
              </p>
            </>
          )}
        </div>
      </div>
    </div>
  );
};
