// src/features/classDocuments/components/FolderCard.tsx
import React, { useState, useRef, useEffect } from "react";
import { Folder, Edit2, Trash2 } from "lucide-react";
import type { FolderItem } from "@features/document/types";

interface FolderCardProps {
  folder: FolderItem;
  onClick: (folder: FolderItem) => void;
  onEdit: (id: string | number, newName: string) => void;
  onDelete: (id: string | number) => void;
}

export const FolderCard: React.FC<FolderCardProps> = ({ folder, onClick, onEdit, onDelete }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [editName, setEditName] = useState(folder.name);
  const inputRef = useRef<HTMLInputElement>(null);

  // Tự động focus và bôi đen chữ giống Windows khi bật chế độ sửa
  useEffect(() => {
    if (isEditing && inputRef.current) {
      inputRef.current.focus();
      inputRef.current.select(); 
    }
  }, [isEditing]);

  // Xử lý lưu tên
  const handleSave = () => {
    if (editName.trim() && editName.trim() !== folder.name) {
      onEdit(folder.id, editName.trim());
    } else {
      setEditName(folder.name); // Trả lại tên cũ nếu để trống
    }
    setIsEditing(false);
  };

  // Bắt sự kiện phím Enter và Escape
  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") {
      handleSave();
    } else if (e.key === "Escape") {
      setEditName(folder.name); // Hủy thao tác
      setIsEditing(false);
    }
  };

  return (
    <div 
      onClick={() => !isEditing && onClick(folder)} // Chỉ cho phép click mở thư mục nếu KHÔNG ĐANG sửa tên
      className={`bg-[var(--bg-surface)] border ${isEditing ? 'border-[#00B4D8] shadow-md' : 'border-[var(--rule)]'} rounded-xl p-4 flex flex-col gap-3 hover:shadow-md cursor-pointer transition-all hover:-translate-y-0.5 w-full sm:w-[200px] group relative`}
    >
      <div className="flex justify-between items-start">
        <Folder className="text-[#00B4D8]" fill="currentColor" size={32} />
        
        {!isEditing && (
          <div className="flex items-center gap-1 opacity-70 sm:opacity-0 sm:group-hover:opacity-100 transition-opacity">
            {/* Nút sửa */}
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

            {/* Nút Xóa */}
            <button
              onClick={(e) => {
                e.stopPropagation();
                // Bật thông báo xác nhận trước khi xóa
                if (window.confirm(`Bạn có chắc chắn muốn xóa thư mục "${folder.name}" và toàn bộ nội dung bên trong không?`)) {
                  onDelete(folder.id);
                }
              }}
              className="p-1.5 text-[var(--ink-3)] hover:text-red-500 hover:bg-red-50 rounded-md transition-colors active:scale-95"
              title="Xóa thư mục"
            >
              <Trash2 size={16} />
            </button>
          </div>
        )}
      </div>

      <div>
        {/* Render Input nếu đang sửa, ngược lại render thẻ H4 */}
        {isEditing ? (
          <input
            ref={inputRef}
            value={editName}
            onChange={(e) => setEditName(e.target.value)}
            onBlur={handleSave} // Bấm click ra ngoài (mất focus) -> Tự động lưu
            onKeyDown={handleKeyDown} // Bấm Enter/Esc
            onClick={(e) => e.stopPropagation()} // Chặn click xuyên qua làm mở thư mục
            className="w-full text-sm font-bold text-[var(--ink-1)] border border-[#00B4D8] rounded px-1 py-0.5 outline-none focus:ring-2 focus:ring-blue-100 transition-shadow bg-white"
          />
        ) : (
          <h4 className="font-bold text-sm text-[var(--ink-1)] truncate" title={folder.name}>
            {folder.name}
          </h4>
        )}
        <p className="text-[11px] text-[var(--ink-3)] mt-1">{folder.itemCount} mục</p>
      </div>
    </div>
  );
};