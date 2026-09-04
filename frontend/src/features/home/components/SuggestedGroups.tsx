// src/features/home/components/SuggestedGroups.tsx
import React, { useState } from "react";
import { Image as ImageIcon, Loader2, Users } from "lucide-react";
import { ClassPrivacy } from "@shared/domain/enums";
import type { ClassItems } from "@features/home/types";

interface SuggestedGroupsProps {
  suggestions: ClassItems[];
  isLoading: boolean;
  onJoin: (id: number, code: string) => Promise<void>;
}

export const SuggestedGroups: React.FC<SuggestedGroupsProps> = ({ 
  suggestions, 
  isLoading, 
  onJoin 
}) => {
  const [joiningId, setJoiningId] = useState<number | null>(null);

  const handleJoinClick = async (id: number, code: string) => {
    try {
      setJoiningId(id);
      await onJoin(id, code);
    } finally {
      setJoiningId(null);
    }
  };

  return (
    <section>
      <h2 className="text-xl font-bold text-[var(--ink-1)] mb-4">Gợi ý cho bạn</h2>
      
      <div className="flex flex-col gap-3">
        {isLoading && (
          <div className="py-8 flex justify-center items-center">
            <Loader2 className="animate-spin text-[var(--ink-3)]" size={24} />
          </div>
        )}

        {!isLoading && suggestions.map((item) => (
          <div 
            key={item.id} 
            // ĐIỂM SỬA 1: flex-col trên mobile, sm:flex-row trên desktop
            className="bg-[var(--bg-surface)] border border-[var(--rule)] p-4 rounded-[var(--r-xl)] flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 shadow-[var(--shadow-sm)] transition-all hover:shadow-[var(--shadow-md)]"
          >
            {/* THÊM w-full để khối bên trái chiếm hết chiều ngang trên mobile */}
            <div className="flex items-center gap-3 sm:gap-4 flex-1 min-w-0 w-full">
              <div className="w-12 h-12 sm:w-14 sm:h-14 bg-[var(--ink-1)] rounded-[var(--r-md)] flex items-center justify-center text-[var(--bg-surface)] shrink-0">
                <ImageIcon size={24} />
              </div>
              
              <div className="flex flex-col flex-1 min-w-0">
                <div className="flex items-center gap-2">
                  <h4 className="font-bold text-sm text-[var(--ink-1)] truncate">
                    {item.name}
                  </h4>
                  <span className={`shrink-0 whitespace-nowrap px-2 py-0.5 border text-[10px] font-bold rounded-[var(--r-xs)] ${
                    item.privacy === ClassPrivacy.PUBLIC
                      ? "bg-[var(--green-fill)] text-[var(--green-text)] border-[var(--green-border)]"
                      : "bg-[var(--amber-fill)] text-[var(--amber-text)] border-[var(--amber-border)]"
                  }`}>
                    {item.privacy === ClassPrivacy.PUBLIC ? "CÔNG KHAI" : "RIÊNG TƯ"}
                  </span>
                </div>
                
                <p className="text-xs text-[var(--ink-2)] mt-0.5 sm:mt-1 truncate">
                  {item.description}
                </p>

                {/* ĐIỂM SỬA 2: Thêm số lượng thành viên */}
                <div className="flex items-center gap-1.5 mt-1.5 text-[11px] text-[var(--ink-3)] font-medium">
                  <Users size={12} />
                  {/* Giả sử API trả về trường member_count, bạn nhớ cập nhật bên interface ClassItems nếu chưa có nhé */}
                  <span>{item.member_count || 0} thành viên</span>
                </div>
              </div>
            </div>
            
            {/* ĐIỂM SỬA 3: Thêm w-full trên mobile, sm:w-auto trên desktop */}
            <button 
              onClick={() => handleJoinClick(item.id, item.code)}
              disabled={joiningId === item.id}
              className="w-full sm:w-auto shrink-0 flex items-center justify-center min-w-[90px] text-[var(--sky)] border border-[var(--sky)] rounded-[var(--r-md)] px-4 py-2 sm:py-1.5 text-xs font-bold hover:bg-[var(--sky-fill)] transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {joiningId === item.id ? (
                <Loader2 size={16} className="animate-spin text-[var(--sky)]" />
              ) : (
                "THAM GIA"
              )}
            </button>
          </div>
        ))}
      </div>
    </section>
  );
};