// src/features/classDocuments/components/UploadDocumentModal.tsx
import React, { useState, useRef } from "react";
import { X, CloudUpload, Trash2, File as FileIcon } from "lucide-react";
import type { FolderItem } from "@features/document/types";

interface UploadDocumentModalProps {
  isOpen: boolean;
  onClose: () => void;
  folders: FolderItem[];
}

export const UploadDocumentModal: React.FC<UploadDocumentModalProps> = ({
  isOpen,
  onClose,
  folders = [],
}) => {
  const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
  const [selectedFolder, setSelectedFolder] = useState<string>("");
  const fileInputRef = useRef<HTMLInputElement>(null);

  if (!isOpen) return null;

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      const filesArray = Array.from(e.target.files);
      setSelectedFiles((prev) => [...prev, ...filesArray]);
    }
  };

  const removeFile = (indexToRemove: number) => {
    setSelectedFiles((prev) =>
      prev.filter((_, index) => index !== indexToRemove),
    );
  };

  const totalSizeMB =
    selectedFiles.reduce((acc, file) => acc + file.size, 0) / (1024 * 1024);

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center bg-black/50 backdrop-blur-sm animate-in fade-in duration-200">
      {/*Dạng Bottom Sheet (bo góc trên) cho Mobile, trượt từ dưới lên */}
      <div className="bg-white w-full sm:max-w-[600px] rounded-t-3xl sm:rounded-2xl shadow-xl flex flex-col max-h-[95dvh] sm:max-h-[90vh] animate-in slide-in-from-bottom-full sm:slide-in-from-bottom-0 sm:zoom-in-95 duration-300">
        <div className="w-full flex justify-center pt-3 pb-1 sm:hidden">
          <div className="w-12 h-1.5 bg-gray-300 rounded-full"></div>
        </div>

        {/* Header */}
        <div className="flex items-center justify-between px-5 pb-4 pt-2 sm:pt-5 sm:pb-5 border-b border-[var(--rule)]">
          <h2 className="text-lg sm:text-xl font-bold text-[var(--ink-1)]">
            Tải lên tài liệu
          </h2>
          <button
            onClick={onClose}
            className="text-[var(--ink-3)] hover:text-[var(--ink-1)] transition-colors p-1 rounded-full hover:bg-gray-100"
          >
            <X size={24} />
          </button>
        </div>

        {/* Body có thể cuộn */}
        <div className="p-4 sm:p-6 overflow-y-auto flex flex-col gap-5 sm:gap-6 custom-scrollbar">
          {/* Vùng tải tệp: Thu gọn padding trên Mobile */}
          <div
            className="border-2 border-dashed border-[#00B4D8] bg-[#E5F9FD] rounded-xl sm:rounded-2xl p-6 sm:p-8 flex flex-col items-center justify-center text-center cursor-pointer hover:bg-[#D4F4FA] transition-colors active:scale-[0.98]"
            onClick={() => fileInputRef.current?.click()}
          >
            <div className="bg-white p-2.5 sm:p-3 rounded-full shadow-sm mb-2 sm:mb-3 text-[#00B4D8]">
              <CloudUpload size={28} className="sm:w-8 sm:h-8" />
            </div>
            <p className="text-sm sm:text-base text-[var(--ink-1)] font-medium">
              Chạm để chọn tệp{" "}
              <span className="hidden sm:inline">hoặc kéo thả vào đây</span>
            </p>
            <p className="text-[11px] sm:text-xs text-[var(--ink-3)] mt-1.5">
              Tối đa 100MB/tệp, tổng 500MB.
            </p>
            <input
              type="file"
              multiple
              className="hidden"
              ref={fileInputRef}
              onChange={handleFileChange}
              accept=".doc,.docx,.pdf,.pptx,.png,.jpg,.jpeg,.webp"
            />
          </div>

          {/* Danh sách tệp đã chọn */}
          {selectedFiles.length > 0 && (
            <div className="flex flex-col gap-2">
              {selectedFiles.map((file, index) => (
                <div
                  key={index}
                  className="flex items-center justify-between p-2.5 sm:p-3 border border-[var(--rule)] rounded-xl bg-white shadow-sm"
                >
                  <div className="flex items-center gap-3 overflow-hidden">
                    <div className="bg-gray-100 p-2 rounded-lg text-gray-500 shrink-0">
                      <FileIcon size={18} />
                    </div>
                    <div className="flex flex-col overflow-hidden">
                      <span className="text-sm font-semibold text-[var(--ink-1)] truncate">
                        {file.name}
                      </span>
                      <span className="text-[11px] text-[var(--ink-3)]">
                        {(file.size / (1024 * 1024)).toFixed(2)} MB
                      </span>
                    </div>
                  </div>

                  <button
                    onClick={() => removeFile(index)}
                    className="text-gray-400 hover:text-red-500 transition-colors p-2.5 shrink-0"
                  >
                    <Trash2 size={18} />
                  </button>
                </div>
              ))}
            </div>
          )}

          {/* Form nhập liệu */}
          <div className="flex flex-col gap-4">
            <div>
              <label className="block text-[13px] sm:text-sm font-semibold text-[var(--ink-2)] mb-1.5">
                Chọn thư mục
              </label>
              {/* Tăng padding (py-3) trên Mobile để dễ bấm */}
              <select
                value={selectedFolder}
                onChange={(e) => setSelectedFolder(e.target.value)}
                className="w-full px-3 py-3 sm:py-2.5 text-sm border border-[var(--rule)] rounded-lg focus:outline-none focus:border-[#00B4D8] bg-white text-[var(--ink-1)] active:border-[#00B4D8]"
              >
                <option value="">Thư mục mới</option>
                {folders?.map((folder) => (
                  <option key={folder.id} value={folder.id}>
                    {folder.name}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-[13px] sm:text-sm font-semibold text-[var(--ink-2)] mb-1.5">
                Từ khóa (Tags)
              </label>
              <input
                type="text"
                placeholder="VD: NCKH, QNU, KTPM..."
                className="w-full px-3 py-3 sm:py-2.5 text-sm border border-[var(--rule)] rounded-lg focus:outline-none focus:border-[#00B4D8] text-[var(--ink-1)]"
              />
            </div>

            <div>
              <label className="block text-[13px] sm:text-sm font-semibold text-[var(--ink-2)] mb-1.5">
                Mô tả ngắn
              </label>
              <textarea
                rows={3}
                placeholder="Nhập mô tả cho tài liệu..."
                className="w-full px-3 py-3 sm:py-2.5 text-sm border border-[var(--rule)] rounded-lg focus:outline-none focus:border-[#00B4D8] text-[var(--ink-1)] resize-none"
              ></textarea>
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="flex flex-col sm:flex-row items-center justify-between p-4 sm:p-5 border-t border-[var(--rule)] gap-4 bg-gray-50/50 sm:bg-white rounded-b-2xl">
          <div className="text-[11px] sm:text-xs w-full text-left">
            <p className="text-[var(--ink-2)] flex justify-between sm:block">
              <span>Tổng dung lượng:</span>
              <span>
                <span className="font-bold text-[#00B4D8]">
                  {totalSizeMB.toFixed(2)} MB
                </span>{" "}
                / 500MB
              </span>
            </p>
            <p className="text-red-500 mt-1 sm:mt-0.5">
              *Hỗ trợ: doc, docx, pdf, pptx, png, jpg, webp
            </p>
          </div>

          <div className="flex gap-3 w-full sm:w-auto shrink-0 mt-1 sm:mt-0">
            <button
              onClick={onClose}
              className="flex-1 sm:flex-none px-6 py-3 sm:py-2.5 text-sm font-semibold text-[var(--ink-2)] border border-[var(--rule)] rounded-xl sm:rounded-lg hover:bg-gray-100 transition-colors bg-white active:scale-[0.98]"
            >
              Hủy
            </button>
            <button className="flex-1 sm:flex-none px-6 py-3 sm:py-2.5 text-sm font-semibold text-white bg-[#00B4D8] rounded-xl sm:rounded-lg hover:bg-[#0096C7] transition-colors active:scale-[0.98]">
              Tải lên
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
