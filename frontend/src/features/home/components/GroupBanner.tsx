import { FileText } from "lucide-react";

export const GroupBanner = () => {
  return (
    <div className="bg-[var(--primary)] rounded-[var(--r-xl)] p-8 mb-8 flex justify-between items-center relative overflow-hidden shadow-[var(--shadow-md)]">
      <div className="relative z-10 max-w-2xl text-white">
        <h1 className="text-3xl font-bold mb-3 font-serif">Không gian học tập riêng của bạn</h1>
        <p className="text-white/90 text-sm mb-6 max-w-lg leading-relaxed">
          Tạo nhóm kín để cùng làm đồ án, chia sẻ tài liệu nội bộ và quản lý tiến độ học tập một cách bảo mật và hiệu quả
        </p>
        {/* <button className="bg-[var(--bg-surface)] text-[var(--primary)] px-5 py-2.5 rounded-[var(--r-full)] font-bold text-sm flex items-center gap-2 hover:bg-[var(--bg-surface-2)] transition-colors shadow-[var(--shadow-sm)]">
          <Plus size={18} /> Tạo nhóm mới
        </button> */}
      </div>
      
      {/* Hình minh họa (Placeholder) */}
      <div className="absolute right-10 top-1/2 -translate-y-1/2 opacity-90 hidden md:block">
        <div className="w-40 h-40 bg-white/10 rounded-[var(--r-full)] flex items-center justify-center backdrop-blur-sm">
          <FileText size={64} className="text-white/50" />
        </div>
      </div>
    </div>
  );
};