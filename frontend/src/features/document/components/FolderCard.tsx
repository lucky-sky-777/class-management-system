// src/features/classDocuments/components/FolderCard.tsx
import React, { useState, useRef, useEffect } from "react";
import { Folder, Edit2, Trash2 } from "lucide-react";
import type { FolderItem } from "@features/document/types";

interface FolderCardProps {
  folder: FolderItem;
  onClick: (folder: FolderItem) => void;
  onEdit: (id: string | number, newName: string) => void;
  onDelete: (id: string | number) => void;
  viewMode?: "grid" | "list";
}

export const FolderCard: React.FC<FolderCardProps> = ({
  folder,
  onClick,
  onEdit,
  onDelete,
  viewMode = "grid",
}) => {
  const [isEditing, setIsEditing] = useState(false);
  const [editName, setEditName] = useState(folder.name);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (isEditing && inputRef.current) {
      inputRef.current.focus();
      inputRef.current.select();
    }
  }, [isEditing]);

  const handleSave = () => {
    if (editName.trim() && editName.trim() !== folder.name) {
      onEdit(folder.id, editName.trim());
    } else {
      setEditName(folder.name);
    }
    setIsEditing(false);
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") {
      handleSave();
    } else if (e.key === "Escape") {
      setEditName(folder.name);
      setIsEditing(false);
    }
  };

  // -------------------------------------------------------------
  // RENDER CHẾ ĐỘ LƯỚI (GRID)
  // -------------------------------------------------------------
  if (viewMode === "grid") {
    return (
      <div
        onClick={() => !isEditing && onClick(folder)}
        className={`bg-[var(--bg-surface)] border ${
          isEditing 
            ? "border-[var(--primary)] shadow-[var(--shadow-md)]" 
            : "border-[var(--rule)] shadow-[var(--shadow-sm)]"
        } rounded-[var(--r-xl)] p-4 flex flex-col gap-3 hover:border-[var(--rule-md)] hover:shadow-[var(--shadow-md)] cursor-pointer transition-all duration-200 hover:-translate-y-0.5 group relative`}
      >
        <div className="flex justify-between items-start">
          {/* Dùng màu Sky cho thư mục (Info/System notification tone) */}
          <Folder className="text-[var(--sky)]" fill="currentColor" size={36} />

          {!isEditing && (
            <div className="flex items-center gap-1 opacity-70 sm:opacity-0 sm:group-hover:opacity-100 transition-opacity">
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  setIsEditing(true);
                }}
                className="p-1.5 text-[var(--ink-3)] hover:text-[var(--primary)] hover:bg-[var(--primary-fill)] rounded-[var(--r-sm)] transition-colors active:scale-95"
                title="Đổi tên"
              >
                <Edit2 size={16} />
              </button>
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  if (
                    window.confirm(
                      `Bạn có chắc chắn muốn xóa thư mục "${folder.name}" và toàn bộ nội dung bên trong không?`
                    )
                  ) {
                    onDelete(folder.id);
                  }
                }}
                className="p-1.5 text-[var(--ink-3)] hover:text-[var(--red)] hover:bg-[var(--red-fill)] rounded-[var(--r-sm)] transition-colors active:scale-95"
                title="Xóa thư mục"
              >
                <Trash2 size={16} />
              </button>
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
              className="w-full text-sm font-semibold text-[var(--ink-1)] border border-[var(--primary)] rounded-[var(--r-sm)] px-1.5 py-0.5 outline-none bg-[var(--bg-surface-2)] focus:ring-2 focus:ring-[var(--primary-border)] transition-shadow"
            />
          ) : (
            <h4
              className="font-semibold text-[15px] text-[var(--ink-1)] truncate"
              title={folder.name}
            >
              {folder.name}
            </h4>
          )}
          <p className="text-[12px] text-[var(--ink-2)] mt-1">
            {folder.itemCount} mục
          </p>
        </div>
      </div>
    );
  }

  // -------------------------------------------------------------
  // RENDER CHẾ ĐỘ DANH SÁCH (LIST)
  // -------------------------------------------------------------
  return (
    <div
      onClick={() => !isEditing && onClick(folder)}
      className={`bg-[var(--bg-surface)] border-b ${
        isEditing 
          ? "border-[var(--primary)] bg-[var(--primary-fill)]" 
          : "border-[var(--rule)] hover:bg-[var(--bg-surface-2)]"
      } p-3 flex items-center gap-4 cursor-pointer transition-colors duration-200 group relative`}
    >
      <Folder
        className="text-[var(--sky)] shrink-0"
        fill="currentColor"
        size={24}
      />

      <div className="flex-1 min-w-0 flex items-center">
        {isEditing ? (
          <input
            ref={inputRef}
            value={editName}
            onChange={(e) => setEditName(e.target.value)}
            onBlur={handleSave}
            onKeyDown={handleKeyDown}
            onClick={(e) => e.stopPropagation()}
            className="w-[50%] max-w-[300px] text-sm font-medium text-[var(--ink-1)] border border-[var(--primary)] rounded-[var(--r-sm)] px-2 py-1 outline-none bg-[var(--bg-surface)] focus:ring-2 focus:ring-[var(--primary-border)]"
          />
        ) : (
          <h4
            className="font-medium text-[14px] text-[var(--ink-1)] truncate"
            title={folder.name}
          >
            {folder.name}
          </h4>
        )}
      </div>

      {/* Thông tin phụ trợ ở chế độ List */}
      <div className="hidden sm:block w-[100px] text-[13px] text-[var(--ink-3)] shrink-0 text-right pr-4 border-r border-[var(--rule)]">
        {folder.itemCount} mục
      </div>

      {/* Ngày tháng */}
      <div className="hidden md:block w-[120px] text-[13px] text-[var(--ink-3)] shrink-0 text-center">
        ---
      </div>

      {/* Hành động */}
      <div className="w-[80px] shrink-0 flex justify-end items-center gap-1 opacity-100 sm:opacity-0 sm:group-hover:opacity-100 transition-opacity">
        {!isEditing && (
          <>
            <button
              onClick={(e) => {
                e.stopPropagation();
                setIsEditing(true);
              }}
              className="p-1.5 text-[var(--ink-3)] hover:text-[var(--primary)] hover:bg-[var(--primary-fill)] rounded-[var(--r-sm)] transition-colors"
              title="Đổi tên"
            >
              <Edit2 size={16} />
            </button>
            <button
              onClick={(e) => {
                e.stopPropagation();
                if (
                  window.confirm(
                    `Bạn có chắc chắn muốn xóa thư mục "${folder.name}" và toàn bộ nội dung bên trong không?`
                  )
                ) {
                  onDelete(folder.id);
                }
              }}
              className="p-1.5 text-[var(--ink-3)] hover:text-[var(--red)] hover:bg-[var(--red-fill)] rounded-[var(--r-sm)] transition-colors"
              title="Xóa thư mục"
            >
              <Trash2 size={16} />
            </button>
          </>
        )}
      </div>
    </div>
  );
};