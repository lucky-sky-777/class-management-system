// src/features/profile/pages/ProfilePage.tsx
import { useState } from "react";
import { Loader2 } from "lucide-react";
import { useProfile } from "@/features/profile/hooks/useProfile";
import { useDocumentDetail } from "@/features/document/hooks/useDocumentDetail";
import { ProfileHeader } from "@/features/profile/components/ProfileHeader";
import { ProfileInfo } from "@/features/profile/components/ProfileInfo";
import { FilePreviewCard } from "@/features/document/components/FilePreviewCard";
import { DocumentViewerModal } from "@/features/document/components/DocumentViewerModal";
import type { FileItem } from "@/features/document/types";

export const ProfilePage = () => {
  const { profile, documents, isLoading: isProfileLoading } = useProfile();

  const [selectedFileId, setSelectedFileId] = useState<string | number | null>(
    null,
  );

  const {
    fileDetail,
    isLoading: isFileDetailLoading,
    handleToggleLike,
    handleDownload,
  } = useDocumentDetail(selectedFileId);

  if (isProfileLoading || !profile) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[400px] space-y-4">
        <div className="animate-in fade-in duration-300 flex flex-col items-center space-y-4">
          <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-[var(--warm-400)]"></div>
          <p className="text-ink-2 text-sm font-medium">Đang tải hồ sơ...</p>
        </div>
      </div>
    );
  }

  const handleOpenFile = (fileId: string | number) => {
    setSelectedFileId(fileId);
  };

  const handleCloseViewer = () => {
    setSelectedFileId(null);
  };

  return (
    <div className="max-w-6xl mx-auto p-4 md:p-6 relative">
      <ProfileHeader profile={profile} />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-1">
          <ProfileInfo profile={profile} />
        </div>

        <div className="lg:col-span-2">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-lg font-bold text-[var(--ink-1)]">
              Tài liệu đã đăng
            </h2>
            <div className="flex items-center gap-2 text-sm text-[var(--ink-2)]">
              <span>Sắp xếp:</span>
              <select className="bg-[var(--bg-surface)] border border-[var(--rule)] rounded-md px-2 py-1 outline-none text-[var(--ink-1)]">
                <option>Mới nhất</option>
                <option>Cũ nhất</option>
                <option>Nhiều lượt tải</option>
              </select>
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-4">
            {documents.map((doc) => {
              const mappedFile: FileItem = {
                id: doc.id,
                name: doc.name,
                size: doc.size,
                fileExtension: doc.extension,
                uploader: `Nhóm: ${doc.groupName}`,
              };

              return (
                <FilePreviewCard
                  key={mappedFile.id}
                  file={mappedFile}
                  onClick={() => handleOpenFile(mappedFile.id)}
                />
              );
            })}
          </div>
        </div>
      </div>

      {/* Hiển thị Loading mờ lên màn hình nếu Hook đang đi fetch chi tiết file */}
      {isFileDetailLoading && selectedFileId && (
        <div className="fixed inset-0 z-[110] bg-black/20 backdrop-blur-sm flex justify-center items-center">
          <Loader2 className="animate-spin text-white" size={48} />
        </div>
      )}

      {fileDetail && !isFileDetailLoading && (
        <DocumentViewerModal
          isOpen={selectedFileId !== null}
          onClose={handleCloseViewer}
          file={fileDetail}
          onDownload={handleDownload}
          onToggleLike={handleToggleLike}
        />
      )}
    </div>
  );
};
