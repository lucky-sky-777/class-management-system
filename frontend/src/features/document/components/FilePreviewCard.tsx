// src/features/classDocuments/components/FilePreviewCard.tsx
import React, { useState, useRef, useEffect } from "react";
import { Edit2, Trash2 } from "lucide-react";
import { DocumentIcon } from "@/shared/components/icons/DocumentIcon";
import type { FileItem } from "@/features/document/types";

interface FilePreviewCardProps {
  file: FileItem;
  onClick: () => void;
  onEdit: (id: string | number, newName: string) => void;
  onDelete: (id: string | number) => void;
}

export const FilePreviewCard: React.FC<FilePreviewCardProps> = ({
  file,
  onClick,
  onDelete,
  onEdit,
}) => {
  const [isEditing, setIsEditing] = useState(false);
  const [editName, setEditName] = useState(file.name);
  const inputRef = useRef<HTMLInputElement>(null);

  // Tự động focus và bôi đen tên file khi bấm nút Sửa
  useEffect(() => {
    if (isEditing && inputRef.current) {
      inputRef.current.focus();
      // Tùy chọn nâng cao: Chỉ bôi đen phần tên, chừa lại phần mở rộng (.pdf, .docx)
      const dotIndex = file.name.lastIndexOf(".");
      if (dotIndex > 0) {
        inputRef.current.setSelectionRange(0, dotIndex);
      } else {
        inputRef.current.select();
      }
    }
  }, [isEditing, file.name]);

  const handleSave = () => {
    if (editName.trim() && editName.trim() !== file.name) {
      onEdit(file.id, editName.trim());
    } else {
      setEditName(file.name);
    }
    setIsEditing(false);
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") handleSave();
    else if (e.key === "Escape") {
      setEditName(file.name);
      setIsEditing(false);
    }
  };

  return (
    <div
      onClick={() => !isEditing && onClick()}
      className={`bg-[var(--bg-surface)] border ${isEditing ? "border-[#00B4D8] shadow-md" : "border-[var(--rule)]"} rounded-xl p-4 flex flex-col gap-3 hover:shadow-md cursor-pointer transition-all hover:-translate-y-0.5 w-full group relative`}
    >
      <div className="flex justify-between items-start">
        {/* Render icon tương ứng với loại file */}
        <DocumentIcon extension={file.fileExtension} size={32} />

        {/* Nhóm 2 nút công cụ Sửa & Xóa */}
        {!isEditing && (
          <div className="flex items-center gap-1 opacity-70 sm:opacity-0 sm:group-hover:opacity-100 transition-opacity">
            <button
              onClick={(e) => {
                e.stopPropagation();
                setIsEditing(true);
              }}
              className="p-1.5 text-[var(--ink-3)] hover:text-[#00B4D8] hover:bg-[#E5F9FD] rounded-md transition-colors active:scale-95"
              title="Đổi tên"
            >
              <Edit2 size={16} />
            </button>

            <button
              onClick={(e) => {
                e.stopPropagation();
                if (
                  window.confirm(
                    `Bạn có chắc chắn muốn xóa file "${file.name}" không?`,
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
          </div>
        )}
      </div>

      <div>
        {/* Inline Edit Input */}
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
            title={file.name}
          >
            {file.name}
          </h4>
        )}

        {/* Thông tin phụ của File */}
        <div className="flex items-center gap-2 mt-1">
          <p className="text-[11px] text-[var(--ink-3)]">{file.size}</p>
          <span className="text-[11px] text-[var(--rule)]">•</span>
          <p className="text-[11px] text-[var(--ink-3)] truncate">
            {file.uploader}
          </p>
        </div>
      </div>
    </div>
  );
};
