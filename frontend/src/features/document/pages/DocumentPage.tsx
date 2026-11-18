// src/features/classDocuments/pages/DocumentPage.tsx
import { useState, useEffect } from "react";
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
  const fileIdFromUrl = searchParams.get("fileId");

  const [isClassAIOpen, setIsClassAIOpen] = useState(false);
  const [viewMode, setViewMode] = useState<"grid" | "list">("grid");
  const [selectedFile, setSelectedFile] = useState<FileItem | null>(null);
  const [isViewerOpen, setIsViewerOpen] = useState(false);

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
    handleToggleFileLike,
  } = useClassDocuments(classId);

  // 1. Tự động đồng bộ file theo param ?fileId=... trên URL
  useEffect(() => {
    if (!fileIdFromUrl) {
      setSelectedFile(null);
      return;
    }

    const allFiles = [...(recentFiles || []), ...(folderFiles || [])];
    const foundFile = allFiles.find(
      (f) => String(f.id) === String(fileIdFromUrl),
    );

    if (foundFile) {
      setSelectedFile((prev) =>
        prev && String(prev.id) === String(foundFile.id) ? prev : foundFile,
      );
      if (!currentFolder && foundFile.directoryId) {
        const parentFolder = folders.find(
          (folder) => String(folder.id) === String(foundFile.directoryId),
        );
        if (parentFolder) {
          handleOpenFolder(parentFolder);
        }
      }
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

  // 2. Lấy metadata, tags & trạng thái yêu thích từ Backend
  const { fileDetail, handleToggleLike, isTogglingLike } = useDocumentDetail(
    selectedFile ? selectedFile.id : null,
    selectedFile,
  );

  const activeFile = fileDetail
    ? { ...selectedFile, ...fileDetail }
    : selectedFile;

  // Đồng bộ thư mục cha lên Breadcrumb nếu mở trực tiếp link ?fileId=... ở tab mới
  useEffect(() => {
    const dirId = activeFile?.directoryId;
    if (!dirId || currentFolder || folders.length === 0) return;

    const parentFolder = folders.find((f) => String(f.id) === String(dirId));
    if (parentFolder) {
      handleOpenFolder(parentFolder);
    }
  }, [activeFile?.directoryId, currentFolder, folders, handleOpenFolder]);

  const clearFileFromUrl = () => {
    setSelectedFile(null);
    setSearchParams(
      (prev) => {
        const next = new URLSearchParams(prev);
        next.delete("fileId");
        return next;
      },
      { replace: true },
    );
  };

  const handleOpenFile = (file: FileItem) => {
    setSelectedFile(file);
    setSearchParams((prev) => {
      const next = new URLSearchParams(prev);
      next.set("fileId", String(file.id));
      return next;
    });
  };

  const handleToolbarGoToRoot = () => {
    clearFileFromUrl();
    handleGoToRoot();
  };

  const handleSelectDocumentFromChat = (documentId: number | string) => {
    const allFiles = [...(recentFiles || []), ...(folderFiles || [])];
    const target = allFiles.find((f) => String(f.id) === String(documentId));

    setSelectedFile(
      target || {
        id: documentId,
        name: "Đang tải tài liệu...",
        size: "",
        uploader: "",
        fileExtension: "pdf",
      },
    );

    setSearchParams((prev) => {
      const next = new URLSearchParams(prev);
      next.set("fileId", String(documentId));
      return next;
    });
  };

  const handleDownloadFile = async () => {
    if (!activeFile) return;
    try {
      const blob = await classDocumentsAPI.downloadFile(activeFile.id);
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

  // Đồng bộ nút tim ở màn hình chi tiết ngược lại danh sách folderFiles/recentFiles
  const handleToggleActiveFileLike = () => {
    if (!activeFile) return;
    handleToggleLike(activeFile.id);
    handleToggleFileLike(activeFile.id, !activeFile.isLiked);
  };

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

  const resolvedDirId = activeFile?.directoryId ?? currentFolder?.id ?? 0;

  return (
    <div
      className={`transition-all duration-300 ease-[cubic-bezier(0.2,0.8,0.2,1)] ${
        isClassAIOpen ? "lg:mr-[480px]" : ""
      }`}
    >
      <div className="py-4 px-4 sm:px-6">
        <DocumentToolbar
          currentFolder={currentFolder}
          currentFileName={
            activeFile ? (activeFile as any).title || activeFile.name : null
          }
          folders={folders}
          onGoToRoot={handleToolbarGoToRoot}
          onBackToFolder={clearFileFromUrl}
          onSearch={handleSearch}
          onCreateFolder={handleCreateFolder}
          onUploadDocument={handleUploadDocument}
          onOpenAIChat={() => setIsClassAIOpen(true)}
          isViewingFile={!!activeFile}
          viewMode={viewMode}
          onViewModeChange={setViewMode}
        />

        {/* 1. ĐANG XEM CHI TIẾT TÀI LIỆU */}
        {activeFile ? (
          <div className="flex flex-col lg:flex-row gap-6 lg:gap-8">
            <DocumentPreview
              key={`preview-${activeFile.id}-${resolvedDirId}`}
              file={activeFile}
              directoryId={resolvedDirId}
              onOpenFullScreen={() => setIsViewerOpen(true)}
              onToggleLike={handleToggleActiveFileLike}
              isLiking={isTogglingLike}
            />
            <DocumentSidebar
              file={activeFile}
              classId={classId}
              onDownload={handleDownloadFile}
              onToggleLike={handleToggleActiveFileLike}
              onEdit={() => console.log("Edit")}
              onDelete={() => {
                if (window.confirm("Bạn có chắc chắn muốn xóa tài liệu này?")) {
                  handleDeleteFile(activeFile.id);
                  clearFileFromUrl();
                }
              }}
            />
          </div>
        ) : !currentFolder ? (
          /* 2. TRANG GỐC: DANH SÁCH THƯ MỤC */
          folders.length > 0 ? (
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
          )
        ) : (
          /* 3. TRONG THƯ MỤC: DANH SÁCH TÀI LIỆU */
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
                    viewMode={viewMode}
                    onClick={() => handleOpenFile(file)}
                    onEdit={handleRenameFile}
                    onDelete={handleDeleteFile}
                    onToggleLike={handleToggleFileLike}
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

        {/* Modal xem toàn màn hình độc lập */}
        <DocumentViewerModal
          isOpen={isViewerOpen && !!activeFile}
          onClose={() => setIsViewerOpen(false)}
          documentId={activeFile?.id ?? null}
          initialFile={activeFile}
          onSelectDocument={handleSelectDocumentFromChat}
        />
      </div>

      <DocumentAIChatSidebar
        isOpen={isClassAIOpen}
        onClose={() => setIsClassAIOpen(false)}
        file={null}
        classId={classId}
      />
    </div>
  );
};
