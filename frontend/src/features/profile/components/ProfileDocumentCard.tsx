// src/features/profile/components/ProfileDocumentCard.tsx
import React from "react";
import type { ProfileDocument } from "../types";
import { DocumentIcon } from "@/shared/components/icons/DocumentIcon"; // <-- Đổi đường dẫn này theo đúng project của bạn

export const ProfileDocumentCard: React.FC<{ doc: ProfileDocument }> = ({ doc }) => {
  // Hàm phụ để lấy màu nền nửa trên của thẻ (Tương đồng với màu của icon)
  const getTopBgColor = (ext: string) => {
    switch (ext.toLowerCase()) {
      case "docx":
      case "doc":
        return "bg-blue-100";
      case "pptx":
      case "ppt":
        return "bg-red-100";
      case "xls":
      case "xlsx":
        return "bg-green-100";
      case "pdf":
        return "bg-red-100";
      default:
        return "bg-gray-100";
    }
  };

  const topBgColor = getTopBgColor(doc.extension);

  return (
    <div className="bg-[var(--bg-surface)] rounded-xl border border-[var(--rule)] overflow-hidden flex flex-col hover:shadow-md transition-shadow cursor-pointer group">
      
      {/* Nửa trên: Background màu & Component DocumentIcon của bạn */}
      <div className={`h-28 ${topBgColor} flex items-center justify-center p-4 transition-colors`}>
        {/* Dùng !bg-white để ép nền khối icon thành màu trắng giống bản thiết kế, đồng thời tăng padding */}
        <DocumentIcon 
          extension={doc.extension} 
          size={32} 
          className="!bg-white !p-3 shadow-sm !rounded-lg" 
        />
      </div>
      
      {/* Nửa dưới: Thông tin */}
      <div className="p-4 flex flex-col gap-2">
        <h4 className="text-sm font-bold text-[var(--ink-1)] truncate" title={doc.name}>
          {doc.name}
        </h4>
        
        <div className="flex justify-between items-center text-xs">
          <span className="text-[var(--ink-3)] font-medium">{doc.size}</span>
          {doc.postedByMe && (
            <span className="text-[var(--ink-3)] font-medium">Bạn đăng</span>
          )}
        </div>
        
        <hr className="my-1 border-[var(--rule)]" />
        
        <p className="text-xs text-[var(--ink-3)] truncate font-medium">
          Nhóm: {doc.groupName}
        </p>
      </div>
    </div>
  );
};