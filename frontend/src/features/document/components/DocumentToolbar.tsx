// src/features/classDocuments/components/DocumentToolbar.tsx
import React, { useState } from "react";
import {
  Folder,
  ChevronRight,
  Search,
  Plus,
  Upload,
  Sparkles,
  LayoutGrid,
  List,
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
  onUploadDocument?: (
    folderId: string,
    files: File[],
    description: string,
  ) => Promise<void>;
  onOpenAIChat?: () => void;
  isViewingFile?: boolean;
  viewMode: "grid" | "list";
  onViewModeChange: (mode: "grid" | "list") => void;
}

export const DocumentToolbar: React.FC<DocumentToolbarProps> = ({
  currentFolder,
  folders,
  onGoToRoot,
  onSearch,
  onCreateFolder,
  onUploadDocument,
  onOpenAIChat,
  isViewingFile = false,
  viewMode,
  onViewModeChange,
}) => {
  const [isUpLoadModalOpen, setIsUpLoadModalOpen] = useState(false);
  const [isCreateFolderModalOpen, setIsCreateFolderModalOpen] = useState(false);

  return (
    <>
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-8">
        {/* Breadcrumbs */}
        <div className="flex items-center gap-2 text-[18px] font-bold text-[var(--ink-1)] shrink-0">
          <Folder className="text-[var(--sky)]" fill="currentColor" size={24} />
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

        <div className="flex items-center justify-end gap-2 sm:gap-3 flex-wrap w-full md:w-auto">
          {/* NÚT CHUYỂN CHẾ ĐỘ XEM (LƯỚI / DANH SÁCH) - Chỉ hiện khi không xem file */}
          {!isViewingFile && (
            <div className="flex bg-[var(--bg-surface-2)] border border-[var(--rule)] rounded-[var(--r-md)] overflow-hidden shadow-[var(--shadow-xs)] mr-1">
              <button
                onClick={() => onViewModeChange("grid")}
                title="Chế độ Lưới"
                className={`p-2 transition-colors duration-200 ${
                  viewMode === "grid"
                    ? "bg-[var(--primary-fill)] text-[var(--primary)] font-medium"
                    : "text-[var(--ink-3)] hover:text-[var(--ink-1)] hover:bg-[var(--bg-surface)]"
                }`}
              >
                <LayoutGrid size={16} />
              </button>
              <button
                onClick={() => onViewModeChange("list")}
                title="Chế độ Danh sách"
                className={`p-2 transition-colors duration-200 ${
                  viewMode === "list"
                    ? "bg-[var(--primary-fill)] text-[var(--primary)] font-medium"
                    : "text-[var(--ink-3)] hover:text-[var(--ink-1)] hover:bg-[var(--bg-surface)]"
                }`}
              >
                <List size={16} />
              </button>
            </div>
          )}

          {/* Tìm kiếm */}
          <div className="relative w-full sm:w-48 lg:w-56 order-last sm:order-none mt-2 sm:mt-0">
            <Search
              size={14}
              className="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--ink-3)]"
            />
            <input
              type="text"
              placeholder="Tìm trong thư mục này..."
              onChange={(e) => onSearch(e.target.value)}
              className="w-full pl-8 pr-3 py-2 sm:py-1.5 text-sm border border-[var(--rule)] rounded-[var(--r-md)] bg-[var(--bg-surface)] text-[var(--ink-1)] placeholder-[var(--ink-3)] focus:outline-none focus:border-[var(--primary)] focus:ring-1 focus:ring-[var(--primary)] transition-all shadow-[var(--shadow-xs)]"
            />
          </div>

          {/* Nút Tạo thư mục */}
          <button
            onClick={() => setIsCreateFolderModalOpen(true)}
            className="flex items-center gap-1.5 px-3 py-2 sm:py-1.5 text-sm font-semibold text-[var(--primary)] border border-[var(--primary)] rounded-[var(--r-md)] hover:bg-[var(--primary-fill)] transition-colors bg-[var(--bg-surface)] active:scale-95"
          >
            <Plus size={16} />
            <span className="hidden sm:inline">Tạo thư mục</span>
            <span className="sm:hidden">Thư mục</span>
          </button>

          {/* Nút Tải lên (Dùng Primary/Sky để nổi bật hành động chính) */}
          <button
            onClick={() => setIsUpLoadModalOpen(true)}
            className="flex items-center gap-1.5 px-3 py-2 sm:py-1.5 text-sm font-semibold text-white bg-[var(--primary)] border border-[var(--primary)] rounded-[var(--r-md)] hover:bg-[var(--primary-hover)] transition-colors shadow-[var(--shadow-sm)] active:scale-95"
          >
            <Upload size={16} /> Tải lên
          </button>

          {/* Nút Hỏi đáp AI toàn lớp (Dùng Gradient từ Violet để thể hiện tính năng AI xuất sắc) */}
          {!isViewingFile && onOpenAIChat && (
            <button
              onClick={onOpenAIChat}
              title="Hỏi AI về toàn bộ tài liệu"
              className="flex items-center gap-1.5 px-3 py-2 sm:py-1.5 text-sm font-semibold text-white bg-gradient-to-r from-[var(--violet)] to-[var(--primary)] hover:opacity-90 border border-[var(--violet-border)] rounded-[var(--r-md)] transition-opacity shadow-[var(--shadow-sm)] active:scale-95"
            >
              <Sparkles size={16} />
              <span className="hidden lg:inline">Hỏi AI</span>
              <span className="inline lg:hidden">AI</span>
            </button>
          )}
        </div>
      </div>

      <UploadDocumentModal
        isOpen={isUpLoadModalOpen}
        onClose={() => setIsUpLoadModalOpen(false)}
        folders={folders}
        defaultFolderId={currentFolder?.id}
        onUpload={onUploadDocument}
      />

      <CreateFolderModal
        isOpen={isCreateFolderModalOpen}
        onClose={() => setIsCreateFolderModalOpen(false)}
        onSubmit={onCreateFolder}
      />
    </>
  );
};
