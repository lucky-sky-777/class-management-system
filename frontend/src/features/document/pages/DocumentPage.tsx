// src/features/classDocuments/pages/ClassDocumentsTab.tsx
import React, { useState } from "react";
import { Loader2, FolderOpen } from "lucide-react";
import { useClassDocuments } from "@features/document/hooks/useClassDocuments";
import { useDocumentDetail } from "@features/document/hooks/useDocumentDetail";
import { DocumentToolbar } from "@features/document/components/DocumentToolbar";
import { FolderCard } from "@features/document/components/FolderCard";
import { FilePreviewCard } from "@features/document/components/FilePreviewCard";
import { DocumentPreview } from "@features/document/components/DocumentPreview";
import { DocumentSidebar } from "@features/document/components/DocumentSidebar";
import type { FileItem } from "@features/document/types";
import { DocumentViewerModal } from "@features/document/components/DocumentViewerModal";

export const DocumentPage = () => {
  const {
    folders,
    recentFiles,
    isLoading: isPageLoading,
    currentFolder,
    folderFiles,
    isFolderLoading,
    handleOpenFolder,
    handleGoToRoot,
    handleSearch,
    handleCreateFolder,
    handleRenameFolder,
    handleDeleteFolder,
    handleRenameFile,
    handleDeleteFile,
  } = useClassDocuments();

  //  State điều hướng: Đang xem chi tiết file nào? (null = đang ở danh sách)
  const [selectedFileId, setSelectedFileId] = useState<string | number | null>(
    null,
  );
  const [isViewerOpen, setIsViewerOpen] = useState(false);
  // Hook quản lý chi tiết 1 file cụ thể
  const {
    fileDetail,
    isLoading: isFileDetailLoading,
    handleToggleLike,
    handleDownload,
  } = useDocumentDetail(selectedFileId);

  // Hàm xử lý khi bấm vào 1 file
  const handleOpenFile = (file: FileItem) => {
    setSelectedFileId(file.id);
  };

  // Hàm xử lý khi bấm nút "Gốc" trên Toolbar
  const handleToolbarGoToRoot = () => {
    setSelectedFileId(null); // Thoát khỏi chế độ xem file
    handleGoToRoot(); // Trở về trang gốc
  };

  // Render lúc mới vào trang
  if (isPageLoading) {
    return (
      <div className="py-20 flex justify-center items-center">
        <Loader2 className="animate-spin text-[var(--ink-3)]" size={32} />
      </div>
    );
  }

  return (
    <div className="py-4">
      <DocumentToolbar
        currentFolder={currentFolder}
        folders={folders}
        onGoToRoot={handleToolbarGoToRoot}
        onSearch={handleSearch}
        onCreateFolder={handleCreateFolder}
      />

      {/* ĐIỀU KIỆN 1: Nếu ĐANG XEM FILE -> Render trang Chi tiết 2 cột */}
      {selectedFileId && fileDetail ? (
        isFileDetailLoading ? (
          <div className="py-20 flex justify-center items-center">
            <Loader2 className="animate-spin text-[var(--ink-3)]" size={32} />
          </div>
        ) : (
          <div className="flex flex-col lg:flex-row gap-6 lg:gap-8 animate-in fade-in slide-in-from-bottom-4 duration-300">
            <DocumentPreview
              file={fileDetail}
              onOpenFullScreen={() => setIsViewerOpen(true)}
            />
            <DocumentSidebar
              file={fileDetail}
              onDownload={handleDownload}
              onToggleLike={handleToggleLike}
              onEdit={() => console.log("Mở form edit chi tiết")}
              onDelete={() => {
                if (window.confirm("Bạn có chắc chắn muốn xóa tài liệu này?")) {
                  handleDeleteFile(fileDetail.id);
                  setSelectedFileId(null); // Xóa xong thì quay ra ngoài
                }
              }}
            />
          </div>
        )
      ) : /* ĐIỀU KIỆN 2: Nếu KHÔNG ở trong thư mục nào -> Render trang Gốc */
      !currentFolder ? (
        <>
          {folders.length > 0 && (
            <section className="mb-10">
              <h3 className="text-sm font-bold text-[var(--ink-3)] uppercase tracking-wider mb-4">
                Thư mục
              </h3>
              <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
                {folders.map((folder) => (
                  <FolderCard
                    key={folder.id}
                    folder={folder}
                    onClick={handleOpenFolder}
                    onEdit={handleRenameFolder}
                    onDelete={handleDeleteFolder}
                  />
                ))}
              </div>
            </section>
          )}

          {recentFiles.length > 0 && (
            <section>
              <h3 className="text-sm font-bold text-[var(--ink-3)] uppercase tracking-wider mb-4">
                Tệp mở gần đây
              </h3>
              <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
                {recentFiles.map((file) => (
                  <FilePreviewCard
                    key={file.id}
                    file={file}
                    onClick={() => handleOpenFile(file)} // Click mở file
                    onEdit={handleRenameFile} // Edit inline
                    onDelete={handleDeleteFile} // Xóa file
                  />
                ))}
              </div>
            </section>
          )}
        </>
      ) : (
        /* ĐIỀU KIỆN 3: Nếu ĐANG Ở TRONG THƯ MỤC -> Render danh sách file của thư mục đó */
        <section className="animate-in fade-in slide-in-from-right-4 duration-300">
          {isFolderLoading ? (
            <div className="py-10 flex justify-center items-center">
              <Loader2
                className="animate-spin text-[var(--primary)]"
                size={28}
              />
            </div>
          ) : folderFiles.length > 0 ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
              {folderFiles.map((file) => (
                <FilePreviewCard
                  key={file.id}
                  file={file}
                  onClick={() => handleOpenFile(file)} // Click mở file
                  onEdit={handleRenameFile} // Edit inline
                  onDelete={handleDeleteFile} // Xóa file
                />
              ))}
            </div>
          ) : (
            <div className="text-center py-20 flex flex-col items-center gap-3">
              <FolderOpen size={48} className="text-[var(--ink-4)]" />
              <p className="text-[var(--ink-3)] font-medium">
                Thư mục này chưa có tài liệu nào.
              </p>
            </div>
          )}
        </section>
      )}

      {fileDetail && (
        <DocumentViewerModal
          isOpen={isViewerOpen}
          onClose={() => setIsViewerOpen(false)}
          file={fileDetail}
          onDownload={handleDownload}
          onToggleLike={handleToggleLike}
        />
      )}
    </div>
  );
};
