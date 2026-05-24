import { useState } from "react";
import { useParams } from "react-router-dom";
import { useEmulation } from "@features/emulation/hooks/useEmulation";
import { RankingTable } from "@features/emulation/pages/RankingTable";
import { HistoryTable } from "@features/emulation/pages/HistoryTable";
import { GroupSidebar } from "@features/emulation/pages/GroupSidebar";
import { Modal } from "@shared/components/ui/Modal";
import type { WeekItem } from "@features/emulation/types";
import { useToastStore } from "@app/store";
import { ToastType } from "@shared/domain/enums";

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

  // State quản lý xóa tổ
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [groupToDelete, setGroupToDelete] = useState<number | null>(null);
  const showToast = useToastStore((state) => state.showToast);

  // Early Return kiểm tra đang tải dữ liệu
  if (isLoading || !data) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[400px] space-y-4">
        <div className="animate-in fade-in duration-300 flex flex-col items-center space-y-4">
          <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-[var(--warm-400)]"></div>
          <p className="text-ink-2 text-sm font-medium">Đang tải thi đua...</p>
        </div>
      </div>
    );
  }

  // Hàm xử lý ghi điểm
  const handleSubmitPoint = async () => {
    const pointsToSubmit = Number(pointForm.points);
    if (!pointForm.content.trim()) {
      showToast("Vui lòng nhập nội dung!", ToastType.WARNING);
      return;
    }
    if (isNaN(pointsToSubmit) || pointsToSubmit === 0) {
      showToast("Số điểm không hợp lệ!", ToastType.ERROR);
      return;
    }

    try {
      setIsSubmitting(true);

      const result = await addPoint(
        selectedTeam,
        pointForm.content,
        pointsToSubmit,
      );

      if (result.success) {
        setShowPointModal(false);
        setPointForm({ content: "", points: 0 });
        showToast(
          `Đã ghi ${pointsToSubmit} điểm cho Tổ ${selectedTeam}!`,
          ToastType.SUCCESS,
        );
      } else {
        showToast("Ghi điểm thất bại. Vui lòng thử lại!", ToastType.ERROR);
      }
    } catch (error) {
      console.error("Lỗi nhập điểm:", error);
      showToast("Đã có lỗi xảy ra khi ghi điểm!", ToastType.ERROR);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="space-y-6 animate-fade-in pb-10">
      {/* THANH BỘ LỌC VÀ TIỆN ÍCH */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center py-2 px-1 gap-4">
        {/* Cụm chọn tuần học */}
        <div className="flex items-center gap-3 bg-[var(--bg-surface)] p-2 px-4 rounded-xl border border-[var(--rule)] shadow-2xs">
          <span className="text-[10px] text-[var(--ink-3)] font-black uppercase tracking-widest">
            Tuần:
          </span>
          <select
            className="text-xs font-bold bg-transparent text-[var(--ink-1)] border-none p-0 cursor-pointer outline-none focus:ring-0"
            value={filters.startDate}
            onChange={(e: React.ChangeEvent<HTMLSelectElement>) => {
              const selected = (weeks as WeekItem[]).find(
                (w) => w.start_at === e.target.value,
              );
              if (selected) {
                setFilters({
                  startDate: selected.start_at,
                  endDate: selected.end_at,
                });
              }
            }}
          >
            {(weeks as WeekItem[]).map((w) => (
              <option
                key={w.week}
                value={w.start_at}
                className="bg-[var(--bg-surface)] text-[var(--ink-1)]"
              >
                Tuần {w.week} ({w.formatted_start_at} - {w.formatted_end_at})
                {w.is_current_week ? " • Hiện tại" : ""}
              </option>
            ))}
          </select>
        </div>

        {/* NÚT NỘI QUY */}
        <button className="bg-[var(--sage-fill)] text-[var(--sage-text)] border border-[var(--sage-border)] px-4 py-1.5 rounded-full font-bold text-[10px] uppercase tracking-wider hover:opacity-90 transition-all active:scale-95 shadow-2xs">
          Nội quy thi đua
        </button>
      </div>

      {/* KHU VỰC SIDEBAR VÀ LỊCH SỬ BẢNG ĐIỂM */}
      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6 items-start">
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

        <div className="lg:col-span-3">
          <HistoryTable
            selectedTeam={selectedTeam}
            history={data.history}
            canEdit={canEdit}
            onOpenPointModal={() => setShowPointModal(true)}
          />
        </div>
      </div>

      {/* KHU VỰC BẢNG XẾP HẠNG THI ĐUA */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <RankingTable title="Xếp hạng tuần" rows={data.weeklyRanking} />
        <RankingTable title="Xếp hạng tháng" rows={data.monthlyRanking} />
      </div>

      {/* MODAL GHI ĐIỂM */}
      <Modal
        isOpen={showPointModal}
        onClose={() => {
          setShowPointModal(false);
          setPointForm({ content: "", points: 0 });
        }}
        title={`Ghi điểm Tổ ${selectedTeam}`}
      >
        <div className="space-y-5 p-1">
          <div>
            <label className="text-[10px] font-black text-[var(--ink-3)] uppercase tracking-widest mb-1.5 block">
              Nội dung vi phạm / Khen thưởng
            </label>
            <input
              type="text"
              className="w-full bg-[var(--bg-surface-2)] border border-[var(--rule-md)] rounded-lg p-3 text-sm text-[var(--ink-1)] placeholder:text-[var(--ink-3)] focus:border-[var(--warm-400)] outline-none transition-all"
              placeholder="Ví dụ: Hăng hái xây dựng bài..."
              value={pointForm.content}
              onChange={(e) =>
                setPointForm({ ...pointForm, content: e.target.value })
              }
            />
          </div>

          <div>
            <label className="text-[10px] font-black text-[var(--ink-3)] uppercase tracking-widest mb-1.5 block">
              Số điểm (+ cộng, - trừ)
            </label>
            <div className="relative">
              <input
                type="text"
                className="w-full bg-[var(--bg-surface-2)] border border-[var(--rule-md)] rounded-lg p-3 pl-4 text-sm font-bold text-[var(--ink-1)] placeholder:text-[var(--ink-3)] focus:border-[var(--warm-400)] outline-none transition-all"
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
              <div className="absolute right-3 top-1/2 -translate-y-1/2 text-[9px] font-black text-[var(--ink-3)] uppercase">
                Points
              </div>
            </div>
          </div>

          <div className="flex gap-3 pt-2">
            <button
              disabled={isSubmitting}
              onClick={() => {
                setShowPointModal(false);
                setPointForm({ content: "", points: 0 });
              }}
              className="flex-1 py-2.5 bg-[var(--bg-surface-3)] text-[var(--ink-2)] rounded-lg font-bold text-xs hover:bg-[var(--ink-4)] hover:text-[var(--ink-1)] transition-colors"
            >
              HỦY BỎ
            </button>
            <button
              disabled={isSubmitting}
              onClick={handleSubmitPoint}
              className="flex-1 py-2.5 bg-[var(--warm-400)] hover:bg-[var(--warm-600)] text-white rounded-lg font-bold text-xs shadow-xs transition-all active:scale-95"
            >
              XÁC NHẬN GHI
            </button>
          </div>
        </div>
      </Modal>

      {/* MODAL XÓA TỔ THI ĐUA */}
      <Modal
        isOpen={showDeleteModal}
        onClose={() => {
          setShowDeleteModal(false);
          setGroupToDelete(null);
        }}
        title="Xóa Tổ Chỉ Định"
      >
        <div className="space-y-5 p-1">
          <div className="bg-[var(--red-fill)] text-[var(--red-text)] text-xs p-4 rounded-xl border border-[var(--red-border)] font-semibold flex gap-2.5 leading-relaxed">
            <span className="flex-shrink-0 text-sm">⚠️</span>
            <span>
              Hành động này sẽ xóa toàn bộ điểm số của tổ vĩnh viễn. Hãy kiểm
              tra thật kỹ trước khi xác nhận!
            </span>
          </div>

          <div className="space-y-2 max-h-60 overflow-y-auto pr-1 no-scrollbar">
            {groups.map((group) => {
              const isSelected = groupToDelete === group.id;
              return (
                <label
                  key={group.id}
                  className={`flex items-center gap-3 p-3 rounded-xl border cursor-pointer transition-all duration-150 ${
                    isSelected
                      ? "border-[var(--red-border)] bg-[var(--red-fill)] shadow-xs"
                      : "border-[var(--rule)] bg-surface hover:bg-[var(--bg-surface-2)]"
                  }`}
                >
                  <input
                    type="radio"
                    name="groupDelete"
                    checked={isSelected}
                    onChange={() => setGroupToDelete(group.id)}
                    className="w-4 h-4 accent-[var(--red-text)] cursor-pointer"
                  />
                  <span
                    className={`text-xs font-bold ${isSelected ? "text-[var(--red-text)]" : "text-[var(--ink-1)]"}`}
                  >
                    {group.name}
                  </span>
                </label>
              );
            })}
          </div>

          <div className="flex gap-3 pt-2">
            <button
              onClick={() => {
                setShowDeleteModal(false);
                setGroupToDelete(null);
              }}
              className="flex-1 py-2.5 bg-[var(--bg-surface-2)] text-[var(--ink-2)] border border-[var(--rule)] rounded-xl font-bold text-xs hover:bg-[var(--bg-surface-3)] hover:text-[var(--ink-1)] transition-colors"
            >
              HỦY BỎ
            </button>
            <button
              disabled={!groupToDelete}
              // Trong Modal Xóa Tổ, tại nút "XÁC NHẬN XÓA":
              onClick={async () => {
                if (!groupToDelete) return;

                const showToast = useToastStore.getState().showToast;
                const isSuccess = await removeGroup(groupToDelete);

                if (isSuccess) {
                  showToast("Đã xóa tổ thành công!", ToastType.SUCCESS);
                  setShowDeleteModal(false);
                  setGroupToDelete(null);
                  if (selectedTeam === groupToDelete) {
                    setSelectedTeam(groups[0]?.id || 1);
                  }
                } else {
                  showToast(
                    "Xóa tổ thất bại, vui lòng thử lại.",
                    ToastType.ERROR,
                  );
                }
              }}
              className="flex-1 py-2.5 bg-[var(--red-text)] text-white rounded-xl font-bold text-xs shadow-xs hover:opacity-90 transition-all active:scale-95 disabled:opacity-40 disabled:cursor-not-allowed"
            >
              XÁC NHẬN XÓA
            </button>
          </div>
        </div>
      </Modal>
    </div>
  );
};
