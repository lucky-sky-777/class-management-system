// src/features/classDocuments/components/DocumentToolbar.tsx
import React, { useState } from "react";
import {
  Folder,
  ChevronRight,
  Clock,
  Search,
  Plus,
  Upload,
} from "lucide-react";
import type { FolderItem } from "@features/document/types";
import { UploadDocumentModal } from "@features/document/components/UploadDocumentModal";
import { CreateFolderModal } from "@features/document/components/CreateFolderModal";

interface DocumentToolbarProps {
  currentFolder: FolderItem | null;
  folders: FolderItem[];
  onGoToRoot: () => void;
  onSearch: (term: string) => void;
  onCreateFolder: (name: string) => void;
}

export const DocumentToolbar: React.FC<DocumentToolbarProps> = ({
  currentFolder,
  folders,
  onGoToRoot,
  onSearch,
  onCreateFolder,
}) => {
  const [isUpLoadModalOpen, setIsUpLoadModalOpen] = useState(false);
  const [isCreateFolderModalOpen, setIsCreateFolderModalOpen] = useState(false);

  return (
    <>
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-8">
        {/* Breadcrumbs */}
        <div className="flex items-center gap-2 text-[18px] font-bold text-[var(--ink-1)]">
          <Folder className="text-[#00B4D8]" fill="currentColor" size={24} />
          <span
            onClick={onGoToRoot}
            className={`cursor-pointer hover:underline transition-colors ${!currentFolder ? "text-[var(--primary)]" : "text-[var(--ink-3)] hover:text-[var(--ink-1)]"}`}
          >
            Gốc
          </span>

          {currentFolder && (
            <>
              <ChevronRight size={20} className="text-[var(--ink-3)]" />
              <span className="text-[var(--ink-1)] truncate max-w-[150px] sm:max-w-none">
                {currentFolder.name}
              </span>
            </>
          )}
        </div>

        <div className="flex items-center justify-end gap-2 sm:gap-4 flex-wrap w-full md:w-auto">
          {/* 1. Lịch sử */}
          <div className="relative cursor-pointer text-[var(--ink-3)] hover:text-[var(--ink-1)] transition-colors mr-1 sm:mr-0">
            <Clock size={20} />
            <span className="absolute -top-1 -right-1.5 bg-red-500 text-white text-[9px] font-bold px-1 rounded-full">
              6
            </span>
          </div>

          {/* Tìm kiếm */}
          <div className="relative w-full sm:w-48 lg:w-60 order-last sm:order-none mt-2 sm:mt-0">
            <Search
              size={14}
              className="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--ink-3)]"
            />
            <input
              type="text"
              placeholder="Tìm trong thư mục này..."
              onChange={(e) => onSearch(e.target.value)}
              className="w-full pl-8 pr-3 py-2 sm:py-1.5 text-sm border border-[var(--rule)] rounded-[var(--r-md)] bg-white focus:outline-none focus:border-[var(--primary)] transition-colors shadow-sm"
            />
          </div>

          {/* 3. Nút Tạo thư mục */}
          <button onClick={() => setIsCreateFolderModalOpen(true)} className="flex items-center gap-1.5 px-3 py-2 sm:py-1.5 text-sm font-semibold text-[var(--primary)] border border-[var(--primary)] rounded-[var(--r-md)] hover:bg-[var(--primary-fill)] transition-colors bg-white active:scale-95">
            <Plus size={16} />
            <span className="hidden sm:inline">Tạo thư mục</span>
            <span className="sm:hidden">Thư mục</span>
          </button>

          {/* 4. Nút Tải lên */}
          <button
            onClick={() => setIsUpLoadModalOpen(true)}
            className="flex items-center gap-1.5 px-3 py-2 sm:py-1.5 text-sm font-semibold text-white bg-[#00B4D8] border border-[#00B4D8] rounded-[var(--r-md)] hover:bg-[#0096C7] transition-colors shadow-sm active:scale-95"
          >
            <Upload size={16} /> Tải lên
          </button>
        </div>
      </div>

      <UploadDocumentModal
        isOpen={isUpLoadModalOpen}
        onClose={() => setIsUpLoadModalOpen(false)}
        folders={folders}
      />

      <CreateFolderModal 
        isOpen={isCreateFolderModalOpen}
        onClose={() => setIsCreateFolderModalOpen(false)}
        onSubmit={onCreateFolder}
      />
    </>
  );
};
