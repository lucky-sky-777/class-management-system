import { useState } from "react";
import { Plus, Minus, Pencil, Check, X, Users, Trash2 } from "lucide-react";
import { Modal } from "@shared/components/ui/Modal";

interface GroupSidebarProps {
  classId: string;
  groups: any[];
  selectedTeam: number;
  setSelectedTeam: (id: number) => void;
  canEdit: boolean;
  addGroup: () => void;
  editGroup: (id: number, name: string) => Promise<boolean>;
  setShowDeleteModal: (show: boolean) => void;

  // 👉 4 Props lấy data từ Hook (Không gọi API trực tiếp nữa)
  fetchGroupMembers: (groupId: number) => Promise<any[]>;
  fetchUngroupedMembers: () => Promise<any[]>;
  addMemberToGroup: (groupId: number, userId: string) => Promise<boolean>;
  removeMemberFromGroup: (groupId: number, userId: string) => Promise<boolean>;
}

export const GroupSidebar = ({
  groups,
  selectedTeam,
  setSelectedTeam,
  canEdit,
  addGroup,
  editGroup,
  setShowDeleteModal,
  fetchGroupMembers,
  fetchUngroupedMembers,
  addMemberToGroup,
  removeMemberFromGroup,
}: GroupSidebarProps) => {
  const [editingGroupId, setEditingGroupId] = useState<number | null>(null);
  const [editGroupName, setEditGroupName] = useState("");

  // --- STATE QUẢN LÝ THÀNH VIÊN ---
  const [showMemberModal, setShowMemberModal] = useState(false);
  const [activeGroup, setActiveGroup] = useState<any>(null);
  const [members, setMembers] = useState<any[]>([]);
  const [availableMembers, setAvailableMembers] = useState<any[]>([]); // Chứa học sinh chưa có tổ
  const [isLoadingMembers, setIsLoadingMembers] = useState(false);
  const [newUserId, setNewUserId] = useState("");

  // 1. Mở Modal và Load danh sách
  const handleOpenMembers = async (group: any) => {
    setActiveGroup(group);
    setShowMemberModal(true);
    setIsLoadingMembers(true);
    try {
      // Gọi qua Hook để lấy 2 danh sách song song
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
    if (!newUserId) return alert("Vui lòng chọn một học sinh!");
    try {
      const isSuccess = await addMemberToGroup(activeGroup.id, newUserId);
      if (isSuccess) {
        setNewUserId("");
        // Reload lại cả 2 danh sách
        const [groupMembers, ungrouped] = await Promise.all([
          fetchGroupMembers(activeGroup.id),
          fetchUngroupedMembers(),
        ]);
        setMembers(groupMembers);
        setAvailableMembers(ungrouped);
      } else {
        alert("Lỗi thêm thành viên!");
      }
    } catch (error) {
      alert("Đã có lỗi xảy ra!");
    }
  };

  // 3. Xóa thành viên
  const handleRemoveMember = async (userId: string) => {
    if (!confirm("Bạn có chắc muốn xóa học sinh này khỏi tổ?")) return;
    try {
      const isSuccess = await removeMemberFromGroup(activeGroup.id, userId);
      if (isSuccess) {
        // Cập nhật lại UI lập tức
        setMembers(
          members.filter((m) => String(m.user_id || m.id) !== String(userId)),
        );
        // Load lại danh sách khay chờ để học sinh vừa xóa hiện lên lại
        const ungrouped = await fetchUngroupedMembers();
        setAvailableMembers(ungrouped);
      } else {
        alert("Lỗi khi xóa thành viên!");
      }
    } catch (error) {
      alert("Đã có lỗi xảy ra!");
    }
  };

  return (
    <div className="space-y-4">
      {/* HEADER DANH SÁCH TỔ */}
      <div className="flex items-center justify-between px-2">
        <label className="text-[10px] font-black text-[var(--ink-3)] uppercase tracking-[0.2em]">
          Lựa chọn tổ
        </label>
        {canEdit && (
          <div className="flex gap-1 bg-[var(--bg-surface-2)] p-1 rounded-lg border border-[var(--rule)]">
            <button
              onClick={() => setShowDeleteModal(true)}
              className="p-1 hover:text-[var(--red-text)] transition-colors"
            >
              <Minus size={12} /> {/* Tăng nhẹ size icon cho mobile dễ bấm */}
            </button>
            <div className="w-[1px] bg-[var(--rule)] mx-0.5" />
            <button
              onClick={addGroup}
              className="p-1 hover:text-[var(--green-text)] transition-colors"
            >
              <Plus size={12} />
            </button>
          </div>
        )}
      </div>

      {/* DANH SÁCH TỔ - Tối ưu click area cho mobile */}
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
                  className="flex items-center gap-2 w-full"
                  onClick={(e) => e.stopPropagation()}
                >
                  <input
                    autoFocus
                    className="w-full bg-white border border-[var(--rule)] rounded text-xs px-2 py-1 outline-none text-black"
                    value={editGroupName}
                    onChange={(e) => setEditGroupName(e.target.value)}
                  />
                  {/* Nút lưu nhanh cho mobile */}
                  <button className="text-green-600">
                    <Check size={16} />
                  </button>
                </div>
              ) : (
                <span className="text-xs md:text-sm font-bold truncate">
                  {group.name}
                </span>
              )}
            </div>

            {/* Điều khiển: Trên mobile luôn hiện nhẹ, trên desktop hiện khi hover */}
            <div className="flex items-center gap-1 ml-2 opacity-100 md:opacity-0 md:group-hover:opacity-100 transition-all">
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  handleOpenMembers(group);
                }}
                className="p-1.5 text-[var(--ink-3)] hover:text-[var(--warm-600)] bg-[var(--bg-surface)] md:bg-transparent rounded-md border md:border-0"
              >
                <Users size={14} />
              </button>
              {canEdit && (
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    setEditingGroupId(group.id);
                    setEditGroupName(group.name);
                  }}
                  className="p-1.5 text-[var(--ink-3)] hover:text-[var(--warm-600)] bg-[var(--bg-surface)] md:bg-transparent rounded-md border md:border-0"
                >
                  <Pencil size={14} />
                </button>
              )}
            </div>
          </div>
        ))}
      </nav>

      {/* --- MODAL QUẢN LÝ THÀNH VIÊN - TỐI ƯU MOBILE --- */}
      <Modal
        isOpen={showMemberModal}
        onClose={() => setShowMemberModal(false)}
        title={`Tổ ${activeGroup?.name || ""}`}
      >
        <div className="p-0 sm:p-1 space-y-4">
          {/* BOX THÊM THÀNH VIÊN - Stack dọc trên mobile cực nhỏ */}
          {canEdit && (
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
          )}

          {/* DANH SÁCH THÀNH VIÊN - Row gọn gàng */}
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
                        <div className="w-9 h-9 rounded-full bg-[var(--warm-fill)] text-[var(--warm-text)] border border-[var(--warm-border)] font-black flex items-center justify-center text-xs shadow-inner">
                          {avatarChar}
                        </div>
                        {/* Status dot */}
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

                    {canEdit && (
                      <button
                        onClick={() => handleRemoveMember(m.user_id || m.id)}
                        className="p-2 text-[var(--ink-3)] hover:text-[var(--red-text)] hover:bg-[var(--red-fill)] rounded-lg transition-colors border border-transparent"
                      >
                        <Trash2 size={16} />
                      </button>
                    )}
                  </div>
                );
              })
            )}
          </div>
        </div>
      </Modal>
    </div>
  );
};
