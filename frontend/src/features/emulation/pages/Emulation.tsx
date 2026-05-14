import { useState } from "react";
import { Plus, Minus } from "lucide-react";
import { useParams } from "react-router-dom";
import { useEmulation } from "@features/emulation/hooks/useEmulation";
import { RankingTable } from "@features/emulation/pages/RankingTable";
import { HistoryTable } from "@features/emulation/pages/HistoryTable";
import { emulationAPI } from "@features/emulation/api";
import { Modal } from "@shared/components/ui/Modal";

export const Emulation = () => {
  const { classId } = useParams<{ classId: string }>();
  const {
    data,
    isLoading,
    filters,
    weeks,
    setFilters,
    changeTeamCount,
    refresh,
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

  const canEdit = true; // TODO: Phân quyền sau
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
    const pointsToSubmit = Number(pointForm.points);
    if (!pointForm.content) return alert("Vui lòng nhập nội dung!");
    if (isNaN(pointsToSubmit) || pointsToSubmit === 0) {
      return alert("Số điểm không hợp lệ!");
    }

    try {
      await emulationAPI.addPoints(
        classId!,
        selectedTeam,
        pointForm.content,
        pointsToSubmit,
      );
      alert(`Đã cập nhật điểm cho Tổ ${selectedTeam}`);
      setShowPointModal(false);
      setPointForm({ content: "", points: 0 }); // Reset form
      refresh(); // Load lại lịch sử và bảng xếp hạng
    } catch (error) {
      console.error("Lỗi nhập điểm", error);
      alert("Lỗi nhập điểm");
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
        {/* DANH SÁCH TỔ - Dạng Sidebar Menu */}
        <div className="lg:col-span-1 space-y-4">
          <div className="flex items-center justify-between px-2">
            <label className="text-[10px] font-black text-[var(--ink-3)] uppercase tracking-[0.2em]">
              Lựa chọn tổ
            </label>
            {canEdit && (
              <div className="flex gap-1 bg-[var(--bg-surface-2)] p-1 rounded-lg border border-[var(--rule)]">
                <button
                  onClick={() => changeTeamCount(data.teamCount - 1)}
                  className="p-1 hover:text-[var(--red-text)] transition-colors"
                >
                  <Minus size={10} />
                </button>
                <div className="w-[1px] bg-[var(--rule)] mx-0.5" />
                <button
                  onClick={() => changeTeamCount(data.teamCount + 1)}
                  className="p-1 hover:text-[var(--green-text)] transition-colors"
                >
                  <Plus size={10} />
                </button>
              </div>
            )}
          </div>

          <nav className="space-y-1">
            {Array.from({ length: data.teamCount }).map((_, i) => (
              <button
                key={i}
                onClick={() => setSelectedTeam(i + 1)}
                className={`w-full flex items-center justify-between px-4 py-3 rounded-xl transition-all duration-200 group ${
                  selectedTeam === i + 1
                    ? "bg-[var(--warm-fill)] text-[var(--warm-text)] shadow-sm border border-[var(--warm-border)]"
                    : "hover:bg-[var(--bg-surface-2)] text-[var(--ink-2)] border border-transparent"
                }`}
              >
                <div className="flex items-center gap-3">
                  <span
                    className={`w-1.5 h-1.5 rounded-full transition-all ${
                      selectedTeam === i + 1
                        ? "bg-[var(--warm-400)] scale-125"
                        : "bg-[var(--ink-4)] group-hover:bg-[var(--ink-3)]"
                    }`}
                  />
                  <span className="text-sm font-bold">Tổ {i + 1}</span>
                </div>
                {selectedTeam === i + 1 && (
                  <div className="w-1.5 h-1.5 rounded-full bg-[var(--warm-400)] animate-pulse" />
                )}
              </button>
            ))}
          </nav>
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
              onClick={() => {
                setShowPointModal(false);
                setPointForm({ content: "", points: 0 });
              }}
              className="flex-1 py-2.5 bg-[var(--bg-surface-3)] text-[var(--ink-2)] rounded-[var(--r-lg)] font-bold text-xs hover:bg-[var(--ink-4)] transition-colors"
            >
              HỦY BỎ
            </button>
            <button
              onClick={handleSubmitPoint}
              className="flex-1 py-2.5 bg-[var(--warm-400)] text-white rounded-[var(--r-lg)] font-bold text-xs shadow-md shadow-blue-900/10 hover:bg-[var(--warm-600)] transition-all active:scale-95"
            >
              XÁC NHẬN GHI
            </button>
          </div>
        </div>
      </Modal>
    </div>
  );
};
