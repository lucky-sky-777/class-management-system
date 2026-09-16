// src/features/classDocuments/pages/ClassDocumentsTab.tsx
import React from "react";
import { Loader2, FolderOpen } from "lucide-react";
import { useClassDocuments } from "@features/document/hooks/useClassDocuments";
import { DocumentToolbar } from "@features/document/components/DocumentToolbar";
import { FolderCard } from "@features/document/components/FolderCard";
import { FilePreviewCard } from "@features/document/components/FilePreviewCard";

export const DocumentPage = () => {
  const {
    folders,
    recentFiles,
    isLoading,
    currentFolder,
    folderFiles,
    isFolderLoading,
    handleOpenFolder,
    handleGoToRoot,
    handleSearch,
    handleCreateFolder,
    handleRenameFolder,
    handleDeleteFolder,
  } = useClassDocuments();

  if (isLoading) {
    return (
      <div className="py-20 flex justify-center items-center">
        <Loader2 className="animate-spin text-[var(--ink-3)]" size={32} />
      </div>
    );
  }

  return (
    <div className="py-4">
      {/* Truyền thêm state hiện tại vào Toolbar để đổi Breadcrumbs */}
      <DocumentToolbar
        currentFolder={currentFolder}
        folders={folders}
        onGoToRoot={handleGoToRoot}
        onSearch={handleSearch}
        onCreateFolder={handleCreateFolder}
      />

      {/* ĐIỀU KIỆN 1: Nếu KHÔNG ở trong thư mục nào -> Render trang Gốc */}
      {!currentFolder ? (
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
                  <FilePreviewCard key={file.id} file={file} />
                ))}
              </div>
            </section>
          )}
        </>
      ) : (
        /* ĐIỀU KIỆN 2: Nếu ĐANG Ở TRONG THƯ MỤC -> Render danh sách file của thư mục đó */
        <section className="animate-in fade-in slide-in-from-right-4 duration-300">
          <div className="flex items-center gap-2 mb-6 border-b border-[var(--rule)] pb-4">
            {/* <h4 className="text-lg font-bold text-[var(--ink-1)]">
               Nội dung thư mục: {currentFolder.name}
             </h4> */}
          </div>

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
                <FilePreviewCard key={file.id} file={file} />
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
    </div>
  );
};
