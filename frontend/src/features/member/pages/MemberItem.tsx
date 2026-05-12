// src/features/member/components/MemberItem.tsx
import { ShieldPlus, ShieldMinus, CircleX, Check, X } from "lucide-react";
import { useParams } from "react-router-dom";
import { memberAPI } from "@features/member/api";
import type { Member, MemberRole } from "@features/member/types";

interface MemberItemProps {
  member: Member;
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
  onRefresh 
}: MemberItemProps) => {
  const { classId } = useParams();
  const name = member.displayName || "Thành viên";
  const firstChar = name.trim().charAt(0).toUpperCase();

  // Xử lý Duyệt yêu cầu (Dành cho danh sách chờ)
  const handleApprove = async () => {
    console.log("Bắt đầu duyệt requestId:", member.requestId);
    if (!member.requestId) return alert("Thiếu Request ID để duyệt!");
    try {
      await memberAPI.approveRequest(classId!, member.requestId);
      onRefresh?.(true); // Load lại danh sách ngầm (silent)
    } catch (error) {
      console.error("Lỗi duyệt:", error);
      alert("Không thể duyệt thành viên này.");
    }
  };

  // Xử lý Từ chối yêu cầu (Dành cho danh sách chờ)
  const handleReject = async () => {
    if (!member.requestId) return alert("Thiếu Request ID để từ chối!");
    if (window.confirm(`Bạn có chắc muốn từ chối yêu cầu của ${name}?`)) {
      try {
        await memberAPI.rejectRequest(classId!, member.requestId);
        onRefresh?.(true);
      } catch (error) {
        console.error("Lỗi từ chối:", error);
        alert("Có lỗi xảy ra khi từ chối yêu cầu.");
      }
    }
  };

  return (
    <div className="group flex items-center justify-between py-3 px-2 hover:bg-[var(--bg-surface-2)] rounded-xl transition-all border-b border-[var(--rule-lg)] last:border-0">
      <div className="flex items-center gap-4">
        {/* Avatar với Gradient xịn xò */}
        <div className="w-10 h-10 rounded-full bg-gradient-to-br from-[var(--warm-500)] to-[var(--warm-700)] flex items-center justify-center text-white font-bold shadow-sm ring-2 ring-[var(--bg-paper)]">
          {firstChar}
        </div>
        
        <div>
          <p className="text-sm font-bold text-[var(--ink-1)] leading-tight">{name}</p>
          <p className="text-[10px] text-[var(--ink-3)] font-medium mt-0.5">
            @{member.username || `user_${member.userId}`} • {member.joinedAt}
          </p>
        </div>
      </div>

      <div className="flex items-center gap-2">
        {/* CASE 1: Đang chờ duyệt (Hiện nút Tích xanh/X đỏ) */}
        {isPending ? (
          <div className="flex gap-2">
            <button 
              onClick={handleApprove}
              className="p-2 bg-[var(--green-fill)] text-[var(--green-text)] rounded-xl hover:scale-105 active:scale-95 transition-all shadow-sm"
              title="Chấp nhận"
            >
              <Check size={18} strokeWidth={3} />
            </button>
            <button 
              onClick={handleReject}
              className="p-2 bg-[var(--red-fill)] text-[var(--red-text)] rounded-xl hover:scale-105 active:scale-95 transition-all shadow-sm"
              title="Từ chối"
            >
              <X size={18} strokeWidth={3} />
            </button>
          </div>
        ) : (
          /* CASE 2: Đã là thành viên (Hiện nút quản lý khi hover) */
          <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-all duration-200 translate-x-2 group-hover:translate-x-0">
            
            {/* Quyền của OWNER đối với Admin/Member */}
            {myRole === "OWNER" && member.role !== "OWNER" && (
              <>
                <button
                  onClick={() => onUpdateRole?.(member.userId, member.role)}
                  className="p-2 hover:bg-[var(--warm-fill)] rounded-lg text-[var(--warm-600)] transition-colors"
                  title={member.role === "CLASS_ADMIN" ? "Hạ cấp xuống thành viên" : "Thăng cấp lên quản trị viên"}
                >
                  {member.role === "CLASS_ADMIN" ? <ShieldMinus size={18}/> : <ShieldPlus size={18}/>}
                </button>
                <button
                  onClick={() => onKick?.(member.userId)}
                  className="p-2 hover:bg-[var(--red-fill)] rounded-lg text-[var(--red-text)] transition-colors"
                  title="Xóa khỏi lớp học"
                >
                  <CircleX size={18} />
                </button>
              </>
            )}

            {/* Quyền của ADMIN đối với Member thường */}
            {myRole === "CLASS_ADMIN" && member.role === "CLASS_MEMBER" && (
              <button
                onClick={() => onKick?.(member.userId)}
                className="p-2 hover:bg-[var(--red-fill)] rounded-lg text-[var(--red-text)] transition-colors"
                title="Khai trừ thành viên"
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