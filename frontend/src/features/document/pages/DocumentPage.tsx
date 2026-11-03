// src/features/classDocuments/pages/DocumentPage.tsx
import React, { useState } from "react";
import { Loader2, FolderOpen } from "lucide-react";
import { useParams } from "react-router-dom";
import { useClassDocuments } from "@features/document/hooks/useClassDocuments";
import { DocumentToolbar } from "@features/document/components/DocumentToolbar";
import { FolderCard } from "@features/document/components/FolderCard";
import { FilePreviewCard } from "@features/document/components/FilePreviewCard";
import { DocumentPreview } from "@features/document/components/DocumentPreview";
import { DocumentSidebar } from "@features/document/components/DocumentSidebar";
import type { FileItem } from "@features/document/types";
import { DocumentViewerModal } from "@features/document/components/DocumentViewerModal";
import { classDocumentsAPI } from "@features/document/api";
import { useDocumentDetail } from "@features/document/hooks/useDocumentDetail";

export const DocumentPage = () => {
  const { classId } = useParams();
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
    handleUploadDocument,
  } = useClassDocuments(classId);

  // Lưu trực tiếp file được chọn từ danh sách thay vì gọi hook mock
  const [selectedFile, setSelectedFile] = useState<FileItem | null>(null);
  const [isViewerOpen, setIsViewerOpen] = useState(false);

  // 1. KÍCH HOẠT HOOK ĐỂ GỌI METADATA & TAGS TỪ BACKEND
  const { fileDetail } = useDocumentDetail(
    selectedFile ? selectedFile.id : null,
    selectedFile,
  );

  // Ưu tiên dùng dữ liệu đã fetch metadata & tags đầy đủ
  const activeFile = fileDetail || selectedFile;

  // Click vào file từ danh sách
  const handleOpenFile = (file: FileItem) => {
    setSelectedFile(file);
  };

  // Trở về trang gốc
  const handleToolbarGoToRoot = () => {
    setSelectedFile(null);
    handleGoToRoot();
  };

  const handleDownloadFile = async () => {
    if (!classId || !activeFile) return;
    try {
      const blob = await classDocumentsAPI.getFileBlob(
        classId,
        currentFolder?.id ?? 0,
        activeFile.id,
      );
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = activeFile.name;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
    } catch (err) {
      console.error("Lỗi khi tải file:", err);
      alert("Không thể tải tài liệu về máy.");
    }
  };

  const handleToggleLike = () => {
    if (!selectedFile) return;
    setSelectedFile((prev) =>
      prev ? { ...prev, isLiked: !prev.isLiked } : null,
    );
  };

  // Màn hình loading khi vào trang
  if (isPageLoading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[400px] space-y-4">
        <div className="animate-in fade-in duration-300 flex flex-col items-center space-y-4">
          <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-[var(--warm-400)]"></div>
          <p className="text-ink-2 text-sm font-medium">Đang tải...</p>
        </div>
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
        onUploadDocument={handleUploadDocument}
      />

      {/* ĐIỀU KIỆN 1: Nếu ĐANG XEM FILE -> Hiển thị Preview thật */}
      {activeFile ? (
        <div className="flex flex-col lg:flex-row gap-6 lg:gap-8">
          <DocumentPreview
            key={activeFile.id}
            file={activeFile}
            directoryId={currentFolder?.id ?? 0}
            onOpenFullScreen={() => setIsViewerOpen(true)}
            onToggleLike={handleToggleLike}
          />
          <DocumentSidebar
            file={activeFile}
            onDownload={handleDownloadFile}
            onToggleLike={handleToggleLike}
            onEdit={() => console.log("Edit")}
            onDelete={() => {
              if (window.confirm("Bạn có chắc chắn muốn xóa tài liệu này?")) {
                handleDeleteFile(activeFile.id);
                setSelectedFile(null);
              }
            }}
          />
        </div>
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
                    onClick={() => handleOpenFile(file)}
                    onEdit={handleRenameFile}
                    onDelete={handleDeleteFile}
                  />
                ))}
              </div>
            </section>
          )}
        </>
      ) : (
        /* ĐIỀU KIỆN 3: Nếu ĐANG Ở TRONG THƯ MỤC -> Render danh sách file thư mục */
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
                  onClick={() => handleOpenFile(file)}
                  onEdit={handleRenameFile}
                  onDelete={handleDeleteFile}
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

      {/* Modal xem toàn màn hình */}
      {activeFile && isViewerOpen && (
        <DocumentViewerModal
          key={`viewer-${activeFile.id}`}
          isOpen={isViewerOpen}
          onClose={() => setIsViewerOpen(false)}
          file={activeFile}
          directoryId={currentFolder?.id ?? 0}
          onDownload={handleDownloadFile}
          onToggleLike={handleToggleLike}
        />
      )}
    </div>
  );
};
