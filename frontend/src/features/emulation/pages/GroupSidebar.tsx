import { useState } from "react";
import { Plus, Minus, Pencil, Check, X, Users, Trash2 } from "lucide-react";
import { Modal } from "@shared/components/ui/Modal";
import type { GroupItem, GroupMember } from "@features/emulation/types";
import { useToastStore } from "@app/store";
import { ToastType } from "@shared/domain/enums";

export const GroupSidebar = ({
  groups,
  selectedTeam,
  setSelectedTeam,
  addGroup,
  editGroup,
  setShowDeleteModal,
  fetchGroupMembers,
  fetchUngroupedMembers,
  addMemberToGroup,
  removeMemberFromGroup,
}: {
  classId: string;
  groups: GroupItem[];
  selectedTeam: number;
  setSelectedTeam: (id: number) => void;
  addGroup: () => void;
  editGroup: (id: number, name: string) => Promise<boolean>;
  setShowDeleteModal: (show: boolean) => void;
  fetchGroupMembers: (groupId: number) => Promise<GroupMember[]>;
  fetchUngroupedMembers: () => Promise<GroupMember[]>;
  addMemberToGroup: (groupId: number, userId: string) => Promise<boolean>;
  removeMemberFromGroup: (groupId: number, userId: string) => Promise<boolean>;
}) => {
  const [editingGroupId, setEditingGroupId] = useState<number | null>(null);
  const [editGroupName, setEditGroupName] = useState("");

  // --- STATE QUẢN LÝ THÀNH VIÊN ---
  const [showMemberModal, setShowMemberModal] = useState(false);
  const [activeGroup, setActiveGroup] = useState<GroupItem | null>(null);
  const [members, setMembers] = useState<GroupMember[]>([]);
  const [availableMembers, setAvailableMembers] = useState<GroupMember[]>([]);
  const [isLoadingMembers, setIsLoadingMembers] = useState(false);
  const [newUserId, setNewUserId] = useState("");

  const [confirmDelete, setConfirmDelete] = useState<{
    isOpen: boolean;
    userId: string | null;
  }>({
    isOpen: false,
    userId: null,
  });

  const showToast = useToastStore((state) => state.showToast);

  const handleSaveGroupName = async (id: number) => {
    if (!editGroupName.trim()) {
      showToast("Tên tổ không được để trống!", ToastType.WARNING);
      return;
    }

    try {
      const isSuccess = await editGroup(id, editGroupName.trim());
      if (isSuccess) {
        showToast("Đã đổi tên tổ thành công!", ToastType.SUCCESS);
        setEditingGroupId(null); // Tắt chế độ chỉnh sửa
      } else {
        showToast("Không thể cập nhật tên tổ!", ToastType.ERROR);
      }
    } catch {
      showToast("Đã có lỗi xảy ra!", ToastType.ERROR);
    }
  };

  // 1. Mở Modal và Load danh sách
  const handleOpenMembers = async (group: GroupItem) => {
    setActiveGroup(group);
    setShowMemberModal(true);
    setIsLoadingMembers(true);
    try {
      // Chạy song song 2 API lấy data bằng Promise.all cực tối ưu
      const [groupMembers, ungrouped] = await Promise.all([
        fetchGroupMembers(group.id),
        fetchUngroupedMembers(),
      ]);
      setMembers(groupMembers);
      setAvailableMembers(ungrouped);
    } catch (error) {
      console.error("Lỗi tải dữ liệu thành viên:", error);
    } finally {
      setIsLoadingMembers(false);
    }
  };

  // 2. Thêm thành viên
  const handleAddMember = async () => {
    if (!activeGroup?.id) {
      showToast("Không tìm thấy thông tin tổ hiện tại!", ToastType.ERROR);
      return;
    }

    if (!newUserId) {
      showToast("Vui lòng chọn học sinh!", ToastType.WARNING);
      return;
    }
    try {
      const isSuccess = await addMemberToGroup(activeGroup.id, newUserId);
      if (isSuccess) {
        setNewUserId("");
        showToast("Đã thêm thành viên vào tổ!", ToastType.SUCCESS);
        // Load lại danh sách mới nhất sau khi thêm thành công
        const [groupMembers, ungrouped] = await Promise.all([
          fetchGroupMembers(activeGroup.id),
          fetchUngroupedMembers(),
        ]);
        setMembers(groupMembers);
        setAvailableMembers(ungrouped);
      } else {
        showToast("Lỗi thêm thành viên!", ToastType.ERROR);
      }
    } catch {
      showToast("Có lỗi xảy ra khi thêm!", ToastType.ERROR);
    }
  };

  // 3. Xóa thành viên
  const handleRemoveMember = async (userId: string) => {
    setConfirmDelete({ isOpen: true, userId });
  };

  // thực hiện hành động sau khi người dùng bấm "Xác nhận"
  const executeRemoveMember = async () => {
    if (!confirmDelete.userId || !activeGroup?.id) return;

    try {
      const isSuccess = await removeMemberFromGroup(
        activeGroup.id,
        confirmDelete.userId,
      );
      if (isSuccess) {
        showToast("Đã xóa thành viên thành công!", ToastType.SUCCESS);
        // Cập nhật UI local
        setMembers(
          members.filter(
            (m) => String(m.user_id || m.id) !== String(confirmDelete.userId),
          ),
        );
        const ungrouped = await fetchUngroupedMembers();
        setAvailableMembers(ungrouped);
      } else {
        showToast("Lỗi khi xóa thành viên!", ToastType.ERROR);
      }
    } catch {
      showToast("Có lỗi xảy ra!", ToastType.ERROR);
    } finally {
      setConfirmDelete({ isOpen: false, userId: null });
    }
  };

  return (
    <div className="space-y-4">
      {/* HEADER DANH SÁCH TỔ */}
      <div className="flex items-center justify-between px-2">
        <label className="text-[10px] font-black text-[var(--ink-3)] uppercase tracking-[0.2em]">
          Lựa chọn tổ
        </label>

        <div className="flex gap-1 bg-[var(--bg-surface-2)] p-1 rounded-lg border border-[var(--rule)]">
          <button
            onClick={() => setShowDeleteModal(true)}
            className="p-1 hover:text-[var(--red-text)] transition-colors"
          >
            <Minus size={12} />
          </button>
          <div className="w-[1px] bg-[var(--rule)] mx-0.5" />
          <button
            onClick={addGroup}
            className="p-1 hover:text-[var(--green-text)] transition-colors"
          >
            <Plus size={12} />
          </button>
        </div>
      </div>

      {/* DANH SÁCH TỔ */}
      <nav className="grid grid-cols-1 sm:flex sm:flex-col gap-1.5">
        {groups.map((group) => (
          <div
            key={group.id}
            className={`w-full flex items-center justify-between px-3 py-3 md:px-4 md:py-3 rounded-xl transition-all duration-200 group relative cursor-pointer border ${
              selectedTeam === group.id
                ? "bg-[var(--warm-fill)] text-[var(--warm-text)] shadow-sm border-[var(--warm-border)]"
                : "hover:bg-[var(--bg-surface-2)] text-[var(--ink-2)] border-transparent"
            }`}
            onClick={() => setSelectedTeam(group.id)}
          >
            <div className="flex items-center gap-3 relative z-10 flex-1 min-w-0">
              <span
                className={`w-1.5 h-1.5 rounded-full flex-shrink-0 ${
                  selectedTeam === group.id
                    ? "bg-[var(--warm-400)] scale-125"
                    : "bg-[var(--ink-4)]"
                }`}
              />

              {editingGroupId === group.id ? (
                <div
                  className="flex items-center gap-1.5 w-full mr-2"
                  onClick={(e) => e.stopPropagation()} // Không cho kích hoạt setSelectedTeam khi bấm vào vùng ô input
                >
                  <input
                    autoFocus
                    className="w-full bg-surface border border-[var(--rule-md)] rounded-lg text-xs px-2 py-1 outline-none text-ink-1 font-bold focus:border-[var(--warm-400)]"
                    value={editGroupName}
                    onChange={(e) => setEditGroupName(e.target.value)}
                    onKeyDown={(e) => {
                      if (e.key === "Enter") handleSaveGroupName(group.id); // Gõ Enter tự động lưu
                      if (e.key === "Escape") setEditingGroupId(null); // Gõ nút Esc để hủy chỉnh sửa nhanh
                    }}
                  />
                  {/* Nút Check xác nhận lưu */}
                  <button
                    onClick={() => handleSaveGroupName(group.id)}
                    className="text-green-600 hover:bg-green-50 p-1 rounded-md transition-colors shrink-0"
                    title="Lưu thay đổi"
                  >
                    <Check size={14} />
                  </button>
                  {/* Nút X để hủy bỏ lệnh sửa */}
                  <button
                    onClick={() => setEditingGroupId(null)}
                    className="text-red-500 hover:bg-red-50 p-1 rounded-md transition-colors shrink-0"
                    title="Hủy bỏ"
                  >
                    <X size={14} />
                  </button>
                </div>
              ) : (
                <span className="text-xs md:text-sm font-bold truncate">
                  {group.name}
                </span>
              )}
            </div>

            {/* Điều khiển tác vụ */}
            {editingGroupId !== group.id && (
              <div className="flex items-center gap-1 ml-2 opacity-100 md:opacity-0 md:group-hover:opacity-100 transition-all">
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    handleOpenMembers(group);
                  }}
                  className="p-1.5 text-[var(--ink-3)] hover:text-[var(--warm-600)] bg-[var(--bg-surface)] md:bg-transparent rounded-md border md:border-0 shadow-sm md:shadow-none"
                  title="Thành viên tổ"
                >
                  <Users size={14} />
                </button>

                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    setEditingGroupId(group.id);
                    setEditGroupName(group.name);
                  }}
                  className="p-1.5 text-[var(--ink-3)] hover:text-[var(--warm-600)] bg-[var(--bg-surface)] md:bg-transparent rounded-md border md:border-0 shadow-sm md:shadow-none"
                  title="Đổi tên tổ"
                >
                  <Pencil size={14} />
                </button>
              </div>
            )}
          </div>
        ))}
      </nav>

      {/* --- MODAL QUẢN LÝ THÀNH VIÊN --- */}
      <Modal
        isOpen={showMemberModal}
        onClose={() => setShowMemberModal(false)}
        title={`Tổ ${activeGroup?.name || ""}`}
      >
        <div className="p-0 sm:p-1 space-y-4">
          {/* BOX THÊM THÀNH VIÊN */}
          <div className="flex flex-col xs:flex-row gap-2 bg-[var(--bg-surface-2)] p-2 rounded-xl border border-[var(--rule)]">
            <select
              value={newUserId}
              onChange={(e) => setNewUserId(e.target.value)}
              className="w-full xs:flex-1 bg-[var(--bg-surface)] border border-[var(--rule)] rounded-lg h-10 px-3 text-sm font-semibold text-[var(--ink-1)] outline-none"
            >
              <option value="" disabled>
                Chọn học sinh...
              </option>
              {availableMembers.map((m) => (
                <option key={m.user_id || m.id} value={m.user_id || m.id}>
                  {m.user_display_name || m.display_name || "Học sinh"}
                </option>
              ))}
            </select>
            <button
              disabled={!newUserId}
              onClick={handleAddMember}
              className="w-full xs:w-auto h-10 px-6 bg-[var(--warm-600)] text-white rounded-lg text-xs font-black shadow-sm flex items-center justify-center gap-2 active:scale-95 transition-transform"
            >
              <Plus size={16} /> THÊM
            </button>
          </div>

          {/* DANH SÁCH THÀNH VIÊN */}
          <div className="space-y-2 max-h-[50vh] overflow-y-auto custom-scrollbar pr-1">
            {isLoadingMembers ? (
              <div className="py-10 text-center text-[var(--ink-3)] text-xs animate-pulse">
                Đang tải...
              </div>
            ) : members.length === 0 ? (
              <div className="py-10 text-center text-[var(--ink-3)] text-xs italic">
                Chưa có thành viên
              </div>
            ) : (
              members.map((m) => {
                const studentName =
                  m.user_display_name || m.display_name || m.full_name;
                const avatarChar = studentName
                  ? studentName.charAt(0).toUpperCase()
                  : "U";

                return (
                  <div
                    key={m.id || m.user_id}
                    className="flex items-center justify-between bg-[var(--bg-surface)] p-2.5 rounded-xl border border-[var(--rule)] shadow-sm"
                  >
                    <div className="flex items-center gap-3 min-w-0">
                      <div className="relative flex-shrink-0">
                        {m.user_avatar_url ? (
                          <img
                            src={m.user_avatar_url}
                            alt={studentName}
                            className="w-9 h-9 rounded-full object-cover border border-[var(--warm-border)] shadow-inner"
                          />
                        ) : (
                          <div className="w-9 h-9 rounded-full bg-[var(--warm-fill)] text-[var(--warm-text)] border border-[var(--warm-border)] font-black flex items-center justify-center text-xs shadow-inner">
                            {avatarChar}
                          </div>
                        )}

                        {/* Chấm trạng thái điểm danh */}
                        <div
                          className={`absolute -bottom-0.5 -right-0.5 w-3 h-3 rounded-full border-2 border-white ${
                            m.attendance_status === "PRESENT"
                              ? "bg-green-500"
                              : "bg-red-500"
                          }`}
                        />
                      </div>

                      <div className="min-w-0">
                        <p className="text-[11px] md:text-xs font-bold text-[var(--ink-1)] truncate uppercase tracking-tight">
                          {studentName || "Học sinh"}
                        </p>
                        <p className="text-[9px] text-[var(--ink-3)] font-medium">
                          Tham gia vào: {m.joined_at?.split(",")[1] || "..."}
                        </p>
                      </div>
                    </div>

                    <button
                      onClick={() =>
                        handleRemoveMember(String(m.user_id || m.id || ""))
                      }
                      className="p-2 text-[var(--ink-3)] hover:text-[var(--red-text)] hover:bg-[var(--red-fill)] rounded-lg transition-colors border border-transparent"
                    >
                      <Trash2 size={16} />
                    </button>
                  </div>
                );
              })
            )}
          </div>
        </div>
      </Modal>

      {/* MODAL XÁC NHẬN XÓA THÀNH VIÊN */}
      <Modal
        isOpen={confirmDelete.isOpen}
        onClose={() => setConfirmDelete({ isOpen: false, userId: null })}
        title="Xác nhận xóa"
      >
        <div className="space-y-4">
          <p className="text-sm text-[var(--ink-2)]">
            Bạn có chắc chắn muốn xóa học sinh này khỏi tổ không? Hành động này
            không thể hoàn tác.
          </p>
          <div className="flex gap-3">
            <button
              onClick={() => setConfirmDelete({ isOpen: false, userId: null })}
              className="flex-1 py-2 rounded-xl bg-[var(--bg-surface-2)] text-[var(--ink-2)] font-bold text-xs"
            >
              Hủy bỏ
            </button>
            <button
              onClick={executeRemoveMember}
              className="flex-1 py-2 rounded-xl bg-[var(--red-text)] text-white font-bold text-xs shadow-sm hover:opacity-90"
            >
              Xác nhận xóa
            </button>
          </div>
        </div>
      </Modal>
    </div>
  );
};
