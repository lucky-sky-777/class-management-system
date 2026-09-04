import React from "react";
import {
  MoreVertical,
  ArrowRight,
  Edit2,
  Trash2,
  LogOut,
  Lock,
} from "lucide-react";
import { ClassPrivacy, ClassStatus } from "@shared/domain/enums";
import type { ClassItems } from "@features/home/types";

interface GroupCardProps {
  item: ClassItems;
  isAdmin: boolean;
  isOpenMenu: boolean;
  onToggleMenu: (e: React.MouseEvent) => void;
  onEdit: (e: React.MouseEvent) => void;
  onDelete: (e: React.MouseEvent) => void;
  onLeave: (e: React.MouseEvent) => void;
  onClick: () => void;
}

export const GroupCard: React.FC<GroupCardProps> = ({
  item,
  isAdmin,
  isOpenMenu,
  onToggleMenu,
  onEdit,
  onDelete,
  onLeave,
  onClick,
}) => {
  return (
    <div
      onClick={onClick}
      // Dùng h-full và min-h để thẻ tự co giãn nội dung nhưng vẫn đều nhau khi xếp dạng Grid
      className="group bg-[var(--bg-surface)] border border-[var(--rule)] rounded-[var(--r-xl)] overflow-hidden shadow-[var(--shadow-sm)] hover:shadow-[var(--shadow-lg)] transition-all duration-300 cursor-pointer flex flex-col h-full min-h-[240px]"
    >
      {/* Banner lớp học */}
      <div
        className={`relative h-[90px] sm:h-[100px] p-3 sm:p-4 transition-colors duration-300 flex-shrink-0 ${
          item.privacy === ClassPrivacy.PUBLIC
            ? "bg-gradient-to-br from-[var(--primary)] to-[var(--primary-hover)]"
            : "bg-gradient-to-br from-[var(--ink-1)] to-[var(--ink-3)]"
        }`}
      >
        <div className="flex justify-between items-start gap-2">
          {/* Vùng chứa Tên & Code: Thêm flex-1 và min-w-0 để text không đẩy menu ra ngoài */}
          <div className="flex flex-wrap items-center gap-2 flex-1 min-w-0 pr-2">
            <h3 className="font-bold text-base sm:text-lg leading-tight truncate group-hover:underline text-white max-w-full">
              {item.name}
            </h3>

            {/* shrink-0 giúp badge CODE không bị bóp méo khi màn hình quá hẹp */}
            <span className="text-[10px] sm:text-[12px] shrink-0 font-bold uppercase px-2 py-0.5 rounded-full bg-white/15 text-white/80 border border-white/10">
              {item.code}
            </span>
          </div>

          {/* KHU VỰC MENU 3 CHẤM (Giữ nguyên kích thước) */}
          <div className="relative shrink-0">
            <button
              onClick={onToggleMenu}
              className="text-white/70 hover:text-white hover:bg-white/20 transition-colors p-1 rounded-full"
            >
              <MoreVertical size={18} />
            </button>

            {/* DROPDOWN MENU */}
            {isOpenMenu && (
              <div className="absolute right-0 mt-2 w-40 bg-[var(--bg-surface)] border border-[var(--rule)] rounded-[var(--r-md)] shadow-[var(--shadow-xl)] py-1 z-50 animate-in fade-in zoom-in-95 duration-100">
                {isAdmin ? (
                  <>
                    <button
                      onClick={onEdit}
                      className="flex items-center gap-2 w-full px-4 py-2 text-sm font-medium text-[var(--ink-1)] hover:bg-[var(--bg-surface-2)] transition-colors text-left"
                    >
                      <Edit2 size={15} /> Chỉnh sửa
                    </button>
                    <button
                      onClick={onDelete}
                      className="flex items-center gap-2 w-full px-4 py-2 text-sm font-bold text-[var(--red-text)] hover:bg-[var(--red-fill)] transition-colors text-left"
                    >
                      <Trash2 size={15} /> Xóa lớp
                    </button>
                  </>
                ) : (
                  <button
                    onClick={onLeave}
                    className="flex items-center gap-2 w-full px-4 py-2 text-sm font-bold text-[var(--red-text)] hover:bg-[var(--red-fill)] transition-colors text-left"
                  >
                    <LogOut size={15} /> Rời lớp
                  </button>
                )}
              </div>
            )}
          </div>
        </div>
        
        {/* Tên GV */}
        <p className="text-white/80 text-[11px] sm:text-xs mt-1 truncate opacity-90 max-w-[80%]">
          {item.owner_display_name || "Giáo viên"}
        </p>

        {/* Avatar viết tắt chủ phòng: Điều chỉnh kích thước nhỏ hơn một chút trên mobile */}
        <div className="absolute -bottom-5 right-3 sm:-bottom-6 sm:right-4 w-10 h-10 sm:w-12 sm:h-12 rounded-full bg-[var(--bg-surface)] shadow-[var(--shadow-md)] flex items-center justify-center border-[3px] sm:border-4 border-[var(--bg-surface)] overflow-hidden">
          <div className="w-full h-full bg-[var(--primary-fill)] flex items-center justify-center text-[var(--primary-text)] font-bold text-xs sm:text-sm overflow-hidden">
            {item.owner_avatar_url ? (
              <img
                src={item.owner_avatar_url}
                alt={item.owner_display_name}
                className="w-full h-full object-cover rounded-full"
              />
            ) : item.owner_display_name ? (
              item.owner_display_name
                .trim()
                .split(" ")
                .pop()
                ?.charAt(0)
                ?.toUpperCase()
            ) : (
              "G"
            )}
          </div>
        </div>
      </div>

      {/* Nội dung bên dưới banner */}
      <div className="p-3 sm:p-4 pt-6 sm:pt-8 flex-1 flex flex-col justify-between bg-[var(--bg-surface)]">
        <div className="space-y-2.5 sm:space-y-3">
          <div className="flex items-center gap-2">
            <span
              className={`text-[10px] px-2.5 py-0.5 rounded-[var(--r-sm)] font-extrabold uppercase tracking-wider ${
                item.privacy === ClassPrivacy.PUBLIC
                  ? "bg-[var(--green-fill)] text-[var(--green-text)] border border-[var(--green-border)]"
                  : "bg-[var(--amber-fill)] text-[var(--amber-text)] border border-[var(--amber-border)]"
              }`}
            >
              {item.privacy === ClassPrivacy.PUBLIC ? "Công khai" : "Riêng tư"}
            </span>
          </div>

          <div className="flex flex-col gap-2">
            {item.status === ClassStatus.PENDING_REQUEST && (
              <div className="flex items-center gap-2 text-[11px] sm:text-xs text-[var(--amber-text)] font-medium">
                <Lock size={14} />
                <span>Chờ duyệt</span>
              </div>
            )}
          </div>
          
          {/* Đổi từ truncate (1 dòng) thành line-clamp-2 (tối đa 2 dòng) để mô tả không bị cụt trên mobile */}
          <div className="text-[11px] sm:text-xs text-[var(--ink-2)] font-medium line-clamp-2 pt-1">
            {item.description || "Nhóm chia sẻ tài liệu học tập"}
          </div>
        </div>

        {/* Nút vào lớp chân thẻ */}
        <div className="pt-3 mt-3 border-t border-[var(--rule)] flex justify-end">
          <span className="text-[11px] sm:text-xs font-bold text-[var(--primary)] group-hover:text-[var(--primary-hover)] flex items-center gap-1 group-hover:translate-x-1 transition-all">
            VÀO LỚP <ArrowRight size={14} />
          </span>
        </div>
      </div>
    </div>
  );
};