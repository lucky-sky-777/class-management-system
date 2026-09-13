// src/features/classDocuments/components/DocumentToolbar.tsx
import React from "react";
import { Folder, ChevronRight, Clock, Search, Plus, Upload } from "lucide-react";
import type { FolderItem } from "../types";

interface DocumentToolbarProps {
  currentFolder: FolderItem | null;
  onGoToRoot: () => void;
  onSearch: (term: string) => void;
}

export const DocumentToolbar: React.FC<DocumentToolbarProps> = ({ 
  currentFolder, 
  onGoToRoot, 
  onSearch 
}) => {
  return (
    <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-8">
      {/* THAY ĐỔI: Breadcrumbs động */}
      <div className="flex items-center gap-2 text-[18px] font-bold text-[var(--ink-1)]">
        <Folder className="text-[#00B4D8]" fill="currentColor" size={24} />
        <span 
          onClick={onGoToRoot}
          className={`cursor-pointer hover:underline transition-colors ${!currentFolder ? 'text-[var(--primary)]' : 'text-[var(--ink-3)] hover:text-[var(--ink-1)]'}`}
        >
          Gốc
        </span>
        
        {currentFolder && (
          <>
            <ChevronRight size={20} className="text-[var(--ink-3)]" />
            <span className="text-[var(--ink-1)]">{currentFolder.name}</span>
          </>
        )}
      </div>

      {/* Công cụ (Giữ nguyên như cũ) */}
      <div className="flex items-center gap-3 sm:gap-4 flex-wrap">
        <div className="relative cursor-pointer text-[var(--ink-3)] hover:text-[var(--ink-1)] transition-colors">
          <Clock size={20} />
          <span className="absolute -top-1 -right-1.5 bg-red-500 text-white text-[9px] font-bold px-1 rounded-full">6</span>
        </div>
        <div className="relative w-full sm:w-48 lg:w-60 order-last sm:order-none">
          <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--ink-3)]" />
          <input type="text" placeholder="Tìm trong thư mục này..." onChange={(e) => onSearch(e.target.value)} className="w-full pl-8 pr-3 py-1.5 text-sm border border-[var(--rule)] rounded-[var(--r-md)] bg-white focus:outline-none focus:border-[var(--primary)] transition-colors shadow-sm" />
        </div>
        <button className="flex items-center gap-1.5 px-3 py-1.5 text-sm font-semibold text-[var(--primary)] border border-[var(--primary)] rounded-[var(--r-md)] hover:bg-[var(--primary-fill)] transition-colors bg-white">
          <Plus size={16} /> Tạo thư mục
        </button>
        <button className="flex items-center gap-1.5 px-3 py-1.5 text-sm font-semibold text-white bg-[#00B4D8] border border-[#00B4D8] rounded-[var(--r-md)] hover:bg-[#0096C7] transition-colors shadow-sm">
          <Upload size={16} /> Tải lên
        </button>
      </div>
    </div>
  );
};