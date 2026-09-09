// src/features/homePageNew/components/HeroSearch.tsx
import React, { useState } from "react";
import { Search, ArrowRight } from "lucide-react";

interface HeroSearchProps {
  onSearch: (term: string) => void;
  currentQuery: string;
}

export const HeroSearch: React.FC<HeroSearchProps> = ({ onSearch, currentQuery }) => {
  // Lưu giá trị input hiện tại
  const [inputValue, setInputValue] = useState(currentQuery);
  // Lưu lại prop currentQuery trước đó để so sánh
  const [prevQuery, setPrevQuery] = useState(currentQuery);

  if (currentQuery !== prevQuery) {
    setInputValue(currentQuery);
    setPrevQuery(currentQuery);
  }

  const handleSearchSubmit = () => {
    onSearch(inputValue);
  };

  return (
    <div className="text-center space-y-4">
      <h1 className="text-[32px] sm:text-[40px] font-bold text-[var(--ink-1)] tracking-tight">
        Tìm kiếm tài liệu học tập
      </h1>
      <p className="text-[var(--ink-3)] text-sm sm:text-base">
        Khám phá chia sẻ kiến thức cùng cộng đồng sinh viên
      </p>

      <div className="max-w-[700px] mx-auto mt-8 relative">
        <div className="absolute inset-y-0 left-0 pl-5 flex items-center pointer-events-none">
          <Search className="h-5 w-5 text-[var(--ink-3)]" />
        </div>
        
        <input
          type="text"
          value={inputValue}
          className="block w-full pl-14 pr-14 py-4 rounded-full border border-[var(--rule)] shadow-[var(--shadow-sm)] bg-[var(--bg-surface)] text-[var(--ink-1)] placeholder:text-[var(--ink-4)] focus:outline-none focus:border-[var(--primary)] focus:ring-1 focus:ring-[var(--primary)] transition-all text-base"
          placeholder="Tìm kiếm"
          onKeyDown={(e) => {
            if (e.key === "Enter") handleSearchSubmit();
          }}
          onChange={(e) => {
            const val = e.target.value;
            setInputValue(val);
            if (val === "") onSearch("");
          }}
        />

        <div className="absolute inset-y-0 right-2 flex items-center">
          <button
            onClick={handleSearchSubmit}
            className="w-10 h-10 bg-[var(--primary)] text-white flex items-center justify-center rounded-full hover:opacity-90 transition-opacity focus:outline-none focus:ring-2 focus:ring-offset-1 focus:ring-[var(--primary)]"
            title="Tìm kiếm"
          >
            <ArrowRight size={20} />
          </button>
        </div>
      </div>
    </div>
  );
};