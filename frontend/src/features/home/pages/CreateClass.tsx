// src/features/home/components/CreateClassModal.tsx
import React, { useState } from "react";
import { useHome } from "@features/home/hooks/useHome";
import { Modal } from "@shared/components/ui/Modal";

interface CreateClassModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const CreateClassModal: React.FC<CreateClassModalProps> = ({
  isOpen,
  onClose,
}) => {
  const [className, setClassName] = useState("");
  const [description, setDescription] = useState("");
  const [status, setStatus] = useState("PUBLIC");

  const { createClassMutation, isCreating } = useHome();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!className.trim()) return;

    try {
      await createClassMutation({
        className: className,
        description: description,
        status: status,
      });

      setClassName("");
      setDescription("");
      setStatus("PUBLIC");
      onClose();
    } catch (error) {
      console.error("Lỗi khi tạo lớp:", error);
    }
  };

  if (!isOpen) return null;

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Tạo lớp học mới">
      <div className="w-full max-w-[400px] mx-auto animate-in zoom-in duration-200">
        <form onSubmit={handleSubmit} className="flex flex-col gap-5">
          {/* Tên lớp */}
          <div className="flex flex-col gap-1.5">
            <label className="text-[11px] font-bold uppercase tracking-[0.1em] text-[var(--ink-3)] ml-1">
              Tên lớp học
            </label>
            <input
              type="text"
              // Thay thế màu focus ring sang màu primary hệ thống
              className="input-field py-3 px-4 text-base focus:ring-2 focus:ring-[var(--primary-border)] text-[var(--ink-1)] outline-none transition-all"
              placeholder="VD: Lập trình .NET nâng cao"
              value={className}
              onChange={(e) => setClassName(e.target.value)}
              required
            />
          </div>

          {/* Mô tả */}
          <div className="flex flex-col gap-1.5">
            <label className="text-[11px] font-bold uppercase tracking-[0.1em] text-[var(--ink-3)] ml-1">
              Mô tả ngắn
            </label>
            <textarea
              className="input-field min-h-[100px] py-3 px-4 text-base resize-none text-[var(--ink-1)] focus:ring-2 focus:ring-[var(--primary-border)] outline-none transition-all"
              placeholder="Lớp học này dành cho..."
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />
          </div>

          {/* Trạng thái */}
          <div className="flex flex-col gap-1.5">
            <label className="text-[11px] font-bold uppercase tracking-[0.1em] text-[var(--ink-3)] ml-1">
              Chế độ hiển thị
            </label>
            <div className="relative">
              <select
                className="input-field w-full py-3 px-4 text-base bg-[var(--bg-surface)] text-[var(--ink-1)] cursor-pointer appearance-none outline-none focus:ring-2 focus:ring-[var(--primary-border)] transition-all"
                value={status}
                onChange={(e) => setStatus(e.target.value)}
              >
                <option value="PUBLIC">🌐 Cộng đồng (Public)</option>
                <option value="PRIVATE">🔒 Nhóm kín (Private)</option>
              </select>
              {/* Thêm mũi tên nhỏ chỉ xuống tinh tế do dùng appearance-none */}
              <div className="pointer-events-none absolute inset-y-0 right-0 flex items-center px-4 text-[var(--ink-3)]">
                ▼
              </div>
            </div>
          </div>

          {/* Nút bấm */}
          <div className="flex items-center gap-3 mt-4">
            <button
              type="button"
              className="flex-1 py-3 font-bold text-[var(--ink-2)] hover:bg-[var(--bg-surface-2)] rounded-[var(--r-md)] transition-all border border-[var(--rule)]"
              onClick={onClose}
              disabled={isCreating}
            >
              Hủy
            </button>
            <button
              type="submit"
              // Chuyển toàn bộ shadow và màu nền cứng sang biến css primary/shadow chuẩn chỉ
              className="flex-[2] py-3 bg-[var(--primary)] text-white hover:bg-[var(--primary-hover)] rounded-[var(--r-md)] font-bold shadow-[var(--shadow-md)] active:scale-[0.98] transition-all disabled:opacity-50"
              disabled={isCreating}
            >
              {isCreating ? "ĐANG TẠO..." : "TẠO LỚP"}
            </button>
          </div>
        </form>
      </div>
    </Modal>
  );
};
