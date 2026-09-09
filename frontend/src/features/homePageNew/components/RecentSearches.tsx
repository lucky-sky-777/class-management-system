// src/features/homePageNew/components/RecentSearches.tsx
import React from "react";
import { Clock } from "lucide-react";

interface RecentSearchesProps {
  searches: string[];
  onTagClick: (term: string) => void;
}

export const RecentSearches: React.FC<RecentSearchesProps> = ({ searches, onTagClick }) => {
  if (!searches || searches.length === 0) return null;

  return (
    <div className="max-w-[900px] mx-auto">
      <h3 className="text-[11px] font-semibold text-[var(--ink-3)] uppercase tracking-wider mb-4">
        Tìm kiếm gần đây
      </h3>
      <div className="flex flex-wrap gap-3">
        {searches.map((term, idx) => (
          <button
            key={idx}
            onClick={() => onTagClick(term)}
            className="flex items-center gap-2 px-4 py-2 rounded-full border border-[var(--rule)] bg-[var(--bg-surface)] hover:bg-[var(--bg-surface-2)] text-sm text-[var(--ink-2)] transition-colors shadow-sm"
          >
            <Clock size={14} className="text-[var(--ink-3)]" />
            {term}
          </button>
        ))}
      </div>
    </div>
  );
};