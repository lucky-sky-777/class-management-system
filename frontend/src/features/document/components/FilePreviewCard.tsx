// src/features/classDocuments/components/FilePreviewCard.tsx
import React from "react";
import { DocumentIcon } from "@/shared/components/icons/DocumentIcon";
import type { FileItem } from "@/features/document/types";

export const FilePreviewCard: React.FC<{ file: FileItem }> = ({ file }) => {
  return (
    <div className="bg-white border border-[var(--rule)] rounded-xl p-3 flex flex-col gap-3 hover:shadow-md cursor-pointer transition-shadow">
      
      {/* 
        Sử dụng DocumentIcon dùng chung.
        - Truyền size={40} để icon bên trong to lên một chút.
        - Truyền className="w-full h-24 !rounded-lg" để kéo giãn khung nền thành hình chữ nhật giống UI thiết kế.
      */}
      <DocumentIcon 
        extension={file.fileExtension} 
        size={40} 
        className="w-full h-24 !rounded-lg" 
      />
      
      {/* Thông tin chi tiết tệp */}
      <div className="flex flex-col gap-1 px-1">
        <h4 className="font-bold text-sm text-[var(--ink-1)] truncate" title={file.name}>
          {file.name}
        </h4>
        <div className="flex justify-between items-center text-[11px] text-[var(--ink-3)] font-medium">
          <span>{file.size}</span>
          <span>{file.uploader}</span>
        </div>
      </div>
    </div>
  );
};