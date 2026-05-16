// src/features/member/components/MemberItem.tsx
import { ShieldPlus, ShieldMinus, CircleX, Check, X } from "lucide-react";
import { useParams } from "react-router-dom";
import { memberAPI } from "@features/member/api";
import type { Member, MemberRole } from "@features/member/types";

interface MemberItemProps {
  member: any;
  myRole: MemberRole | "OWNER";
  onUpdateRole?: (userId: number, currentRole: MemberRole) => void;
  onKick?: (userId: number) => void;
  isPending?: boolean;
  onRefresh?: (silent?: boolean) => void;
}

export const MemberItem = ({
  member,
  myRole,
  onUpdateRole,
  onKick,
  isPending,
  onRefresh,
}: MemberItemProps) => {
  const { classId } = useParams();

  const name = member.user_display_name || member.displayName || "Thành viên";
  // 👉 FALLBACK AVATAR VỚI MÀU WARM GLOBAL
  const avatarUrl = `https://ui-avatars.com/api/?name=${encodeURIComponent(name)}&background=e2e8f0&color=475569&bold=true`;

  const handleApprove = async () => {
    if (!member.requestId) return alert("Thiếu Request ID!");
    try {
      await memberAPI.approveRequest(classId!, member.requestId);
      onRefresh?.(true);
    } catch (error) {
      alert("Không thể duyệt thành viên này.");
    }
  };

  const handleReject = async () => {
    if (!member.requestId) return alert("Thiếu Request ID!");
    if (window.confirm(`Từ chối yêu cầu của ${name}?`)) {
      try {
        await memberAPI.rejectRequest(classId!, member.requestId);
        onRefresh?.(true);
      } catch (error) {
        alert("Có lỗi xảy ra khi từ chối.");
      }
    }
  };

  return (
    <div className="group flex items-center justify-between py-3.5 px-3 hover:bg-[var(--bg-surface-2)] rounded-2xl transition-all duration-300 border-b border-[var(--rule)] last:border-0 mb-1">
      <div className="flex items-center gap-4 min-w-0">
        {/* AVATAR DÙNG BORDER & SHADOW GLOBAL */}
        <div className="relative shrink-0">
          <div className="w-10 h-10 md:w-11 md:h-11 rounded-full overflow-hidden border-2 border-[var(--bg-paper)] shadow-[var(--shadow-sm)] ring-1 ring-[var(--rule-md)]">
            <img
              src={member.avatar_url || member.user_avatar || avatarUrl}
              alt={name}
              className="w-full h-full object-cover"
            />
          </div>
          {/* Chấm trạng thái dùng màu GREEN của hệ thống */}
          <div className="absolute bottom-0 right-0 w-3 h-3 bg-[var(--green-text)] border-2 border-[var(--bg-paper)] rounded-full"></div>
        </div>

        <div className="min-w-0">
          <p className="text-sm font-black text-[var(--ink-1)] leading-tight truncate tracking-tight">
            {name}
          </p>
          <div className="flex items-center gap-2 mt-1 overflow-hidden text-[9px]">
            <span className="text-[var(--ink-3)] font-medium truncate tracking-tighter">
              {isPending ? "Yêu cầu vào:" : "Tham gia:"}{" "}
              {member.joined_at || member.joinedAt}
            </span>
          </div>
        </div>
      </div>

      <div className="flex items-center gap-2 shrink-0">
        {/* CASE 1: ĐANG CHỜ DUYỆT - DÙNG SOFT COLOR GLOBAL */}
        {isPending ? (
          <div className="flex items-center gap-1.5 md:gap-2">
            <button
              onClick={handleApprove}
              className="flex items-center gap-1.5 px-3 py-2 bg-[var(--green-fill)] text-[var(--green-text)] rounded-xl hover:bg-[var(--green-text)] hover:text-white transition-all duration-300 shadow-[var(--shadow-sm)] active:scale-95"
            >
              <Check size={16} strokeWidth={4} />
              <span className="text-[10px] font-black hidden sm:block">
                Duyệt
              </span>
            </button>

            <button
              onClick={handleReject}
              className="flex items-center gap-1.5 p-2 bg-[var(--red-fill)] text-[var(--red-text)] rounded-xl hover:bg-[var(--red-text)] hover:text-white transition-all duration-300 shadow-[var(--shadow-sm)] active:scale-95"
            >
              <X size={16} strokeWidth={4} />
              <span className="text-[10px] font-black hidden sm:block">
                Từ chối
              </span>
            </button>
          </div>
        ) : (
          /* CASE 2: ĐÃ LÀ THÀNH VIÊN - HIỆN KHI HOVER */
          <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-all duration-300 translate-x-2 group-hover:translate-x-0">
            {myRole === "OWNER" && member.role !== "OWNER" && (
              <>
                <button
                  onClick={() => onUpdateRole?.(member.userId, member.role)}
                  className="p-2 text-[var(--ink-3)] hover:text-[var(--warm-600)] hover:bg-[var(--warm-fill)] rounded-xl transition-all"
                >
                  {member.role === "CLASS_ADMIN" ? (
                    <ShieldMinus size={18} />
                  ) : (
                    <ShieldPlus size={18} />
                  )}
                </button>
                <button
                  onClick={() => onKick?.(member.userId)}
                  className="p-2 text-[var(--ink-3)] hover:text-[var(--red-text)] hover:bg-[var(--red-fill)] rounded-xl transition-all"
                >
                  <CircleX size={18} />
                </button>
              </>
            )}

            {/* Quyền của ADMIN đối với Member thường */}
            {myRole === "CLASS_ADMIN" && member.role === "CLASS_MEMBER" && (
              <button
                onClick={() => onKick?.(member.userId)}
                className="p-2 text-[var(--ink-3)] hover:text-[var(--red-text)] hover:bg-[var(--red-fill)] rounded-xl transition-all"
              >
                <CircleX size={18} />
              </button>
            )}
          </div>
        )}
      </div>
    </div>
  );
};
