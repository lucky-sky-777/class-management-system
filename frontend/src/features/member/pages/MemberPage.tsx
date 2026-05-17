// src/features/member/pages/MemberPage.tsx
import { useState } from "react";
import { useParams } from "react-router-dom";
import { useMembers } from "@features/member/hooks/useMembers";
import { useAuth } from "@features/auth";
import { memberAPI } from "@features/member/api";
import { UserCheck } from "lucide-react";
import { GroupSection } from "./GroupSection";
import type { MemberRole } from "@features/member/types";

export const MemberPage = () => {
  const { classId } = useParams<{ classId: string }>();
  const { user } = useAuth();
  const [showPending, setShowPending] = useState(false);
  const { classInfo, members, isLoading, myRole, refresh } = useMembers(
    classId!,
    user?.id,
  );

  // --- CÁC HÀM XỬ LÝ TƯƠNG TÁC ---
  const handleUpdateRole = async (userId: number, currentRole: MemberRole) => {
    try {
      const newRole: MemberRole =
        currentRole === "CLASS_ADMIN" ? "CLASS_MEMBER" : "CLASS_ADMIN";
      await memberAPI.updateRole(classId!, userId, newRole);
      refresh(true);
    } catch {
      alert("Lỗi khi cập nhật quyền hạn");
    }
  };

  const handleKick = async (userId: number) => {
    if (window.confirm("Xóa thành viên này khỏi lớp học?")) {
      try {
        await memberAPI.kickMember(classId!, userId);
        refresh(true);
      } catch {
        alert("Lỗi khi xóa thành viên");
      }
    }
  };

  // --- KIỂM TRA ĐIỀU KIỆN RENDER ---
  if (!classInfo || isLoading) {
    return (
      <div className="p-10 text-center text-[var(--ink-3)] animate-pulse font-medium bg-[var(--bg-paper)] min-h-screen">
        Đang chuẩn bị dữ liệu...
      </div>
    );
  }

  const pendingMembers = members.filter((m) => m.role === "PENDING");
  const isPrivateClass =
    classInfo.type === "PRIVATE" || classInfo.privacy === "PRIVATE";

  return (
    <div className="max-w-3xl mx-auto py-8 px-4 bg-[var(--bg-paper)] min-h-screen transition-colors duration-300">
      <header className="mb-10">
        <h2 className="text-2xl font-black text-[var(--ink-1)] uppercase tracking-tight">
          Thành viên lớp học
        </h2>
        <p className="text-[var(--ink-3)] text-sm font-medium">
          Lớp: {classInfo.name} (Mã lớp: {classInfo.code})
        </p>
      </header>

      {/* Banner duyệt thành viên cho Private Class */}
      {(myRole === "OWNER" || myRole === "CLASS_ADMIN") && isPrivateClass && (
        <div className="mb-10 p-5 bg-[var(--amber-fill)] border border-[var(--amber-border)] rounded-2xl flex flex-col sm:flex-row items-center justify-between gap-4 shadow-sm">
          <div className="flex items-center gap-4">
            <div className="p-2.5 bg-[var(--bg-surface)] text-[var(--amber-text)] rounded-xl shadow-sm">
              <UserCheck size={24} />
            </div>
            <div>
              <h4 className="font-bold text-[var(--amber-text)] text-sm uppercase tracking-wide">
                Yêu cầu tham gia
              </h4>
              <p className="text-xs text-[var(--ink-2)] font-medium">
                Có {pendingMembers.length} thành viên đang chờ bạn phê duyệt.
              </p>
            </div>
          </div>
          <button
            onClick={() => setShowPending(!showPending)}
            className="w-full sm:w-auto px-6 py-2.5 bg-[var(--amber-text)] text-white text-xs font-black rounded-xl hover:opacity-90 transition-all uppercase tracking-wider shadow-md"
          >
            {showPending ? "Đóng danh sách" : "Xem ngay"}
          </button>
        </div>
      )}

      {/* Khu vực chờ duyệt */}
      {showPending && (
        <div className="mb-10 animate-in fade-in slide-in-from-top-2 duration-300">
          <GroupSection
            title="Đang chờ duyệt"
            data={pendingMembers}
            borderColor="var(--amber-border)"
            textColor="var(--amber-text)"
            myRole={myRole}
            isPendingSection={true}
            onRefresh={refresh}
          />
        </div>
      )}

      {/* Nhóm 1: Chủ sở hữu */}
      <GroupSection
        title="Chủ sở hữu"
        data={members.filter((m) => m.role === "OWNER")}
        borderColor="var(--red-border)"
        textColor="var(--red-text)"
        myRole={myRole}
        onUpdateRole={handleUpdateRole}
        onKick={handleKick}
        onRefresh={refresh}
      />

      {/* Nhóm 2: Quản trị viên */}
      <GroupSection
        title="Quản trị viên"
        data={members.filter((m) => m.role === "CLASS_ADMIN")}
        borderColor="var(--warm-border)"
        textColor="var(--warm-600)"
        myRole={myRole}
        onUpdateRole={handleUpdateRole}
        onKick={handleKick}
        onRefresh={refresh}
      />

      {/* Nhóm 3: Thành viên thường */}
      <GroupSection
        title="Thành viên"
        data={members.filter((m) => m.role === "CLASS_MEMBER")}
        borderColor="var(--green-border)"
        textColor="var(--green-text)"
        myRole={myRole}
        onUpdateRole={handleUpdateRole}
        onKick={handleKick}
        onRefresh={refresh}
      />
    </div>
  );
};
