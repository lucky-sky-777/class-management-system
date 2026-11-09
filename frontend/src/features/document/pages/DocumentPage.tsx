// src/features/classDocuments/pages/DocumentPage.tsx
import React, { useState, useEffect } from "react";
import { Loader2, FolderOpen } from "lucide-react";
import { useParams, useSearchParams } from "react-router-dom";
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
import { DocumentAIChatSidebar } from "../components/DocumentAIChatSidebar";

export const DocumentPage = () => {
  const { classId } = useParams();
  const [searchParams, setSearchParams] = useSearchParams();
  const [isClassAIOpen, setIsClassAIOpen] = useState(false);
  const [viewMode, setViewMode] = useState<"grid" | "list">("grid");
  const fileIdFromUrl = searchParams.get("fileId");

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

  // Lưu file được chọn từ danh sách hoặc từ URL query
  const [selectedFile, setSelectedFile] = useState<FileItem | null>(null);
  const [isViewerOpen, setIsViewerOpen] = useState(false);

  // 1. TỰ ĐỘNG CHỌN FILE KHI URL CÓ PARAM ?fileId=...
  useEffect(() => {
    if (!fileIdFromUrl) return;

    const allFiles = [...(recentFiles || []), ...(folderFiles || [])];
    const foundFile = allFiles.find(
      (f) => String(f.id) === String(fileIdFromUrl),
    );

    if (foundFile) {
      setSelectedFile(foundFile);
    } else {
      setSelectedFile((prev) => {
        if (prev && String(prev.id) === String(fileIdFromUrl)) return prev;
        return {
          id: fileIdFromUrl,
          name: "Đang tải thông tin tài liệu...",
          size: "",
          uploader: "",
          fileExtension: "pdf",
        };
      });
    }
  }, [fileIdFromUrl, recentFiles, folderFiles]);

  // 2. KÍCH HOẠT HOOK ĐỂ GỌI METADATA, TAGS & TOGGLE LIKE TỪ BACKEND
  const { fileDetail, handleToggleLike, isTogglingLike } = useDocumentDetail(
    selectedFile ? selectedFile.id : null,
    selectedFile,
  );

  const activeFile = fileDetail
    ? { ...selectedFile, ...fileDetail }
    : selectedFile;

  // Click vào file từ danh sách -> đồng bộ fileId lên URL
  const handleOpenFile = (file: FileItem) => {
    setSelectedFile(file);
    setSearchParams((prev) => {
      const next = new URLSearchParams(prev);
      next.set("fileId", String(file.id));
      return next;
    });
  };

  // Trở về trang gốc -> xóa fileId khỏi URL
  const handleToolbarGoToRoot = () => {
    setSelectedFile(null);
    setSearchParams((prev) => {
      const next = new URLSearchParams(prev);
      next.delete("fileId");
      return next;
    });
    handleGoToRoot();
  };

  // Callback chọn tài liệu trực tiếp từ nguồn trích dẫn của Chat AI
  const handleSelectDocumentFromChat = (documentId: number | string) => {
    const allFiles = [...(recentFiles || []), ...(folderFiles || [])];
    const target = allFiles.find((f) => String(f.id) === String(documentId));

    if (target) {
      setSelectedFile(target);
    } else {
      setSelectedFile((prev) => ({
        id: documentId,
        name: "Đang tải tài liệu...",
        size: "",
        uploader: "",
        fileExtension: "pdf",
        directoryId: prev?.directoryId,
      }));
    }

    setSearchParams((prev) => {
      const next = new URLSearchParams(prev);
      next.set("fileId", String(documentId));
      return next;
    });
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

  const layoutClass =
    viewMode === "grid"
      ? "grid grid-cols-[repeat(auto-fill,minmax(220px,1fr))] gap-4"
      : "grid grid-cols-1 gap-3";

  return (
    <div
      className={`transition-all duration-300 ease-[cubic-bezier(0.2,0.8,0.2,1)] ${isClassAIOpen ? "lg:mr-[480px]" : ""}`}
    >
      <div className="py-4 px-4 sm:px-6">
        <DocumentToolbar
          currentFolder={currentFolder}
          folders={folders}
          onGoToRoot={handleToolbarGoToRoot}
          onSearch={handleSearch}
          onCreateFolder={handleCreateFolder}
          onUploadDocument={handleUploadDocument}
          onOpenAIChat={() => setIsClassAIOpen(true)}
          isViewingFile={!!activeFile}
          viewMode={viewMode}
          onViewModeChange={setViewMode}
        />

        {/* ĐIỀU KIỆN 1: Nếu ĐANG XEM FILE -> Hiển thị Preview thật */}
        {activeFile ? (
          <div className="flex flex-col lg:flex-row gap-6 lg:gap-8">
            {(() => {
              const resolvedDirId =
                activeFile.directoryId ?? currentFolder?.id ?? 0;

              return (
                <DocumentPreview
                  key={`preview-${activeFile.id}-${resolvedDirId}`}
                  file={activeFile}
                  directoryId={resolvedDirId}
                  onOpenFullScreen={() => setIsViewerOpen(true)}
                  onToggleLike={() => handleToggleLike(activeFile.id)}
                  isLiking={isTogglingLike}
                />
              );
            })()}
            <DocumentSidebar
              file={activeFile}
              classId={classId}
              onDownload={handleDownloadFile}
              onToggleLike={() => handleToggleLike(activeFile.id)}
              onEdit={() => console.log("Edit")}
              onDelete={() => {
                if (window.confirm("Bạn có chắc chắn muốn xóa tài liệu này?")) {
                  handleDeleteFile(activeFile.id);
                  setSelectedFile(null);
                  setSearchParams((prev) => {
                    const next = new URLSearchParams(prev);
                    next.delete("fileId");
                    return next;
                  });
                }
              }}
            />
          </div>
        ) : /* ĐIỀU KIỆN 2: Nếu KHÔNG ở trong thư mục nào -> Render trang Gốc */
        !currentFolder ? (
          <>
            {folders.length > 0 ? (
              <section className="mb-10">
                <h3 className="text-sm font-bold text-[var(--ink-3)] uppercase tracking-wider mb-4">
                  Thư mục
                </h3>
                <div className={layoutClass}>
                  {folders.map((folder) => (
                    <FolderCard
                      key={folder.id}
                      folder={folder}
                      onClick={handleOpenFolder}
                      onEdit={handleRenameFolder}
                      onDelete={handleDeleteFolder}
                      viewMode={viewMode}
                    />
                  ))}
                </div>
              </section>
            ) : (
              <div className="text-center py-20 flex flex-col items-center gap-3 animate-in fade-in duration-300">
                <FolderOpen size={48} className="text-[var(--ink-4)]" />
                <p className="text-[var(--ink-3)] font-medium">
                  Chưa có thư mục nào được tạo.
                </p>
              </div>
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
              <div className={layoutClass}>
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
            directoryId={activeFile.directoryId ?? currentFolder?.id ?? 0}
            onDownload={handleDownloadFile}
            onToggleLike={() => handleToggleLike(activeFile.id)}
            onSelectDocument={handleSelectDocumentFromChat}
          />
        )}
      </div>
      <DocumentAIChatSidebar
        isOpen={isClassAIOpen}
        onClose={() => setIsClassAIOpen(false)}
        file={null}
        classId={classId}
        onSelectDocument={(docId) => {
          setIsClassAIOpen(false);
          handleSelectDocumentFromChat(docId);
        }}
      />
    </div>
  );
};
