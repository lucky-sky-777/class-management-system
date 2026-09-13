// src/features/classDocuments/components/FolderCard.tsx
import React from "react";
import { Folder } from "lucide-react";
import type { FolderItem } from "../types";

interface FolderCardProps {
  folder: FolderItem;
  onClick: (folder: FolderItem) => void;
}

export const FolderCard: React.FC<FolderCardProps> = ({ folder, onClick }) => {
  return (
    <div 
      onClick={() => onClick(folder)}
      className="bg-[var(--bg-surface)] border border-[var(--rule)] rounded-xl p-4 flex flex-col gap-3 hover:shadow-md cursor-pointer transition-all hover:-translate-y-0.5 w-full sm:w-[200px]"
    >
      <Folder className="text-[#00B4D8]" fill="currentColor" size={32} />
      <div>
        <h4 className="font-bold text-sm text-[var(--ink-1)] truncate">{folder.name}</h4>
        <p className="text-[11px] text-[var(--ink-3)] mt-0.5">{folder.itemCount} mục</p>
      </div>
    </div>
  );
};