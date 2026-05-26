// src/features/member/components/MemberItem.tsx
import { CircleX, Check, X, Shield } from "lucide-react";
import { useParams } from "react-router-dom";
import { memberAPI } from "@features/member/api";
import type { Member, MemberRole } from "@features/member/types";
import { useToastStore } from "@app/store";
import { ToastType } from "@shared/domain/enums";

interface MemberItemProps {
  member: Member & {
    user_display_name?: string;
    avatar_url?: string;
    user_avatar?: string;
    joined_at?: string;
  };
  myRole: MemberRole | "OWNER";
  onKick?: (userId: number) => void;
  onManagePermissions?: (member: Member) => void;
  isPending?: boolean;
  onRefresh?: (silent?: boolean) => void;
}

export const MemberItem = ({
  member,
  myRole,
  onKick,
  onManagePermissions,
  isPending,
  onRefresh,
}: MemberItemProps) => {
  const { classId } = useParams<{ classId: string }>();
  const showToast = useToastStore((state) => state.showToast);

  const name = member.displayName || member.user_display_name || "Thành viên";
  const avatarSrc = member.avatarUrl || member.avatar_url || member.user_avatar;
  const joinedDate = member.joinedAt || member.joined_at;

  const fallbackAvatarUrl = `https://ui-avatars.com/api/?name=${encodeURIComponent(name)}&background=e2e8f0&color=475569&bold=true`;

  const handleApprove = async () => {
    if (!member.requestId) return;
    try {
      await memberAPI.approveRequest(classId!, member.requestId);
      showToast("Đã duyệt thành viên!", ToastType.SUCCESS);
      onRefresh?.(true);
    } catch {
      showToast("Lỗi duyệt thành viên.", ToastType.ERROR);
    }
  };

  const handleReject = async () => {
    if (!member.requestId) return;
    if (window.confirm(`Từ chối yêu cầu của ${name}?`)) {
      try {
        await memberAPI.rejectRequest(classId!, member.requestId);
        showToast("Đã từ chối.", ToastType.INFO);
        onRefresh?.(true);
      } catch {
        showToast("Có lỗi xảy ra.", ToastType.ERROR);
      }
    }
  };

  // từ điển map từ PermissionCode sang Tiếng Việt
  const PERMISSION_LABELS: Record<string, string> = {
    MANAGE_ACTIVITY: "Duyệt sự kiện",
    MANAGE_POINT: "Quyền ghi điểm",
    MANAGE_FUND: "Quyền thủ quỹ",
    MANAGE_ABSENCE_REQUEST: "Duyệt phép",
    MANAGE_GROUP: "Quyền nhân sự",
  };

  return (
    <div className="group flex items-center justify-between py-3.5 px-3 hover:bg-[var(--bg-surface-2)] rounded-2xl transition-all duration-300 border-b border-[var(--rule)] last:border-0 mb-1">
      <div className="flex items-center gap-4 min-w-0">
        <div className="w-10 h-10 rounded-full overflow-hidden border-2 border-[var(--bg-paper)] shadow-sm ring-1 ring-[var(--rule-md)]">
          <img
            src={avatarSrc || fallbackAvatarUrl}
            alt={name}
            className="w-full h-full object-cover"
          />
        </div>

        <div className="min-w-0 flex-1 ml-4">
          {/* Tên thành viên - luôn hiện đầy đủ */}
          <p className="text-sm font-black text-[var(--ink-1)] leading-tight break-words">
            {name}
          </p>

          {/* Hàng chứa Ngày gia nhập + Các Tags (Vai trò + Quyền) */}
          <div className="flex flex-wrap items-center gap-1.5 mt-1.5">
            <span className="text-[9px] text-[var(--ink-3)] font-medium whitespace-nowrap">
              {isPending ? "Yêu cầu: " : "Tham gia: "} {joinedDate}
            </span>

            {/* Vai trò */}
            {member.role !== "CLASS_MEMBER" && !isPending && (
              <span className="px-1.5 py-0.5 bg-[var(--warm-fill)] text-[var(--warm-text)] rounded-full font-bold uppercase text-[8px] whitespace-nowrap">
                {member.role === "OWNER" ? "Chủ lớp" : "Quản trị viên"}
              </span>
            )}

            {/* Danh sách quyền */}
            {Array.isArray(member.permissions) &&
              member.permissions.map((perm) => (
                <span
                  key={perm}
                  className="px-1.5 py-0.5 bg-[var(--sky-fill)] text-[var(--sky-text)] border border-[var(--sky-border)] rounded-full font-bold uppercase text-[8px] whitespace-nowrap"
                  title={PERMISSION_LABELS[perm] || perm}
                >
                  {PERMISSION_LABELS[perm] || perm.replace("MANAGE_", "")}
                </span>
              ))}
          </div>
        </div>
      </div>

      <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-all">
        {isPending ? (
          /* Nút Duyệt/Từ chối giữ nguyên */
          <div className="flex gap-1">
            <button
              onClick={handleApprove}
              className="p-2 bg-[var(--green-fill)] text-[var(--green-text)] rounded-xl"
            >
              <Check size={16} />
            </button>
            <button
              onClick={handleReject}
              className="p-2 bg-[var(--red-fill)] text-[var(--red-text)] rounded-xl"
            >
              <X size={16} />
            </button>
          </div>
        ) : (
          /* Chỉ hiện nút Phân quyền và Kick nếu có quyền */
          (myRole === "OWNER" || myRole === "CLASS_ADMIN") &&
          member.role !== "OWNER" && (
            <>
              <button
                onClick={() => onManagePermissions?.(member)}
                title="Phân quyền chi tiết"
                className="p-2 text-[var(--ink-3)] hover:text-[var(--warm-600)] hover:bg-[var(--warm-fill)] rounded-xl"
              >
                <Shield size={18} />
              </button>
              <button
                onClick={() => onKick?.(member.userId)}
                title="Mời ra khỏi lớp"
                className="p-2 text-[var(--ink-3)] hover:text-[var(--red-text)] hover:bg-[var(--red-fill)] rounded-xl"
              >
                <CircleX size={18} />
              </button>
            </>
          )
        )}
      </div>
    </div>
  );
};
