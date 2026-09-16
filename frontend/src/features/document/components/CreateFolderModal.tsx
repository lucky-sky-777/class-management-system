// src/features/classDocuments/components/CreateFolderModal.tsx
import React, { useState } from "react";
import { X, FolderPlus } from "lucide-react";

interface CreateFolderModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (folderName: string) => void;
}

export const CreateFolderModal: React.FC<CreateFolderModalProps> = ({ 
  isOpen, 
  onClose, 
  onSubmit 
}) => {
  const [folderName, setFolderName] = useState("");

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (folderName.trim()) {
      onSubmit(folderName.trim());
      setFolderName(""); // Reset form
      onClose(); // Đóng modal
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center bg-black/50 backdrop-blur-sm animate-in fade-in duration-200">
      <div className="bg-white w-full sm:max-w-[400px] rounded-t-3xl sm:rounded-2xl shadow-xl flex flex-col animate-in slide-in-from-bottom-full sm:slide-in-from-bottom-0 sm:zoom-in-95 duration-300">
        
        {/* Nút kéo trên Mobile */}
        <div className="w-full flex justify-center pt-3 pb-1 sm:hidden">
          <div className="w-12 h-1.5 bg-gray-300 rounded-full"></div>
        </div>

        <div className="flex items-center justify-between px-5 pb-4 pt-2 sm:pt-5 sm:pb-5 border-b border-[var(--rule)]">
          <h2 className="text-lg sm:text-xl font-bold text-[var(--ink-1)] flex items-center gap-2">
            <FolderPlus className="text-[#00B4D8]" size={24} /> Tạo thư mục mới
          </h2>
          <button onClick={onClose} className="text-[var(--ink-3)] hover:text-[var(--ink-1)] transition-colors p-1 rounded-full hover:bg-gray-100">
            <X size={24} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-5 sm:p-6 flex flex-col gap-5">
          <div>
            <label className="block text-[13px] sm:text-sm font-semibold text-[var(--ink-2)] mb-2">
              Tên thư mục
            </label>
            <input
              type="text"
              autoFocus
              value={folderName}
              onChange={(e) => setFolderName(e.target.value)}
              placeholder="Nhập tên thư mục..."
              className="w-full px-4 py-3 sm:py-2.5 text-sm border border-[var(--rule)] rounded-lg focus:outline-none focus:border-[#00B4D8] text-[var(--ink-1)]"
            />
          </div>
          
          <div className="flex gap-3 mt-2">
            <button 
              type="button" 
              onClick={onClose} 
              className="flex-1 px-4 py-3 sm:py-2.5 text-sm font-semibold text-[var(--ink-2)] border border-[var(--rule)] rounded-xl sm:rounded-lg hover:bg-gray-100 transition-colors bg-white active:scale-95"
            >
              Hủy
            </button>
            <button 
              type="submit" 
              disabled={!folderName.trim()}
              className="flex-1 px-4 py-3 sm:py-2.5 text-sm font-semibold text-white bg-[#00B4D8] rounded-xl sm:rounded-lg hover:bg-[#0096C7] transition-colors disabled:opacity-50 disabled:cursor-not-allowed active:scale-95"
            >
              Tạo mới
            </button>
          </div>
        </form>

      </div>
    </div>
  );
};