// src/features/document/components/DocumentViewerModal.tsx
import React, { useState, useEffect } from "react";
import {
  ArrowLeft,
  Heart,
  Share2,
  Download,
  Sparkles,
  Loader2,
  FileText,
} from "lucide-react";
import { useParams } from "react-router-dom";
import type { FileItem } from "@/features/document/types";
import { DocumentAIChatSidebar } from "@/features/document/components/DocumentAIChatSidebar";
import { classDocumentsAPI } from "@/features/document/api";

interface DocumentViewerModalProps {
  isOpen: boolean;
  onClose: () => void;
  documentId: string | number | null;
  initialFile?: Partial<FileItem> | null;
  onSelectDocument?: (documentId: number | string) => void;
}

const PREVIEWABLE_EXTS = ["pdf", "png", "jpg", "jpeg", "webp"];

export const DocumentViewerModal: React.FC<DocumentViewerModalProps> = ({
  isOpen,
  onClose,
  documentId,
  initialFile,
}) => {
  const { classId } = useParams();

  const [activeDocId, setActiveDocId] = useState<string | number | null>(
    documentId,
  );
  const [fileData, setFileData] = useState<FileItem | null>(null);
  const [isAIOpen, setIsAIOpen] = useState(false);
  const [blobUrl, setBlobUrl] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setActiveDocId(documentId);
  }, [documentId, isOpen]);

  // 1. Lấy metadata trước, chỉ gọi getFileBlob nếu định dạng hỗ trợ xem trực tiếp
  useEffect(() => {
    if (!isOpen || !activeDocId) return;

    let url: string | null = null;
    let isCancelled = false;

    setIsLoading(true);
    setError(null);
    setBlobUrl(null);

    const baseData: FileItem = {
      id: activeDocId,
      name:
        String(initialFile?.id) === String(activeDocId) && initialFile?.name
          ? initialFile.name
          : "Đang tải thông tin tài liệu...",
      size: initialFile?.size || "--",
      uploader: initialFile?.uploader || "Thành viên hệ thống",
      uploadDate: initialFile?.uploadDate || "",
      fileExtension: initialFile?.fileExtension || "",
      downloads: initialFile?.downloads || 0,
      likes: initialFile?.likes || 0,
      isLiked: initialFile?.isLiked || false,
    };
    setFileData(baseData);

    const loadDocument = async () => {
      try {
        // Lấy thông tin chi tiết file trước để biết chính xác đuôi mở rộng (fileExtension)
        const detail = await classDocumentsAPI
          .getFileDetail(activeDocId, baseData)
          .catch(() => baseData);

        if (isCancelled) return;
        setFileData(detail);

        const resolvedExt =
          detail.fileExtension?.toLowerCase().replace(".", "") || "";

        // Nếu không phải PDF hoặc Ảnh -> Dừng luôn, không gọi getFileBlob để tránh tự tải/tốn băng thông
        if (!PREVIEWABLE_EXTS.includes(resolvedExt)) {
          setIsLoading(false);
          return;
        }

        // Chỉ tải stream Blob khi là PDF hoặc Ảnh
        const blob = await classDocumentsAPI.getFileBlob(activeDocId);
        if (isCancelled) return;

        if (
          blob.type.includes("application/json") ||
          blob.type.includes("json")
        ) {
          const errorText = await blob.text();
          console.error("Lỗi từ server:", errorText);
          throw new Error(
            "Không thể tải tài liệu. Tài liệu có thể đã bị xóa hoặc bạn không có quyền truy cập.",
          );
        }

        if (blob.size === 0) {
          throw new Error("Tài liệu trống (0 bytes).");
        }

        url = URL.createObjectURL(blob);
        setBlobUrl(url);
        setError(null);
        setIsLoading(false);
      } catch (err: any) {
        if (isCancelled) return;
        console.error("Lỗi khi nạp tài liệu:", err);
        setError(err.message || "Không thể đọc nội dung tài liệu này.");
        setBlobUrl(null);
        setIsLoading(false);
      }
    };

    loadDocument();

    return () => {
      isCancelled = true;
      if (url) {
        URL.revokeObjectURL(url);
      }
    };
  }, [isOpen, activeDocId]);

  // 2. Xử lý Tải xuống chỉ khi người dùng chủ động bấm nút
  const handleDownload = async () => {
    if (!activeDocId) return;
    try {
      const blob = await classDocumentsAPI.downloadFile(activeDocId);
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = fileData?.name || `document-${activeDocId}`;
      document.body.appendChild(a);
      a.click();
      a.remove();
      URL.revokeObjectURL(url);
    } catch (err) {
      console.error("Lỗi tải xuống tài liệu:", err);
      alert("Không thể tải tài liệu về máy.");
    }
  };

  // 3. Xử lý Yêu thích (Bookmark)
  const handleToggleLike = async () => {
    if (!activeDocId || !fileData) return;
    const prevLiked = fileData.isLiked;
    setFileData((prev) => (prev ? { ...prev, isLiked: !prevLiked } : prev));

    try {
      await classDocumentsAPI.toggleBookmark(activeDocId);
    } catch (err) {
      setFileData((prev) => (prev ? { ...prev, isLiked: prevLiked } : prev));
      console.error("Lỗi yêu thích tài liệu:", err);
    }
  };

  if (!isOpen || !activeDocId) return null;

  const displayTitle =
    (fileData as any)?.title || fileData?.name || "Đang tải tài liệu...";
  const ext = fileData?.fileExtension?.toLowerCase().replace(".", "") || "";

  return (
    <div className="fixed inset-0 z-[100] bg-[var(--bg-surface)] flex flex-col animate-in fade-in zoom-in-95 duration-200">
      {/* HEADER */}
      <header className="h-[60px] shrink-0 bg-[var(--bg-surface)] border-b border-[var(--rule)] flex items-center justify-between px-4 sm:px-6 relative z-[103]">
        <div className="flex items-center gap-4 sm:gap-6 min-w-0">
          <button
            onClick={onClose}
            className="text-[var(--ink-3)] hover:text-[var(--ink-1)] transition-colors p-1.5 rounded-full hover:bg-[var(--bg-surface-2)]"
            title="Đóng"
          >
            <ArrowLeft size={24} />
          </button>

          {ext && (
            <div className="hidden sm:block px-2 py-0.5 bg-[var(--red-fill)] border border-[var(--red-border)] rounded text-[var(--red)] text-[10px] font-bold uppercase tracking-wider">
              {ext}
            </div>
          )}

          <div className="flex flex-col min-w-0">
            <h2
              className="text-[var(--ink-1)] text-sm sm:text-base font-semibold truncate max-w-[200px] sm:max-w-[400px] lg:max-w-[600px]"
              title={displayTitle}
            >
              {displayTitle}
            </h2>
            <p className="text-[var(--ink-3)] text-[11px] sm:text-xs truncate mt-0.5">
              Đăng bởi: {fileData?.uploader || "--"}
              <span className="mx-1 text-[var(--rule-lg)]">•</span>
              Ngày: {fileData?.uploadDate || "--"}
            </p>
          </div>
        </div>

        {/* Header Right */}
        <div className="flex items-center gap-2 sm:gap-4 lg:gap-6 shrink-0">
          <button
            onClick={handleToggleLike}
            className={`hidden sm:flex items-center gap-1.5 text-sm transition-colors ${
              fileData?.isLiked
                ? "text-[var(--red)]"
                : "text-[var(--ink-3)] hover:text-[var(--ink-1)]"
            }`}
          >
            <Heart
              size={18}
              className={fileData?.isLiked ? "fill-[var(--red)]" : ""}
            />{" "}
            Yêu thích
          </button>

          <button
            onClick={() => {
              navigator.clipboard?.writeText(window.location.href);
            }}
            className="hidden sm:flex items-center gap-1.5 text-sm text-[var(--ink-3)] hover:text-[var(--ink-1)] transition-colors"
            title="Sao chép liên kết"
          >
            <Share2 size={18} /> Chia sẻ
          </button>

          <div className="w-px h-6 bg-[var(--rule-lg)] hidden sm:block mx-1"></div>

          <button
            onClick={() => setIsAIOpen(!isAIOpen)}
            className={`flex items-center gap-1.5 px-3 sm:px-4 py-1.5 sm:py-2 rounded-[var(--r-md)] text-sm font-semibold transition-all active:scale-95 border ${
              isAIOpen
                ? "bg-[var(--violet-fill)] text-[var(--violet)] border-[var(--violet)] shadow-inner"
                : "bg-gradient-to-r from-[var(--violet)] to-[var(--primary)] hover:opacity-90 text-white border-[var(--violet-border)] shadow-[var(--shadow-sm)]"
            }`}
          >
            <Sparkles size={16} />
            <span className="hidden sm:inline">
              {isAIOpen ? "Đóng AI" : "Hỏi đáp AI"}
            </span>
            <span className="inline sm:hidden">AI</span>
          </button>

          <button
            onClick={handleDownload}
            className="flex items-center gap-1.5 px-3 sm:px-4 py-1.5 sm:py-2 bg-[var(--primary)] hover:bg-[var(--primary-hover)] text-white rounded-[var(--r-md)] text-sm font-semibold transition-colors shadow-[var(--shadow-sm)] active:scale-95"
          >
            <Download size={16} />
            <span className="hidden sm:inline">Tải xuống</span>
          </button>
        </div>
      </header>

      {/* BODY */}
      <div className="flex-1 flex overflow-hidden bg-[var(--bg-surface-2)]">
        <div className="flex-1 flex justify-center items-center overflow-hidden transition-all duration-300 ease-[cubic-bezier(0.2,0.8,0.2,1)] p-4 sm:p-6">
          <div className="w-full h-full max-w-5xl flex justify-center items-center relative">
            {isLoading ? (
              <div className="flex flex-col items-center gap-3 text-[var(--ink-3)]">
                <Loader2
                  className="animate-spin text-[var(--primary)]"
                  size={36}
                />
                <p className="text-sm font-medium">
                  Đang nạp dữ liệu tài liệu...
                </p>
              </div>
            ) : error ? (
              <div className="text-center text-[var(--ink-2)] p-8 bg-[var(--bg-surface)] border border-[var(--rule)] rounded-[var(--r-lg)] shadow-[var(--shadow-sm)]">
                <FileText
                  size={48}
                  className="mx-auto mb-4 text-[var(--red)] opacity-70"
                />
                <p className="text-sm font-medium text-[var(--red)]">{error}</p>
              </div>
            ) : blobUrl && ext === "pdf" ? (
              <iframe
                key={`pdf-${activeDocId}`}
                src={blobUrl}
                className="w-full h-full bg-white shadow-[var(--shadow-lg)] rounded-[var(--r-md)] border border-[var(--rule)]"
                title={displayTitle}
              />
            ) : blobUrl && ["png", "jpg", "jpeg", "webp"].includes(ext) ? (
              <div className="w-full h-full flex justify-center items-center dark:brightness-[0.85] dark:saturate-[0.9] transition-all duration-200">
                <img
                  key={`img-${activeDocId}`}
                  src={blobUrl}
                  alt={displayTitle}
                  className="max-h-full max-w-full object-contain drop-shadow-xl rounded-[var(--r-md)]"
                />
              </div>
            ) : (
              /* Giao diện yêu cầu tải về cho các định dạng không hỗ trợ xem trực tiếp */
              <div className="text-center p-8 bg-[var(--bg-surface)] border border-[var(--rule)] rounded-[var(--r-xl)] text-[var(--ink-2)] max-w-md shadow-[var(--shadow-sm)]">
                <FileText
                  size={48}
                  className="mx-auto mb-4 text-[var(--ink-3)]"
                />
                <h3 className="font-semibold text-[var(--ink-1)] mb-2 text-lg">
                  Định dạng {ext ? ext.toUpperCase() : "này"} không hỗ trợ xem
                  trực tiếp
                </h3>
                <p className="text-sm text-[var(--ink-3)] mb-8">
                  Vui lòng tải tài liệu về thiết bị để xem nội dung hoặc sử dụng
                  tính năng Hỏi đáp AI ở góc trên.
                </p>
                <button
                  onClick={handleDownload}
                  className="inline-flex items-center gap-2 px-5 py-2.5 bg-[var(--primary)] text-white rounded-[var(--r-md)] text-sm font-semibold hover:bg-[var(--primary-hover)] transition-colors shadow-[var(--shadow-sm)] active:scale-95"
                >
                  <Download size={18} /> Tải file về máy
                </button>
              </div>
            )}
          </div>
        </div>

        {/* AI Sidebar */}
        <div
          className={`h-full bg-[var(--bg-surface)] border-l border-[var(--rule)] transition-all duration-300 ease-[cubic-bezier(0.2,0.8,0.2,1)] shadow-[-8px_0_24px_rgba(0,0,0,0.05)] ${
            isAIOpen
              ? "w-[100%] lg:w-[480px] opacity-100"
              : "w-0 opacity-0 overflow-hidden"
          }`}
        >
          <div className="w-[100vw] lg:w-[480px] h-full flex flex-col relative">
            <DocumentAIChatSidebar
              isOpen={isAIOpen}
              onClose={() => setIsAIOpen(false)}
              file={fileData}
              classId={classId}
            />
          </div>
        </div>
      </div>
    </div>
  );
};
