// src/features/document/components/DocumentPreview.tsx
import React, { useEffect, useState } from "react";
import { Maximize2, FileText, Heart, Loader2, Download } from "lucide-react";
import { useParams } from "react-router-dom";
import type { FileItem } from "@/features/document/types";
import { classDocumentsAPI } from "@/features/document/api";

interface DocumentPreviewProps {
  file: FileItem;
  directoryId?: string | number;
  onOpenFullScreen: () => void;
  onToggleLike?: (id: string | number) => void;
}

export const DocumentPreview: React.FC<DocumentPreviewProps> = ({
  file,
  directoryId = 0,
  onOpenFullScreen,
  onToggleLike,
}) => {
  const { classId } = useParams();
  const [blobUrl, setBlobUrl] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let activeUrl: string | null = null;
    let isCancelled = false;

    if (!classId) return;

    classDocumentsAPI
      .getFileBlob(classId, directoryId, file.id)
      .then((blob) => {
        if (isCancelled) return;
        activeUrl = URL.createObjectURL(blob);
        setBlobUrl(activeUrl);
        setError(null);
        setIsLoading(false);
      })
      .catch((err) => {
        if (isCancelled) return;
        console.error("Lỗi tải bản xem trước file:", err);
        setError("Không thể tải bản xem trước của tài liệu này.");
        setBlobUrl(null);
        setIsLoading(false);
      });

    return () => {
      isCancelled = true;
      if (activeUrl) {
        URL.revokeObjectURL(activeUrl);
      }
    };
  }, [classId, directoryId, file.id]);

  const ext = file.fileExtension?.toLowerCase().replace(".", "");

  return (
    <div className="flex-1 flex flex-col min-w-0">
      {/* Header */}
      <div className="flex items-start justify-between mb-4">
        <div>
          <h2 className="text-xl font-bold text-[var(--ink-1)]">{file.name}</h2>
          <p className="text-sm text-[var(--ink-3)] mt-1 flex items-center gap-2">
            <span>{file.uploader}</span>
            {file.uploadDate && (
              <>
                <span>•</span>
                <span>{file.uploadDate}</span>
              </>
            )}
          </p>
        </div>

        {/* Nhóm nút góc phải */}
        <div className="flex items-center gap-3">
          {onToggleLike && (
            <button
              onClick={() => onToggleLike(file.id)}
              className={`p-2 rounded-full transition-all active:scale-95 border ${
                file.isLiked
                  ? "bg-red-50 border-red-100 text-red-500 shadow-sm"
                  : "bg-white border-[var(--rule)] text-[var(--ink-3)] hover:text-red-500 hover:bg-red-50 hover:border-red-100 shadow-sm"
              }`}
              title={file.isLiked ? "Bỏ yêu thích" : "Thêm vào yêu thích"}
            >
              <Heart size={18} className={file.isLiked ? "fill-current" : ""} />
            </button>
          )}

          <span className="px-3 py-1.5 bg-red-100 text-red-600 text-xs font-bold rounded-full uppercase tracking-wider">
            {file.fileExtension}
          </span>
        </div>
      </div>

      {/* Vùng hiển thị xem trước */}
      <div className="flex-1 min-h-[460px] lg:min-h-[560px] bg-[var(--primary-fill)] border border-[var(--rule)] rounded-2xl flex flex-col items-center justify-center relative overflow-hidden group p-2">
        {isLoading ? (
          <div className="flex flex-col items-center gap-3">
            <Loader2 className="animate-spin text-[var(--primary)]" size={32} />
            <p className="text-xs text-[var(--ink-3)]">Đang tải tài liệu...</p>
          </div>
        ) : error ? (
          <div className="text-center p-6">
            <FileText size={40} className="text-gray-400 mx-auto mb-2" />
            <p className="text-sm text-red-500 mb-2">{error}</p>
          </div>
        ) : blobUrl && ext === "pdf" ? (
          <div className="w-full h-full flex flex-col">
            <div className="absolute top-4 right-4 z-10">
              <button
                onClick={onOpenFullScreen}
                className="flex items-center gap-1.5 px-3 py-1.5 bg-black/70 hover:bg-black text-white rounded-lg text-xs font-medium shadow backdrop-blur transition-all"
              >
                <Maximize2 size={14} /> Toàn màn hình
              </button>
            </div>
            <iframe
              src={blobUrl}
              className="w-full h-[520px] rounded-xl border-none shadow-inner"
              title={file.name}
            />
          </div>
        ) : blobUrl && ["png", "jpg", "jpeg", "webp"].includes(ext) ? (
          <div className="w-full h-full flex items-center justify-center relative">
            <img
              src={blobUrl}
              alt={file.name}
              className="max-h-[520px] max-w-full object-contain rounded-lg"
            />
            <button
              onClick={onOpenFullScreen}
              className="absolute top-4 right-4 flex items-center gap-1.5 px-3 py-1.5 bg-black/70 hover:bg-black text-white rounded-lg text-xs font-medium shadow backdrop-blur"
            >
              <Maximize2 size={14} /> Toàn màn hình
            </button>
          </div>
        ) : (
          <div className="text-center flex flex-col items-center p-6">
            <div className="w-16 h-20 bg-blue-500 rounded-lg flex items-center justify-center mb-4 shadow-sm relative overflow-hidden">
              <div className="absolute top-0 right-0 w-6 h-6 bg-blue-600 rounded-bl-lg"></div>
              <FileText size={32} color="white" />
            </div>
            <p className="text-[var(--ink-2)] text-sm font-medium mb-1">
              Định dạng {file.fileExtension.toUpperCase()} không hỗ trợ xem
              inline.
            </p>
            <p className="text-xs text-[var(--ink-3)] mb-4">
              Vui lòng tải về máy để mở bằng ứng dụng tương thích.
            </p>
            {blobUrl && (
              <a
                href={blobUrl}
                download={file.name}
                className="flex items-center gap-2 px-4 py-2 bg-[var(--primary)] text-white rounded-lg text-sm font-semibold hover:opacity-90 shadow-sm"
              >
                <Download size={16} /> Tải file về máy
              </a>
            )}
          </div>
        )}
      </div>
    </div>
  );
};
