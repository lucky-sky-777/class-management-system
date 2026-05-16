import { useState } from "react";
import { Plus, Minus, Pencil, Check, X } from "lucide-react";
import { useParams } from "react-router-dom";
import { useEmulation } from "@features/emulation/hooks/useEmulation";
import { RankingTable } from "@features/emulation/pages/RankingTable";
import { HistoryTable } from "@features/emulation/pages/HistoryTable";
import { GroupSidebar } from "@features/emulation/pages/GroupSidebar";
import { Modal } from "@shared/components/ui/Modal";

export const Emulation = () => {
  const { classId } = useParams<{ classId: string }>();
  const {
    data,
    groups,
    isLoading,
    filters,
    weeks,
    canEdit,
    setFilters,
    addGroup,
    addPoint,
    editGroup,
    removeGroup,
    fetchGroupMembers,
    fetchUngroupedMembers,
    addMemberToGroup,
    removeMemberFromGroup,
  } = useEmulation(classId!);

  const [selectedTeam, setSelectedTeam] = useState(1);
  const [showPointModal, setShowPointModal] = useState(false);
  const [pointForm, setPointForm] = useState<{
    content: string;
    points: number | string;
  }>({
    content: "",
    points: 0,
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  //state remove
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [groupToDelete, setGroupToDelete] = useState<number | null>(null);
  // //state edit
  // const [editingGroupId, setEditingGroupId] = useState<number | null>(null);
  // const [editGroupName, setEditGroupName] = useState("");
  //state phân quyền
  // 3. EARLY RETURN (Chỉ return sau khi đã gọi hết Hooks)
  if (isLoading || !data) {
    return (
      <div className="p-10 text-center text-ink-3 font-medium animate-pulse">
        Đang tải dữ liệu thi đua...
      </div>
    );
  }

  // const currentTeamMembers = data?.teams?.[selectedTeam] || [];

  // 4. HÀM XỬ LÝ GHI ĐIỂM
  const handleSubmitPoint = async () => {
    // 1. Validation sớm
    const pointsToSubmit = Number(pointForm.points);
    if (!pointForm.content.trim()) return alert("Vui lòng nhập nội dung!");
    if (isNaN(pointsToSubmit) || pointsToSubmit === 0) {
      return alert("Số điểm không hợp lệ!");
    }

    try {
      setIsSubmitting(true); // Khóa nút bấm lại

      // 2. Gọi hàm addPoint từ Hook (Sử dụng hàm đã bọc trong useEmulation để đồng bộ)
      // Lưu ý: Nên dùng hàm addPoint từ Hook trả về thay vì gọi trực tiếp emulationAPI ở đây
      const result = await addPoint(
        selectedTeam,
        pointForm.content,
        pointsToSubmit,
      );

      if (result.success) {
        // 3. Xử lý sau khi thành công
        setShowPointModal(false);
        setPointForm({ content: "", points: 0 });

        // Thông báo nhẹ nhàng (nên dùng Toast nếu có, alert hơi "cổ điển")
        console.log(`Đã cập nhật điểm cho Tổ ${selectedTeam}`);
      } else {
        alert("Ghi điểm thất bại. Vui lòng kiểm tra lại kết nối!");
      }
    } catch (error) {
      console.error("Lỗi nhập điểm:", error);
      alert("Đã có lỗi xảy ra khi ghi điểm!");
    } finally {
      setIsSubmitting(false); // Mở khóa nút bấm
    }
  };

  return (
    <div className="space-y-6 animate-fade-in pb-10">
      {/* THANH BỘ LỌC */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center py-2 px-1 gap-4 animate-fade-in">
        {/* Cụm chọn tuần - Nhỏ nhắn */}
        <div className="flex items-center gap-3 bg-[var(--bg-surface)] p-1.5 px-3 rounded-[var(--r-lg)] border border-[var(--rule)] shadow-[var(--shadow-xs)]">
          <span className="text-[10px] text-[var(--ink-3)] font-black uppercase tracking-widest">
            Tuần:
          </span>
          <select
            className="text-[12px] font-bold bg-transparent text-[var(--ink-1)] border-none p-0 cursor-pointer outline-none focus:ring-0"
            value={filters.startDate}
            onChange={(e) => {
              const selected = weeks.find(
                (w: any) => w.start_at === e.target.value,
              );
              if (selected) {
                setFilters({
                  startDate: selected.start_at,
                  endDate: selected.end_at,
                });
              }
            }}
          >
            {weeks.map((w: any) => (
              <option key={w.week} value={w.start_at}>
                Tuần {w.week} ({w.formatted_start_at} - {w.formatted_end_at})
                {w.is_current_week ? " • Hiện tại" : ""}
              </option>
            ))}
          </select>
        </div>

        {/* NÚT NỘI QUY - Màu Sage (Xanh lá xám) */}
        <button className="bg-[var(--sage-fill)] text-[var(--sage-text)] border border-[var(--sage-border)] px-4 py-1.5 rounded-[var(--r-full)] font-black text-[10px] uppercase tracking-tighter hover:bg-[var(--bg-surface-3)] transition-all active:scale-95 shadow-sm">
          Nội quy thi đua
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6 items-start">
        {/* DANH SÁCH TỔ ĐÃ ĐƯỢC TÁCH COMPONENT */}
        <div className="lg:col-span-1">
          <GroupSidebar
            classId={classId!}
            groups={groups}
            selectedTeam={selectedTeam}
            setSelectedTeam={setSelectedTeam}
            canEdit={canEdit}
            addGroup={addGroup}
            editGroup={editGroup}
            setShowDeleteModal={setShowDeleteModal}
            fetchGroupMembers={fetchGroupMembers}
            fetchUngroupedMembers={fetchUngroupedMembers}
            addMemberToGroup={addMemberToGroup}
            removeMemberFromGroup={removeMemberFromGroup}
          />
        </div>

        {/* LỊCH SỬ THAY ĐỔI */}
        <div className="lg:col-span-3">
          <HistoryTable
            selectedTeam={selectedTeam}
            history={data.history}
            canEdit={canEdit}
            onOpenPointModal={() => setShowPointModal(true)}
          />
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <RankingTable title="Xếp hạng tuần" rows={data.weeklyRanking} />
        <RankingTable
          title="Xếp hạng tháng"
          rows={data.monthlyRanking}
          // isMonthly
        />
      </div>

      {/* MODAL GHI ĐIỂM GIỮ NGUYÊN */}
      <Modal
        isOpen={showPointModal}
        onClose={() => {
          setShowPointModal(false);
          setPointForm({ content: "", points: 0 });
        }}
        title={`Ghi điểm Tổ ${selectedTeam}`}
      >
        <div className="space-y-5 p-1">
          {/* TRƯỜNG NỘI DUNG */}
          <div>
            <label className="text-[10px] font-black text-[var(--ink-3)] uppercase tracking-widest mb-1.5 block">
              Nội dung vi phạm / Khen thưởng
            </label>
            <input
              type="text"
              className="w-full bg-[var(--bg-surface-2)] border border-[var(--rule)] rounded-[var(--r-lg)] p-3 text-sm text-[var(--ink-1)] placeholder:text-[var(--ink-3)] focus:ring-2 focus:ring-[var(--warm-400)] focus:border-transparent outline-none transition-all"
              placeholder="Ví dụ: Hăng hái xây dựng bài..."
              value={pointForm.content}
              onChange={(e) =>
                setPointForm({ ...pointForm, content: e.target.value })
              }
            />
          </div>

          {/* TRƯỜNG SỐ ĐIỂM */}
          <div>
            <label className="text-[10px] font-black text-[var(--ink-3)] uppercase tracking-widest mb-1.5 block">
              Số điểm (+ cộng, - trừ)
            </label>
            <div className="relative">
              <input
                type="text"
                className="w-full bg-[var(--bg-surface-2)] border border-[var(--rule)] rounded-[var(--r-lg)] p-3 pl-4 text-sm font-bold text-[var(--ink-1)] placeholder:text-[var(--ink-3)] focus:ring-2 focus:ring-[var(--warm-400)] outline-none transition-all"
                placeholder="Nhập ví dụ: 10 hoặc -5"
                value={pointForm.points === 0 ? "" : pointForm.points}
                onChange={(e) => {
                  const val = e.target.value;
                  if (val === "" || val === "-" || !isNaN(Number(val))) {
                    setPointForm({ ...pointForm, points: val });
                  }
                }}
                onKeyDown={(e) => e.key === "Enter" && handleSubmitPoint()}
              />
              {/* Badge gợi ý nhỏ bên cạnh */}
              <div className="absolute right-3 top-1/2 -translate-y-1/2 text-[9px] font-black text-[var(--ink-3)] uppercase">
                Points
              </div>
            </div>
          </div>

          {/* CỤM NÚT BẤM */}
          <div className="flex gap-3 pt-3">
            <button
              disabled={isSubmitting}
              onClick={() => {
                setShowPointModal(false);
                setPointForm({ content: "", points: 0 });
              }}
              className="flex-1 py-2.5 bg-[var(--bg-surface-3)] text-[var(--ink-2)] rounded-[var(--r-lg)] font-bold text-xs hover:bg-[var(--ink-4)] transition-colors"
            >
              HỦY BỎ
            </button>
            <button
              disabled={isSubmitting}
              onClick={handleSubmitPoint}
              className="flex-1 py-2.5 bg-[var(--warm-400)] text-white rounded-[var(--r-lg)] font-bold text-xs shadow-md shadow-blue-900/10 hover:bg-[var(--warm-600)] transition-all active:scale-95"
            >
              XÁC NHẬN GHI
            </button>
          </div>
        </div>
      </Modal>

      {/* MODAL XÓA TỔ */}
      <Modal
        isOpen={showDeleteModal}
        onClose={() => {
          setShowDeleteModal(false);
          setGroupToDelete(null);
        }}
        title="Xóa Tổ Chỉ Định"
      >
        <div className="space-y-5 p-2">
          {/* Hộp cảnh báo - Dùng bộ Semantic Danger (--red-fill, --red-border, --red-text) */}
          <div className="bg-[var(--red-fill)] text-[var(--red-text)] text-sm p-4 rounded-[var(--r-md)] border border-[var(--red-border)] font-medium flex gap-3 leading-relaxed">
            <span className="flex-shrink-0 text-base">⚠️</span>
            <span>
              Hành động này sẽ xóa toàn bộ điểm số của tổ vĩnh viễn. Hãy kiểm
              tra thật kỹ trước khi xác nhận!
            </span>
          </div>

          {/* Danh sách chọn Tổ */}
          <div className="space-y-2 max-h-60 overflow-y-auto pr-1 no-scrollbar">
            {groups.map((group) => {
              const isSelected = groupToDelete === group.id;
              return (
                <label
                  key={group.id}
                  className={`flex items-center gap-3 p-3.5 rounded-[var(--r-md)] border cursor-pointer transition-all duration-200 ${
                    isSelected
                      ? "border-[var(--red-border)] bg-[var(--red-fill)] shadow-[var(--shadow-sm)]"
                      : "border-[var(--rule)] bg-[var(--bg-surface)] hover:bg-[var(--bg-surface-2)]"
                  }`}
                >
                  {/* Dùng accent-color cho radio button để match với màu Danger */}
                  <input
                    type="radio"
                    name="groupDelete"
                    checked={isSelected}
                    onChange={() => setGroupToDelete(group.id)}
                    className="w-4 h-4 accent-[var(--red-text)] cursor-pointer"
                  />
                  <span
                    className={`text-sm font-bold ${isSelected ? "text-[var(--red-text)]" : "text-[var(--ink-1)]"}`}
                  >
                    {group.name}
                  </span>
                </label>
              );
            })}
          </div>

          {/* Cụm nút bấm */}
          <div className="flex gap-3 pt-2">
            {/* Nút Hủy - Dùng Surface & Ink */}
            <button
              onClick={() => {
                setShowDeleteModal(false);
                setGroupToDelete(null);
              }}
              className="flex-1 py-2.5 bg-[var(--bg-surface-2)] text-[var(--ink-2)] border border-[var(--rule)] rounded-[var(--r-md)] font-bold text-[11px] tracking-[0.05em] hover:bg-[var(--bg-surface-3)] hover:text-[var(--ink-1)] transition-colors"
            >
              HỦY BỎ
            </button>

            {/* Nút Xóa - Dùng màu Red Text làm background cho nổi bật (vì globals.css không có red-button) */}
            <button
              disabled={!groupToDelete}
              onClick={async () => {
                if (!groupToDelete) return;
                const isSuccess = await removeGroup(groupToDelete);
                if (isSuccess) {
                  setShowDeleteModal(false);
                  setGroupToDelete(null);
                  if (selectedTeam === groupToDelete) {
                    setSelectedTeam(groups[0]?.id || 1);
                  }
                }
              }}
              className="flex-1 py-2.5 bg-[var(--red-text)] text-white rounded-[var(--r-md)] font-bold text-[11px] tracking-[0.05em] shadow-[var(--shadow-sm)] hover:opacity-90 transition-all active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed"
            >
              XÁC NHẬN XÓA
            </button>
          </div>
        </div>
      </Modal>
    </div>
  );
};
