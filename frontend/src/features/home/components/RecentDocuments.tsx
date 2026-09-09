import React from "react";
import { Users, Loader2 } from "lucide-react";
import { useRecentDocuments } from "@features/home/hooks/useRecentDocuments";
import { DocumentIcon } from "@/shared/components/icons/DocumentIcon";

export const RecentDocuments = () => {
  const { documents, isLoading } = useRecentDocuments();

  return (
    <div className="lg:col-span-1">
      <h2 className="text-xl font-bold text-[var(--ink-1)] mb-4">Tài liệu mới từ nhóm</h2>
      
      <div className="flex flex-col gap-3">
        {isLoading && (
          <div className="py-8 flex justify-center items-center">
            <Loader2 className="animate-spin text-[var(--ink-3)]" size={24} />
          </div>
        )}

        {!isLoading && documents.map((doc) => {
          return (
            <div
              key={doc.id}
              className="bg-[var(--bg-surface)] border border-[var(--rule)] p-4 rounded-[var(--r-xl)] shadow-[var(--shadow-sm)] flex items-start gap-3 cursor-pointer hover:bg-[var(--bg-surface-2)] transition-colors"
            >
              <DocumentIcon extension={doc.fileExtension} />
              
              <div>
                <h4 className="font-bold text-sm text-[var(--ink-1)] mb-1 leading-tight line-clamp-2">
                  {doc.title}
                </h4>
                <p className="text-[11px] text-[var(--ink-3)] mb-2">
                  Đăng bởi: {doc.authorName} • {doc.createdAt}
                </p>
                <span className="bg-[var(--bg-surface-2)] text-[var(--ink-2)] border border-[var(--rule)] text-[10px] px-2 py-1 rounded-[var(--r-xs)] font-medium inline-flex items-center gap-1">
                  <Users size={10} /> Nhóm: {doc.groupName}
                </span>
              </div>
            </div>
          );
        })}
        
        {!isLoading && documents.length === 0 && (
          <div className="text-center py-6 text-[var(--ink-3)] text-sm italic">
            Chưa có tài liệu mới nào.
          </div>
        )}
      </div>
    </div>
  );
};