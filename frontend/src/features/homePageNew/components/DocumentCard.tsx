// src/features/homePageNew/components/DocumentCard.tsx
import React from "react";
import { Download } from "lucide-react";
import { DocumentIcon } from "@/shared/components/icons/DocumentIcon";
import type { SearchDocument } from "@features/homePageNew/types";

export interface DocumentCardProps {
  document: SearchDocument;
  onClick?: (id: string | number) => void;
}

export const DocumentCard: React.FC<DocumentCardProps> = ({ document, onClick }) => {
  // Logic thông minh: Kiểm tra xem tên tài liệu đã chứa đuôi file chưa
  const ext = document.fileExtension?.toLowerCase() || "";
  const hasExtensionInTitle = ext && document.title.toLowerCase().endsWith(`.${ext}`);
  
  // Nếu chưa có thì nối thêm đuôi (ví dụ: .pdf), nếu có rồi thì giữ nguyên
  const displayTitle = hasExtensionInTitle 
    ? document.title 
    : `${document.title}${ext ? `.${ext}` : ""}`;

  return (
    <div
      onClick={() => onClick && onClick(document.id)}
      className="bg-[var(--bg-surface)] border border-[var(--rule)] rounded-[var(--r-xl)] p-5 flex gap-4 hover:shadow-[var(--shadow-sm)] hover:bg-[var(--bg-surface-2)] transition-all cursor-pointer"
    >
      {/* Tích hợp icon dùng chung */}
      <DocumentIcon extension={document.fileExtension} size={24} className="p-3" />

      <div className="flex-1 min-w-0">
        {/* Render tên tệp đã được đính kèm đuôi */}
        <h4 className="font-bold text-[15px] text-[var(--ink-1)] leading-snug mb-1 line-clamp-2">
          {displayTitle}
        </h4>
        <p className="text-[13px] text-[var(--ink-3)] mb-2 truncate">{document.category}</p>

        <div className="flex items-center gap-2 text-[11px] text-[var(--ink-3)] mb-2 flex-wrap">
          <span>Đăng bởi: {document.author}</span>
          <span className="w-0.5 h-0.5 rounded-full bg-[var(--ink-4)]"></span>
          <span>{document.date}</span>
        </div>

        <div className="flex items-center gap-1.5 text-[12px] text-[var(--ink-2)] font-medium">
          <Download size={14} />
          <span>{document.downloads}</span>
        </div>
      </div>
    </div>
  );
};