// src/features/member/pages/MemberPage.tsx
import { useState } from "react";
import { useParams } from "react-router-dom";
import { useMembers } from "@features/member/hooks/useMembers";
import { useAuth } from "@features/auth";
import { memberAPI } from "@features/member/api";
import { UserCheck } from "lucide-react";
import { GroupSection } from "./GroupSection";
import type { Member, MemberRole } from "@features/member/types";
import { useToastStore } from "@app/store";
import { ToastType, PermissionCode } from "@shared/domain/enums";
import { MemberPermissionsModal } from "@features/member/pages/MemberPermissionsModal";

export const MemberPage = () => {
  const { classId } = useParams<{ classId: string }>();
  const { user } = useAuth();
  const [showPending, setShowPending] = useState(false);
  const { classInfo, members, isLoading, myRole, refresh } = useMembers(
    classId!,
    user?.id,
  );

  const showToast = useToastStore((state) => state.showToast);

  // State quản lý Modal phân quyền
  const [isPermissionsModalOpen, setIsPermissionsModalOpen] = useState(false);
  const [selectedMember, setSelectedMember] = useState<Member | null>(null);
  const [isSubmittingPermissions, setIsSubmittingPermissions] = useState(false);

  const handleKick = async (userId: number) => {
    if (window.confirm("Xóa thành viên này khỏi lớp học?")) {
      try {
        await memberAPI.kickMember(classId!, userId);
        showToast("Đã xóa thành viên khỏi lớp!", ToastType.SUCCESS);
        refresh(true);
      } catch {
        showToast("Lỗi khi xóa thành viên", ToastType.ERROR);
      }
    }
  };

  // Mở Modal Phân quyền chi tiết
  const handleManagePermissionsClick = (member: Member) => {
    setSelectedMember(member);
    setIsPermissionsModalOpen(true);
  };

  // Lưu quyền hạn chi tiết từ Modal
  const handleSavePermissions = async (
    role: MemberRole,
    permissions: PermissionCode[],
  ) => {
    if (!selectedMember) return;

    setIsSubmittingPermissions(true);
    try {
      await memberAPI.updateMemberAuthorities(
        classId!,
        selectedMember.userId,
        role,
        permissions,
      );
      showToast(
        `Đã phân quyền thành công cho ${selectedMember.displayName}`,
        ToastType.SUCCESS,
      );
      setIsPermissionsModalOpen(false);
      refresh(true);
    } catch (error) {
      console.error("Lỗi khi lưu phân quyền:", error);
      showToast("Có lỗi xảy ra khi lưu phân quyền chi tiết.", ToastType.ERROR);
    } finally {
      setIsSubmittingPermissions(false);
    }
  };

  // 1. Định nghĩa logic xác định xem ai là "Quản trị viên"
const isAdminOrHasPermissions = (m: Member) => {
    // 1. Kiểm tra role cứng
    if (m.role === "CLASS_ADMIN" || m.role === "OWNER") return true;
    
    // 2. Kiểm tra permissions an toàn:
    // Dùng optional chaining (?.) để lấy length. 
    // Nếu không có permissions thì nó trả về undefined, so sánh với > 0 sẽ là false.
    return (m.permissions?.length ?? 0) > 0;
};

// 2. Chia lại danh sách thành viên
const ownerMembers = members.filter((m) => m.role === "OWNER");
const adminMembers = members.filter((m) => m.role !== "OWNER" && isAdminOrHasPermissions(m));
const regularMembers = members.filter((m) => m.role !== "OWNER" && !isAdminOrHasPermissions(m));

  // --- KIỂM TRA ĐIỀU KIỆN RENDER ---
  if (!classInfo || isLoading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[400px] space-y-4">
        <div className="animate-in fade-in duration-300 flex flex-col items-center space-y-4">
          <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-[var(--warm-400)]"></div>
          <p className="text-ink-2 text-sm font-medium">
            Đang tải danh sách thành viên...
          </p>
        </div>
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
        data={ownerMembers}
        borderColor="var(--red-border)"
        textColor="var(--red-text)"
        myRole={myRole}
        onKick={handleKick}
        onManagePermissions={handleManagePermissionsClick}
        onRefresh={refresh}
      />

      {/* Nhóm 2: Quản trị viên */}
      <GroupSection
        title="Quản trị viên"
        data={adminMembers}
        borderColor="var(--warm-border)"
        textColor="var(--warm-600)"
        myRole={myRole}
        onKick={handleKick}
        onManagePermissions={handleManagePermissionsClick}
        onRefresh={refresh}
      />

      {/* Nhóm 3: Thành viên thường */}
      <GroupSection
        title="Thành viên"
        data={regularMembers}
        borderColor="var(--green-border)"
        textColor="var(--green-text)"
        myRole={myRole}
        onKick={handleKick}
        onManagePermissions={handleManagePermissionsClick}
        onRefresh={refresh}
      />

      {/* Modal phân quyền chi tiết */}
      <MemberPermissionsModal
        isOpen={isPermissionsModalOpen}
        onClose={() => setIsPermissionsModalOpen(false)}
        member={selectedMember}
        onSave={handleSavePermissions}
        isSubmitting={isSubmittingPermissions}
      />
    </div>
  );
};
